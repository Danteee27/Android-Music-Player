package com.example.myapplication.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.musicapp.data.entities.Category
import com.example.myapplication.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class InsertCategoryActivity : AppCompatActivity() {

    lateinit var edtId: EditText
    lateinit var edtName: EditText
    lateinit var edtImage: EditText
    lateinit var edtPublisher: EditText
    lateinit var edtTimestamp: EditText
    lateinit var edtInterestedCount: EditText
    lateinit var edtSongsCount: EditText
    lateinit var edtAlbumsCount: EditText

    lateinit var btnInsert: Button

    var db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_category)


        edtId = findViewById<EditText>(R.id.insertCategory_editTextIdCategory)
        edtName = findViewById<EditText>(R.id.insertCategory_editTextNameCategory)
        edtImage = findViewById<EditText>(R.id.insertCategory_editTextImageUrlCategory)
        edtPublisher = findViewById<EditText>(R.id.insertCategory_editTextPublisherCategory)
        edtTimestamp = findViewById<EditText>(R.id.insertCategory_editTextTimestampCategory)
        edtInterestedCount =
            findViewById<EditText>(R.id.insertCategory_editTextInterestedCountCategory)
        edtSongsCount = findViewById<EditText>(R.id.insertCategory_editTextSongsCountCategory)
        edtAlbumsCount = findViewById<EditText>(R.id.insertCategory_editTextAlbumsCountCategory)

        btnInsert = findViewById<Button>(R.id.insertCategory_buttonInsert)

        //id: String?,
        //name: String?,
        //image: String?,
        //publisher: String?,
        //timestamp: Long,
        //interestedCount: Int,
        //songsCount: Int,
        //albumsCount: Int

        btnInsert.setOnClickListener {
            val id = edtId.text.toString().trim()
            val name = edtName.text.toString().trim()
            val image = edtImage.text.toString().trim()
            val publisher = edtPublisher.text.toString().trim()
            val timestamp = edtTimestamp.text.toString().trim().toLongOrNull()
            val interestedCount = edtInterestedCount.text.toString().trim().toIntOrNull()
            val songsCount = edtSongsCount.text.toString().trim().toIntOrNull()
            val albumsCount = edtAlbumsCount.text.toString().trim().toIntOrNull()


            val category = Category(
                id, name, image, publisher, timestamp ?: 0,
                interestedCount ?: 0, songsCount ?: 0, albumsCount ?: 0
            )

            db.collection("albums").add(category)
                .addOnSuccessListener {
                    Log.d("TAG_INSERT", "DocumentSnapshot added with ID ${category.id}")
                    Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                    Toast.makeText(this, "failed", Toast.LENGTH_LONG).show()

                }
        }



    }
}