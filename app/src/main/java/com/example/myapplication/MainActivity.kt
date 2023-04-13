package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.musicapp.data.entities.Song
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.content.Intent as Intent


class MainActivity : AppCompatActivity() {

    lateinit var insertBtn:Button
    lateinit var songNameEdt:EditText

    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        insertBtn = findViewById<Button>(R.id.insertButton)

        insertBtn.setOnClickListener {
//            // Create a new user with a first and last name
//            val user = hashMapOf(
//                "first" to "Ada",
//                "last" to "Lovelace",
//                "born" to 1815
//            )
//
////            val song = Song()
//
//            // Add a new document with a generated ID
//            db.collection("users")
//                .add(user)
//                .addOnSuccessListener { documentReference ->
//                    Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
//                    Toast.makeText(this,"success",Toast.LENGTH_LONG).show()
//                }
//                .addOnFailureListener { e ->
//                    Log.w("TAG", "Error adding document", e)
//
//                }

            val intent = Intent(this,InsertSongActivity::class.java)
            startActivity(intent)


        }
    }
}