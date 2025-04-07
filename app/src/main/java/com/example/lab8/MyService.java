package com.example.lab8;

import android.annotation.SuppressLint;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.IBinder;
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


    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Запуск сервиса");

        try {
            createNotificationChannel();
            startForeground(NOTIFICATION_ID, buildNotification());

            Toast.makeText(this, "Сервер запущен", Toast.LENGTH_SHORT).show();

            if (toneGenerator != null && !isPlaying) {
                isPlaying = true;
                Log.d(TAG, "onStartCommand: Запуск воспроизведения звука");

                new Thread(() -> {
                    while (isPlaying) {
                        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);
                        try {
                            Thread.sleep(1100);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "onStartCommand: Поток прерван", e);
                            break;
                        }
                    }
                }).start();
            } else {
                Log.w(TAG, "onStartCommand: ToneGenerator не готов или уже воспроизводит звук");
            }

        } catch (Exception e) {
            Log.e(TAG, "onStartCommand: Ошибка запуска сервиса", e);
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            stopSelf();
        }

        return START_STICKY;
    }
}