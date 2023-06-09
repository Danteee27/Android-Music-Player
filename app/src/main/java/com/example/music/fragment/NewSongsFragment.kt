package com.example.music.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.MyApplication
import com.example.music.R
import com.example.music.activity.MainActivity
import com.example.music.activity.PlayMusicActivity
import com.example.music.adapter.SongAdapter
import com.example.music.adapter.SongGridAdapter
import com.example.music.constant.Constant
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.FragmentNewSongsBinding
import com.example.music.listener.IOnClickSongItemListener
import com.example.music.model.Song
import com.example.music.service.MusicService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class NewSongsFragment : Fragment() {

    private var mFragmentNewSongsBinding: FragmentNewSongsBinding? = null
    private var mListSong: MutableList<Song>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentNewSongsBinding = FragmentNewSongsBinding.inflate(inflater, container, false)
        getListNewSongs()
        initListener()
        return mFragmentNewSongsBinding?.root
    }

    private fun getListNewSongs() {
        if (activity == null) {
            return
        }
        MyApplication[activity].getSongsDatabaseReference()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mListSong = ArrayList()
                for (dataSnapshot in snapshot.children) {
                    val song: Song = dataSnapshot.getValue<Song>(Song::class.java) ?: return
                    if (song.isLatest()) {
                        mListSong?.add(0, song)
                    }
                }
                displayListNewSongs()
            }

            override fun onCancelled(error: DatabaseError) {
                GlobalFuntion.showToastMessage(activity, getString(R.string.msg_get_date_error))
            }
        })
    }

    private fun displayListNewSongs() {
        if (activity == null) {
            return
        }
//        val linearLayoutManager = LinearLayoutManager(activity)
//        mFragmentNewSongsBinding?.rcvData?.layoutManager = linearLayoutManager
//        val songAdapter = SongAdapter(mListSong, object : IOnClickSongItemListener {
//            override fun onClickItemSong(song: Song) {
//                goToSongDetail(song)
//            }
//        })
//        mFragmentNewSongsBinding?.rcvData?.adapter = songAdapter
        val gridLayoutManager = GridLayoutManager(activity, 2)

        mFragmentNewSongsBinding?.rcvData?.layoutManager = gridLayoutManager
        val songGridAdapter = SongGridAdapter(mListSong, object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }
        })
        mFragmentNewSongsBinding?.rcvData?.adapter = songGridAdapter
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
            // Play all songs
            MusicService.clearListSongPlaying()
            mListSong?.let { it1 -> MusicService.mListSongPlaying?.addAll(it1) }
            MusicService.isPlaying = false
            GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0)
            GlobalFuntion.startActivity(getActivity(), PlayMusicActivity::class.java)
        }

        activity.getActivityMainBinding()!!.header.layoutShuffle.setOnClickListener {
            // Shuffle the song list
            mListSong?.let { songs ->
                val shuffledSongs = songs.shuffled()
                MusicService.clearListSongPlaying()
                MusicService.mListSongPlaying?.addAll(shuffledSongs)
                MusicService.isPlaying = false
                GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0)
                GlobalFuntion.startActivity(getActivity(), PlayMusicActivity::class.java)
            }
        }
    }
}