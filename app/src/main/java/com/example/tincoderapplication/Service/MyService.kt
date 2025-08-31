package com.example.tincoderapplication.Service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.tincoderapplication.R
import com.example.tincoderapplication.Service.MyApplication.Companion.CHANNEL_ID

class MyService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying: Boolean = false
    private var song: Song? = null

    companion object {
        const val ACTION_PAUSE = 1
        const val ACTION_RESUME = 2
        const val ACTION_CLEAR = 3
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private fun getPendingIntent(
        context: Context,
        action: Int
    ): PendingIntent {
        val intent = Intent(this, BroadcastReceiverMusic::class.java)
        intent.putExtra("action", action)
        return PendingIntent.getBroadcast(context, action, intent, PendingIntent.FLAG_IMMUTABLE)

    }

    private fun handleAction(action: Int) {
        when (action) {
            1 -> pauseMusic()
            2 -> resumeMusic()
            3 -> clearMusic()
        }
    }

    private fun clearMusic() {
        mediaPlayer?.run { stop(); release() }
        mediaPlayer = null
        isPlaying = false

        // GỠ notification foreground, rồi dừng service
        stopForeground(true)
        stopSelf()

    }

    private fun resumeMusic() {
       if(!isPlaying){
           mediaPlayer?.start()
           isPlaying = true
           song?.let { sendNotificationMusic(it) }
       }
    }

    private fun pauseMusic() {
        if(isPlaying)
        {
            mediaPlayer?.pause()
            isPlaying = false
            song?.let { sendNotificationMusic(it) }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val newSong = intent?.getSerializableExtra("newSong", Song::class.java)
        val action = intent?.getIntExtra("action", 0) ?: 0

        if (newSong != null) {
            song = newSong
            startMusic(newSong)
            sendNotificationMusic(newSong)

        }
        if (action != 0) {
            handleAction(action)
        }
        return START_NOT_STICKY
    }


    private fun startMusic(newSong: Song) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(applicationContext, newSong.resource);

        }
        mediaPlayer?.start();
        isPlaying = true
    }

    private fun sendNotificationMusic(newSong: Song) {
        val bitmap = BitmapFactory.decodeResource(resources, newSong.imgSong)
        val notificationLayout = RemoteViews(packageName, R.layout.custom_music_song)
        notificationLayout.setImageViewBitmap(R.id.img_song, bitmap)
        notificationLayout.setTextViewText(R.id.singer, newSong.titleSinger)
        notificationLayout.setTextViewText(R.id.titleSong, newSong.titleSong)
        notificationLayout.setImageViewResource(R.id.playOrPauseMusic, R.drawable.pause_circle_24px)
        if (isPlaying) {
            notificationLayout.setImageViewResource(
                R.id.playOrPauseMusic,
                R.drawable.pause_circle_24px
            )
            notificationLayout.setOnClickPendingIntent(
                R.id.playOrPauseMusic,
                getPendingIntent(this, ACTION_PAUSE)
            )

        } else {
            notificationLayout.setImageViewResource(
                R.id.playOrPauseMusic,
                R.drawable.play_circle_24px
            )
            notificationLayout.setOnClickPendingIntent(
                R.id.playOrPauseMusic,
                getPendingIntent(this, ACTION_RESUME)
            )

        }
        notificationLayout.setOnClickPendingIntent(
            R.id.clearMusic,
            getPendingIntent(this, ACTION_CLEAR)
        )
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MusicSongActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.mark_unread_chat_alt_24px)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setContentIntent(pendingIntent)
            .build()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(1, notification)
        } else {
            startForeground(
                1, notification,
                FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release();
        mediaPlayer = null
    }
}