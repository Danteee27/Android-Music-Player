package com.example.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.myapplication.activity.InsertCategoryActivity
import com.example.myapplication.activity.InsertSongActivity
import com.example.myapplication.activity.TestGetDataActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.content.Intent as Intent


class MainActivity : AppCompatActivity() {

    lateinit var btnInsertSong: Button
    lateinit var btnInsertCategory: Button
    lateinit var btnTest: Button
    lateinit var songNameEdt: EditText

    val db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnInsertSong = findViewById<Button>(R.id.insertButtonSong)
        btnInsertCategory = findViewById<Button>(R.id.insertButtonCategory)
        btnTest = findViewById<Button>(R.id.buttonTest)

        btnInsertSong.setOnClickListener {
            val intent = Intent(this, InsertSongActivity::class.java)
            startActivity(intent)
        }

        btnInsertCategory.setOnClickListener {
            val intent = Intent(this, InsertCategoryActivity::class.java)
            startActivity(intent)
        }

        btnTest.setOnClickListener {
            val intent = Intent(this, TestGetDataActivity::class.java)
            startActivity(intent)
        }
    }
}