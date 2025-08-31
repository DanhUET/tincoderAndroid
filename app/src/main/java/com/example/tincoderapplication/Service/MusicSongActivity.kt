package com.example.tincoderapplication.Service

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tincoderapplication.R


class MusicSongActivity : AppCompatActivity() {
    private var btnStartMusic: Button? = null
    private var btnStopMusic: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_music_song)
        startMusic()
        stopMusic()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun startMusic() {
        btnStartMusic = findViewById(R.id.btnStart)
        val newSong= Song(R.drawable.img_tulaunuocmat,"Mr Siro","Tự lau nươc mắt",R.raw.y1nvt)
        btnStartMusic?.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            intent.putExtra("newSong",newSong)
            startService(intent)
        }
    }

    fun stopMusic() {
        btnStopMusic = findViewById(R.id.btnStop)
        btnStopMusic?.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            stopService(intent)
        }
    }
}