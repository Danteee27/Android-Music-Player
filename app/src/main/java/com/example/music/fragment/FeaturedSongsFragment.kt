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
import com.example.music.databinding.FragmentFeaturedSongsBinding
import com.example.music.listener.IOnClickSongItemListener
import com.example.music.model.Song
import com.example.music.service.MusicService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class FeaturedSongsFragment : Fragment() {

    private var mFragmentFeaturedSongsBinding: FragmentFeaturedSongsBinding? = null
    private var mListSong: MutableList<Song>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentFeaturedSongsBinding = FragmentFeaturedSongsBinding.inflate(inflater, container, false)
        getListFeaturedSongs()
        initListener()
        return mFragmentFeaturedSongsBinding?.root
    }

    private fun getListFeaturedSongs() {
        if (activity == null) {
            return
        }
        MyApplication[activity].getSongsDatabaseReference()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mListSong = ArrayList()
                for (dataSnapshot in snapshot.children) {
                    val song: Song = dataSnapshot.getValue<Song>(Song::class.java) ?: return
                    if (song.isFeatured()) {
                        mListSong?.add(0, song)
                    }
                }
                displayListFeaturedSongs()
            }

            override fun onCancelled(error: DatabaseError) {
                GlobalFuntion.showToastMessage(activity, getString(R.string.msg_get_date_error))
            }
        })
    }

    private fun displayListFeaturedSongs() {
        if (activity == null) {
            return
        }
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentFeaturedSongsBinding?.rcvData?.layoutManager = linearLayoutManager
        val songAdapter = SongAdapter(mListSong, object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }
        })
        mFragmentFeaturedSongsBinding?.rcvData?.adapter = songAdapter
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