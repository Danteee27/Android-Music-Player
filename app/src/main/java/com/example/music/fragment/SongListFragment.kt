package com.example.music.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.MyApplication
import com.example.music.R
import com.example.music.activity.MainActivity
import com.example.music.activity.PlayMusicActivity
import com.example.music.adapter.SongAdapter
import com.example.music.constant.Constant
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.FragmentAllSongsBinding
import com.example.music.databinding.FragmentListSonglistBinding
import com.example.music.listener.IOnClickSongItemListener
import com.example.music.model.Song
import com.example.music.service.MusicService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class SongListFragment : Fragment() {

    private var mFragmentListSong: FragmentListSonglistBinding? = null
    private var mListIdSong: ArrayList<String> =ArrayList()
    private var mListSong: MutableList<Song>? = null
    var listName: String? = null
    val firebaseAuth = FirebaseAuth.getInstance()
    val uid = firebaseAuth.currentUser?.uid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentListSong = FragmentListSonglistBinding.inflate(inflater, container, false)
        listName = arguments?.getString("listName")
        getAllSongId()
        getListAllSongs()
        initListener()
        return mFragmentListSong?.root
    }
    private fun getAllSongId() {
        if (activity == null) {
            return
        }

        val playlistRef = MyApplication[activity].getSongInListDatabaseReference(uid, listName)

        playlistRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mListIdSong.clear() // Clear the existing list

                for (dataSnapshot in snapshot.children) {
                    val songId = dataSnapshot.value.toString()
                    println(songId)
                    mListIdSong.add(songId)
                }

                // Process the list of song IDs
                // ...
            }

            override fun onCancelled(error: DatabaseError) {
                GlobalFuntion.showToastMessage(activity, getString(R.string.msg_get_date_error))
            }
        })
    }

    private fun getListAllSongs() {
        if (activity == null) {
            return
        }

        MyApplication[activity].getSongsDatabaseReference()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val filteredSongs = ArrayList<Song>()

                for (dataSnapshot in snapshot.children) {
                    val song: Song = dataSnapshot.getValue(Song::class.java) ?: continue
                    val songId = song.getId().toString()
                    if (mListIdSong.contains(songId) == true) {
                        println("$songId added")
                        filteredSongs.add(song)
                    }
                }

                // Assign the filtered songs to mListSong
                mListSong = filteredSongs

                // Display the filtered song list
                displayListAllSongs()
            }

            override fun onCancelled(error: DatabaseError) {
                GlobalFuntion.showToastMessage(activity, getString(R.string.msg_get_date_error))
            }
        })
    }

    private fun displayListAllSongs() {
        if (activity == null) {
            return
        }
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentListSong?.rcvData?.layoutManager = linearLayoutManager
        val songAdapter = SongAdapter(mListSong, object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }
        })
        mFragmentListSong?.rcvData?.adapter = songAdapter
    }

    private fun goToSongDetail(song: Song) {
        MusicService.clearListSongPlaying()
        MusicService.mListSongPlaying?.add(song)
        MusicService.isPlaying = false
        GlobalFuntion.startMusicService(activity, Constant.PLAY, 0)
        GlobalFuntion.startActivity(activity, PlayMusicActivity::class.java)
    }

    private fun initListener() {
        val activity = activity as MainActivity?
        if (activity?.getActivityMainBinding() == null) {
            return
        }
        activity.getActivityMainBinding()!!.header.layoutPlayAll.setOnClickListener {
            MusicService.clearListSongPlaying()
            mListSong?.let { it1 -> MusicService.mListSongPlaying?.addAll(it1) }
            MusicService.isPlaying = false
            GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0)
            GlobalFuntion.startActivity(getActivity(), PlayMusicActivity::class.java)
        }
    }
}