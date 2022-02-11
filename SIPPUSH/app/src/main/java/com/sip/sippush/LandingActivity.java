package com.sip.sippush;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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

public class LandingActivity extends AppCompatActivity {


    Button btn_push_sip, btn_push_contact,btn_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_main);

        btn_push_sip = (Button) findViewById(R.id.btn_push_sip);
        btn_push_contact = (Button) findViewById(R.id.btn_push_contact);
        btn_notification = (Button) findViewById(R.id.btn_notification);

        btn_push_sip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inti = new Intent(LandingActivity.this, MainActivity.class);
                startActivity(inti);
            }
        });
        btn_push_contact.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {


                Intent inti = new Intent(LandingActivity.this, ContactActivity.class);
                startActivity(inti);
            }
        });

        btn_notification.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                showNotification();

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("rv_channel", "ReservedValuesSync", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "rv_channel")
                        .setSmallIcon(R.drawable.ic_telephone_directory)
                        .setContentTitle("2001")
                        .setContentText("Bakery")
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setOngoing(true)

                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        notificationManager.notify(123, notificationBuilder.build());






        NotificationManager notificationManager1 = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel1 = new NotificationChannel("channel", "Sync", NotificationManager.IMPORTANCE_HIGH);
            notificationManager1.createNotificationChannel(mChannel1);
        }

        NotificationCompat.Builder notificationBuilder1 =
                new NotificationCompat.Builder(getApplicationContext(), "channel")
                        .setSmallIcon(R.drawable.ic_notification_message)
                        //.setContentTitle("2001")
                        .setContentText("New Message")
                        .setPriority(Notification.PRIORITY_DEFAULT)

                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        notificationManager1.notify(1234, notificationBuilder1.build());


    }
}