package com.sip.sippush;

import static android.media.AudioManager.RINGER_MODE_VIBRATE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

public class MainActivity extends AppCompatActivity {

    EditText editusername, editdomain, editpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

     //     checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
//
//           <uses-permission android:name="android.permission.USE_SIP" />
//    <uses-permission android:name="android.permission.INTERNET" />
//
//    <uses-feature android:name="android.software.sip.voip" android:required="true" />
//    <uses-feature android:name="android.hardware.wifi" android:required="true" />
//    <uses-feature android:name="android.hardware.microphone" android:required="true" />

        requestPermissions(new String[]{Manifest.permission.USE_SIP,Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_SETTINGS},
                123);

        TextView buttonClick = (TextView) findViewById(R.id.btn);
        Button unregister = (Button) findViewById(R.id.button);
        Button btn_list = (Button) findViewById(R.id.btn_list);

        editusername = (EditText) findViewById(R.id.editusername);
        editdomain = (EditText) findViewById(R.id.editdomain);
        editpassword = (EditText) findViewById(R.id.editpassword);


        buttonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                push();

                // only Vivrate while incomming call
//                AudioManager am =(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                //
//                AudioManager  audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                audiomanager.set
               // audiomanager.setRingerMode(AudioManager.VIBRATE_TYPE_RINGER);
               // audiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);


            }
        });


        unregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeLocalProfile();
            }
        });

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list();
            }
        });

    }

    SipManager mSipManager = null;

    SipProfile mSipProfile = null;

    public void push() {


        if (mSipManager == null) {
            mSipManager = SipManager.newInstance(this);
        }


        SipProfile mSipProfile = null;
        SipManager manager = SipManager.newInstance(getBaseContext());

        SipProfile.Builder builder;
        try {
            builder = new SipProfile.Builder(editusername.getText().toString(), editdomain.getText().toString());
            builder.setPassword(editpassword.getText().toString());
           // builder.setProtocol()
            mSipProfile = builder.build();
            manager.open(mSipProfile);

            // manager.register(mSipProfile, 30, MyActivity.this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            manager.setRegistrationListener(mSipProfile.getUriString(), new SipRegistrationListener() {
                public void onRegistering(String localProfileUri) {
                    // updateStatus("Registering with SIP Server...");
                    Log.v("Registering with SIP Server...", "Registering with SIP Server...");
                    Toast.makeText(getApplicationContext(), "Registering with SIP Server...", Toast.LENGTH_LONG).show();

                }

                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    // updateStatus("Ready");
                    Log.v("Ready", "Ready");
                    Toast.makeText(getApplicationContext(), "Ready...", Toast.LENGTH_LONG).show();
                }

                public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                 String errorMessage) {
                    // updateStatus("Registration failed.  Please check settings.");
                    Log.v("Registration failed.  Please check settings.", "Registration failed.  Please check settings.");
                    Toast.makeText(getApplicationContext(), "Registration failed.  Please check settings....", Toast.LENGTH_LONG).show();
                }
            });
        } catch (SipException e) {
            e.printStackTrace();
        }


    }


    public void closeLocalProfile() {
        if (mSipManager == null) {
            return;
        }
        try {
            if (mSipManager != null) {

                SipProfile mSipProfile = null;
                SipManager manager = SipManager.newInstance(getBaseContext());

                SipProfile.Builder builder;
                try {
                    builder = new SipProfile.Builder("rajdeep", "sip.linphone.org");
                    builder.setPassword("XXX");
                    mSipProfile = builder.build();
                    mSipManager.close(mSipProfile.getUriString());

                    // manager.register(mSipProfile, 30, MyActivity.this);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        } catch (Exception ee) {
            Log.d("WalkieTalkieActivity/onDestroy", "Failed to close local profile.", ee);
        }
    }

    public void list() {


        Uri uri = ContactsContract.Contacts.CONTENT_URI.buildUpon()
                .appendQueryParameter(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX, "true")
                .build();
        Cursor cursor = getContentResolver().query(uri,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                null, null, null);
        Bundle bundle = cursor.getExtras();
        if (bundle.containsKey(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX_TITLES) &&
                bundle.containsKey(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX_COUNTS)) {
            String sections[] =
                    bundle.getStringArray(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX_TITLES);
            int counts[] = bundle.getIntArray(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX_COUNTS);
        }
    }


    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }


    public void download(){

        File file = new File(Environment.getExternalStorageDirectory() + "/" + File.separator + "Kalimba.mp3");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int downloadId = PRDownloader.download("https://dl.espressif.com/dl/audio/ff-16b-2c-44100hz.mp3",
                Environment.getExternalStorageDirectory() + "/" + File.separator , "Kalimba.mp3")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                    Log.v("","");
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        Log.v("","");
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        Log.v("","");
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        Log.v("","");
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.v("","");

                        SetAsRingtoneOrNotification(new File(Environment.getExternalStorageDirectory() + "/" + "Kalimba.mp3"), RingtoneManager.TYPE_RINGTONE);
                    }

                    @Override
                    public void onError(Error error) {
                        Log.v("","");
                    }


                });

    }



    private boolean SetAsRingtoneOrNotification(File k, int type) {


        ContentValues values = new ContentValues();

        values.put(MediaStore.MediaColumns.TITLE, k.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        if (RingtoneManager.TYPE_RINGTONE == type) {
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        } else if (RingtoneManager.TYPE_NOTIFICATION == type) {
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        }


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri newUri = this.getContentResolver()
                    .insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            try (OutputStream os = getContentResolver().openOutputStream(newUri)) {

                int size = (int) k.length();
                byte[] bytes = new byte[size];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(k));
                    buf.read(bytes, 0, bytes.length);
                    buf.close();

                    os.write(bytes);
                    os.close();
                    os.flush();
                } catch (IOException e) {
                    return false;
                }
            } catch (Exception ignored) {
                return false;
            }
            RingtoneManager.setActualDefaultRingtoneUri(this, type,
                    newUri);

            return true;
        } else {
            values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(k
                    .getAbsolutePath());

            getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + k.getAbsolutePath() + "\"", null);


            Uri newUri = this.getContentResolver().insert(uri, values);
            RingtoneManager.setActualDefaultRingtoneUri(this, type,
                    newUri);

            this.getContentResolver()
                    .insert(MediaStore.Audio.Media.getContentUriForPath(k
                            .getAbsolutePath()), values);

            return true;
        }



    }


//    private void setRingtone(String path) {
//        Context context = this.getApplicationContext();
//
//        if (path == null) {
//            return;
//        }
//        File file = new File(path);
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.MediaColumns.DATA, path);
//        String filterName = path.substring(path.lastIndexOf("/") + 1);
//        contentValues.put(MediaStore.MediaColumns.TITLE, filterName);
//        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
//        contentValues.put(MediaStore.MediaColumns.SIZE, file.length());
//        contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
//        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
//        Cursor cursor = context.getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
//        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
//            String id = cursor.getString(0);
//            contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
//            context.getContentResolver().update(uri, contentValues, MediaStore.MediaColumns.DATA + "=?", new String[]{path});
//            Uri newuri = ContentUris.withAppendedId(uri, Long.valueOf(id));
//            try {
//                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newuri);
//                Toast.makeText(context, "Set as Ringtone Successfully.", Toast.LENGTH_SHORT).show();
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//            cursor.close();
//        }
//    }

    private void openAndroidPermissionsMenu() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        startActivity(intent);
    }

}