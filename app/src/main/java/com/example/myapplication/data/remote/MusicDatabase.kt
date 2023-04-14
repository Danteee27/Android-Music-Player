package com.example.musicapp.data.remote

import android.util.Log
import com.example.musicapp.data.entities.Song
import com.example.myapplication.other.Constants
import com.example.myapplication.other.Constants.SONG_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object MusicDatabase {

    private var db = Firebase.firestore

    fun getAllSongs() {
            db.collection(Constants.SONG_COLLECTION).get().addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                }
            }
                .addOnFailureListener { e ->
                    Log.d("GETDATA", "Error getting documents: ", e)
                }
    }

}