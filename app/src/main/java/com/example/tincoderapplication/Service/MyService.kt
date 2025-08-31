package com.example.tincoderapplication.Service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.tincoderapplication.MainActivity
import com.example.tincoderapplication.R
import com.example.tincoderapplication.Service.MyApplication.Companion.CHANNEL_ID

class MyService : Service() {
    private var mediaPlayer: MediaPlayer?=null
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sendNotificationMusic(intent)
        return START_NOT_STICKY
    }

    private fun sendNotificationMusic(intent: Intent?) {
        val newSong=intent?.getSerializableExtra("newSong") as Song
        val bitmap= BitmapFactory.decodeResource(resources, newSong.imgSong)
        val notificationLayout = RemoteViews(packageName, R.layout.custom_music_song)
        notificationLayout.setImageViewBitmap(R.id.img_song,bitmap)
        notificationLayout.setTextViewText(R.id.singer,newSong.titleSinger)
        notificationLayout.setTextViewText(R.id.titleSong,newSong.titleSong)
        val pendingIntent= PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_IMMUTABLE)
        mediaPlayer = MediaPlayer.create(applicationContext, newSong.resource);
        mediaPlayer?.start();
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.mark_unread_chat_alt_24px)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setContentIntent(pendingIntent)
            .build()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(1,notification)
        } else {
            startForeground(1, notification,
                FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        }
    }

    override fun onDestroy() {
            super.onDestroy()
        mediaPlayer?.stop();
        mediaPlayer?.release();
    }
}