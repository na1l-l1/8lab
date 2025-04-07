package com.example.lab8;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Канал сервиса";
            String channelDescription = "Музыкальный канал";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "createNotificationChannel: Канал уведомлений создан");
            } else {
                Log.w(TAG, "createNotificationChannel: NotificationManager не доступен");
            }
        }
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle("Мой музыкальный плеер")
                .setContentText("Проигрывается звук")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Остановка сервиса");

        isPlaying = false;

        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
            Log.d(TAG, "onDestroy: ToneGenerator освобожден");
        }

        Toast.makeText(this, "Сервер остановлен", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
