package com.example.lab8;


import android.app.NotificationManager;
import android.app.Service;

import android.media.AudioManager;
import android.media.ToneGenerator;

import android.util.Log;
import android.widget.Toast;



public class MyService extends Service {

    private static final String TAG = "MyService";
    private static final String CHANNEL_ID = "channelId";
    private static final int NOTIFICATION_ID = 1;

    private ToneGenerator toneGenerator;
    private NotificationManager notificationManager;
    private boolean isPlaying = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Создание сервиса");

        try {
            toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            Log.d(TAG, "onCreate: ToneGenerator создан успешно");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Ошибка инициализации ToneGenerator", e);
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    }