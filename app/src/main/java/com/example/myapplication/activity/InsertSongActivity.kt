package com.example.myapplication.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.musicapp.data.entities.Song
import com.example.myapplication.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InsertSongActivity : AppCompatActivity() {

    lateinit var edtSongMediaId : EditText
    lateinit var edtSongTitle : EditText
    lateinit var edtSongSubtitle : EditText
    lateinit var edtSongUrl : EditText
    lateinit var edtSongImageUrl : EditText

    lateinit var btnInsert : Button

    var db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_song)


        edtSongMediaId = findViewById<EditText>(R.id.editTextSongId)
        edtSongTitle = findViewById<EditText>(R.id.editTextSongTitle)
        edtSongSubtitle = findViewById<EditText>(R.id.editTextSongSubtitle)
        edtSongUrl = findViewById<EditText>(R.id.editTextSongUrl)
        edtSongImageUrl = findViewById<EditText>(R.id.editTextSongImageUrl)

        btnInsert = findViewById<Button>(R.id.insertSongButton)

        btnInsert.setOnClickListener {

            val songMediaID = edtSongMediaId.text.toString().trim()
            val songTitle = edtSongTitle.text.toString().trim()
            val songSubtitle = edtSongSubtitle.text.toString().trim()
            val songUrl = edtSongUrl.text.toString().trim()
            val songImageUrl = edtSongImageUrl.text.toString().trim()

            val song = Song(songMediaID,"",13,"",songTitle,"","","",songUrl,2,2,2)

            db.collection("songs").document(songMediaID).set(song)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: ${song.id}")
                    Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                    Toast.makeText(this, "failed", Toast.LENGTH_LONG).show()

                }
        }

    }
}
















