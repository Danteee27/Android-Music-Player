package com.example.myapplication.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.musicapp.data.remote.MusicDatabase
import com.example.myapplication.R
@SuppressLint("MissingInflatedId")
class TestGetDataActivity : AppCompatActivity() {

    lateinit var btnGetAllSongs:Button
    lateinit var btnGetAllAlbums:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_get_data)

        btnGetAllSongs = findViewById<Button>(R.id.buttonGetAllSongs)

        btnGetAllSongs.setOnClickListener {
            MusicDatabase.getAllSongs()
        }
    }
}