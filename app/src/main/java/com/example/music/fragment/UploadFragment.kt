package com.example.music.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.example.music.MyApplication
import com.example.music.R
import com.example.music.activity.MainActivity
import com.example.music.adapter.GenreAdapter
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.FragmentUploadBinding
import com.example.music.model.Genre
import com.example.music.model.Song
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*

class UploadFragment : Fragment() {
    private var mFragmentUploadBinding: FragmentUploadBinding? = null
    private var REQUEST_CODE = 100
    private var REQUEST_CODE_AUDIO = 200
    private var imageUri: Uri? = null
    private var audioUri: Uri? = null
    private var imageUriFirebase: Uri? = null
    private var audioUriFirebase: Uri? = null
    private var storage = FirebaseStorage.getInstance()
    private var fileName = ""
    private var song = Song()
    private var nextSongId = -1
    private var isFeatured = false
    private var isLatest = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentUploadBinding = FragmentUploadBinding.inflate(inflater, container, false)
        initialComponent()
        return mFragmentUploadBinding!!.root
    }

    private fun initialComponent() {

        initialSpinerGenre()
        mFragmentUploadBinding?.checkBoxFeatured?.setOnCheckedChangeListener  { buttonView, isChecked ->
            if (isChecked) {
                isFeatured = true
                Log.d("khoa","Is featured")
            } else {
                isFeatured = false
                Log.d("khoa","Is not featured")
            }
        }
        mFragmentUploadBinding?.checkBoxLatest?.setOnCheckedChangeListener  { buttonView, isChecked ->
            if (isChecked) {
                isFeatured = true
                Log.d("khoa","Is latest")
            } else {
                isFeatured = false
                Log.d("khoa","Is not latest")
            }
        }
        mFragmentUploadBinding?.tvSelectImage?.setOnClickListener { selectImage() }
        mFragmentUploadBinding?.tvSelectAudio?.setOnClickListener { selectAudio() }
        mFragmentUploadBinding?.tvUpload?.setOnClickListener { onClickUpload() }
    }

    private fun initialSpinerGenre() {
//        mFragmentUploadBinding?.spinnerGenre?.
        if (activity == null) {
            return
        }

        val activity = activity as MainActivity?
        val genreAdapter: GenreAdapter =
            GenreAdapter(requireContext(), R.layout.item_selected_genre, getListGenre())
        mFragmentUploadBinding?.spinnerGenre?.adapter = genreAdapter
        mFragmentUploadBinding?.spinnerGenre?.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                GlobalFuntion.showToastMessage(activity, genreAdapter.getItem(position)?.getName())
                // Do something when an item is selected
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do something when nothing is selected
            }
        })

    }

    private fun getListGenre(): MutableList<Genre> {
        val list: MutableList<Genre> = mutableListOf()
        val pop = Genre()
        val acoustic = Genre()
        val remix = Genre()
        val edm = Genre()
        val nhacTre = Genre()
        val hipHop = Genre()
        val kPop = Genre()
        val rock = Genre()
        val indie = Genre()
        val classical = Genre()
        val ambient = Genre()
        val disco = Genre()
        pop.setName("Pop")
        pop.setColor("greenGenre")
        acoustic.setName("Acoustic")
        acoustic.setColor("blueGenre")
        remix.setName("Remix")
        remix.setColor("orangeGenre")
        edm.setName("EDM")
        edm.setColor("pinkGenre")
        nhacTre.setName("Nhạc trẻ")
        nhacTre.setColor("purpleGenre")
        hipHop.setName("Hip-hop")
        hipHop.setColor("darkPinkGenre")
        kPop.setName("K-pop")
        kPop.setColor("darkRedGenre")
        rock.setName("Rock")
        rock.setColor("lightGreenGenre")
        indie.setName("Indie")
        indie.setColor("lightPurpleGenre")
        classical.setName("Classical")
        classical.setColor("greenGenre")
        ambient.setName("Ambient")
        ambient.setColor("blueGenre")
        disco.setName("Disco")
        disco.setColor("defaultGenre")

        list.add(pop)
        list.add(acoustic)
        list.add(remix)
        list.add(edm)
        list.add(nhacTre)
        list.add(hipHop)
        list.add(kPop)
        list.add(rock)
        list.add(indie)
        list.add(classical)
        list.add(ambient)
        list.add(disco)


        return list
    }


    @SuppressLint("SetTextI18n")
    private fun onClickUpload() {
        if (activity == null) {
            return
        }
        val activity = activity as MainActivity?
        val formatter = SimpleDateFormat("dd_MM_yyyy_hh_mm_ss", Locale.TAIWAN)
        val now = Date()
        fileName = "${mFragmentUploadBinding?.edtTitle?.text}_${formatter.format(now)}"

        // upload image
        val imageStorageRef = storage.getReference("images/$fileName")
        imageStorageRef.putFile(imageUri!!).addOnSuccessListener {
            // handle when upload image success
            mFragmentUploadBinding?.ivImgSong?.setImageURI(null)
            mFragmentUploadBinding?.tvSelectImage?.text = "Select Image"
            GlobalFuntion.showToastMessage(activity, getString(R.string.msg_upload_image_success))
            uploadAudio()
        }.addOnFailureListener {
            GlobalFuntion.showToastMessage(activity, getString(R.string.msg_upload_image_fail))
        }

    }

    private fun uploadAudio() {
        val audioStorageRef = storage.getReference("audios/$fileName")
        audioStorageRef.putFile(audioUri!!).addOnSuccessListener {
            // handle when upload audio success
            mFragmentUploadBinding?.tvSelectAudio?.setText(audioUri.toString())
            mFragmentUploadBinding?.tvSelectAudio?.text = "Select Audio"
            GlobalFuntion.showToastMessage(activity, getString(R.string.msg_upload_audio_success))
            uploadSongData()
        }.addOnFailureListener {
            GlobalFuntion.showToastMessage(activity, getString(R.string.msg_upload_image_fail))
        }
    }

    private fun uploadSongData() {
        // get imageUriFirebase
        val storageRefDownLoadUri = Firebase.storage.reference.child("images/$fileName")
        storageRefDownLoadUri.downloadUrl.addOnSuccessListener { uri ->
            Log.d("khoa", "Download URL: $uri")
            imageUriFirebase = uri
            updateAudioUri()

        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.e("khoa", "Failed to get download URL: ${exception.message}")
        }
    }

    private fun addSong(song: Song) {
        val database = MyApplication[activity].getSongsDatabaseReference()
        val newSongRef = database?.child(song.getId().toString())
        newSongRef?.setValue(song)
            ?.addOnSuccessListener {
                Log.d("khoa", "Song added to database")
                Log.d("khoa", "Song genre: ${song.getGenre()}")

                // Add success logic here
            }
            ?.addOnFailureListener {
                Log.w("khoa", "Error adding song to database", it)
                // Add failure logic here
            }
    }

    private fun updateAudioUri() {
        val storageRefDownLoadUri = Firebase.storage.reference.child("audios/$fileName")
        storageRefDownLoadUri.downloadUrl.addOnSuccessListener { uri ->
            Log.d("khoa", "Download audio URL: $uri")
            audioUriFirebase = uri
            getNextSongId()
        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.e("khoa", "Failed to get download URL: ${exception.message}")
        }
    }

    private fun getNextSongId() {
        MyApplication[activity].getSongsDatabaseReference()?.get()
            ?.addOnSuccessListener { snapshot ->
                val numSongs = snapshot.childrenCount.toInt()
                nextSongId = numSongs + 1
                // Do something with the number of songs
                Log.d("khoa", "Next song ID URL: $nextSongId")

                val title = mFragmentUploadBinding?.edtTitle?.text
                val artist = mFragmentUploadBinding?.edtArtist?.text
                song.setSong(
                    nextSongId,
                    title.toString(),
                    imageUriFirebase.toString(),
                    audioUriFirebase.toString(),
                    artist.toString(),
                    mFragmentUploadBinding?.spinnerGenre?.selectedItem.toString(),
                    isLatest,
                    isFeatured
                )
                addSong(song)
                Log.d("khoa", "Next song ID URL: ${song.getId()}")
            }?.addOnFailureListener {
//            // Handle error
                exception ->
            Log.e("khoa", "Failed to get download URL: ${exception.message}")

        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, REQUEST_CODE)

    }

    private fun selectAudio() {
        val intent = Intent()
        intent.type = "audio/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, REQUEST_CODE_AUDIO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && data != null && data.data != null) {
            imageUri = data.data
            mFragmentUploadBinding?.ivImgSong?.setImageURI(imageUri)
            mFragmentUploadBinding?.tvSelectImage?.setText(imageUri.toString())
        }
        if (requestCode == REQUEST_CODE_AUDIO && resultCode == Activity.RESULT_OK && data != null) {
            audioUri = data.data
            // Do something with the audio file URI
            mFragmentUploadBinding?.tvSelectAudio?.text = audioUri.toString()
        }
    }
}