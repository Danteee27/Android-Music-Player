package com.example.music.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.music.MyApplication
import com.example.music.R
import com.example.music.activity.MainActivity
import com.example.music.activity.PlayMusicActivity
import com.example.music.adapter.BannerSongAdapter
import com.example.music.adapter.SongAdapter
import com.example.music.adapter.SongGridAdapter
import com.example.music.constant.Constant
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.FragmentHomeBinding
import com.example.music.listener.IOnClickSongItemListener
import com.example.music.model.Song
import com.example.music.service.MusicService
import com.example.music.utils.StringUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class HomeFragment : Fragment() {

    private var mFragmentHomeBinding: FragmentHomeBinding? = null
    private var mListSong: MutableList<Song>? = null
    private var mListSongBanner: MutableList<Song>? = null
    private val mHandlerBanner: Handler = Handler(Looper.getMainLooper())
    private val mRunnableBanner: Runnable = Runnable {
        if (mListSongBanner == null || mListSongBanner!!.isEmpty()) {
            return@Runnable
        }
        if (mFragmentHomeBinding?.viewpager2?.currentItem == mListSongBanner!!.size - 1) {
            mFragmentHomeBinding?.viewpager2?.currentItem = 0
            return@Runnable
        }
        mFragmentHomeBinding?.viewpager2?.currentItem = mFragmentHomeBinding?.viewpager2?.currentItem!! + 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        getListSongFromFirebase("")
        initListener()
        return mFragmentHomeBinding?.root
    }

    private fun initListener() {
        mFragmentHomeBinding?.edtSearchName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                val strKey = s.toString().trim { it <= ' ' }
                if (strKey == "" || strKey.isEmpty()) {
                    mListSong?.clear()
                    getListSongFromFirebase("")
                }
            }
        })
        mFragmentHomeBinding?.imgSearch?.setOnClickListener { searchSong() }
        mFragmentHomeBinding?.edtSearchName?.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchSong()
                return@setOnEditorActionListener true
            }
            false
        }
        mFragmentHomeBinding?.layoutViewAllPopular?.setOnClickListener {
            val mainActivity = activity as MainActivity?
            mainActivity?.openPopularSongsScreen()
        }
        mFragmentHomeBinding?.layoutViewAllNewSongs?.setOnClickListener {
            val mainActivity = activity as MainActivity?
            mainActivity?.openNewSongsScreen()
        }
    }

    private fun getListSongFromFirebase(key: String?) {
        if (activity == null) {
            return
        }
        MyApplication[activity].getSongsDatabaseReference()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mFragmentHomeBinding?.layoutContent?.visibility = View.VISIBLE
                mListSong = ArrayList()
                for (dataSnapshot in snapshot.children) {
                    val song: Song = dataSnapshot.getValue<Song>(Song::class.java) ?: return
                    if (StringUtil.isEmpty(key)) {
                        mListSong?.add(0, song)
                    } else {
                        if (GlobalFuntion.getTextSearch(song.getTitle()!!).trim().toLowerCase(Locale.getDefault())
                                        .contains(GlobalFuntion.getTextSearch(key!!).trim().toLowerCase(Locale.getDefault()))) {
                            mListSong?.add(0, song)
                        }
                    }
                }
                displayListBannerSongs()
                displayListPopularSongs()
                displayListNewSongs()
            }

            override fun onCancelled(error: DatabaseError) {
                GlobalFuntion.showToastMessage(activity, getString(R.string.msg_get_date_error))
            }
        })
    }

    private fun displayListBannerSongs() {
        val bannerSongAdapter = BannerSongAdapter(getListBannerSongs(), object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }
        })
        mFragmentHomeBinding?.viewpager2?.adapter = bannerSongAdapter
        mFragmentHomeBinding?.indicator3?.setViewPager(mFragmentHomeBinding?.viewpager2)
        mFragmentHomeBinding?.viewpager2?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mHandlerBanner.removeCallbacks(mRunnableBanner)
                mHandlerBanner.postDelayed(mRunnableBanner, 3000)
            }
        })
    }

    private fun getListBannerSongs(): MutableList<Song>? {
        if (mListSongBanner != null) {
            mListSongBanner!!.clear()
        } else {
            mListSongBanner = ArrayList()
        }
        if (mListSong == null || mListSong!!.isEmpty()) {
            return mListSongBanner
        }
        for (song in mListSong!!) {
            if (song.isFeatured() && mListSongBanner!!.size < Constant.MAX_COUNT_BANNER) {
                mListSongBanner!!.add(song)
            }
        }
        return mListSongBanner
    }

    private fun displayListPopularSongs() {
        val gridLayoutManager = GridLayoutManager(activity, 2)
        mFragmentHomeBinding?.rcvPopularSongs?.layoutManager = gridLayoutManager
        val songGridAdapter = SongGridAdapter(getListPopularSongs(), object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }
        })
        mFragmentHomeBinding?.rcvPopularSongs?.adapter = songGridAdapter
    }

    private fun getListPopularSongs(): MutableList<Song> {
        val list: MutableList<Song> = ArrayList()
        if (mListSong == null || mListSong!!.isEmpty()) {
            return list
        }
        val allSongs: MutableList<Song> = ArrayList(mListSong!!)
        allSongs.sortWith(Comparator { song1: Song?, song2: Song? -> song2!!.getCount() - song1!!.getCount() })
        for (song in allSongs) {
            if (list.size < Constant.MAX_COUNT_POPULAR) {
                list.add(song)
            }
        }
        return list
    }

    private fun displayListNewSongs() {
        if (activity == null) {
            return
        }
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentHomeBinding?.rcvNewSongs?.layoutManager = linearLayoutManager
        val songAdapter = SongAdapter(getListNewSongs(), object : IOnClickSongItemListener {
            override fun onClickItemSong(song: Song) {
                goToSongDetail(song)
            }
        })
        mFragmentHomeBinding?.rcvNewSongs?.adapter = songAdapter
    }

    private fun getListNewSongs(): MutableList<Song> {
        val list: MutableList<Song> = ArrayList()
        if (mListSong == null || mListSong!!.isEmpty()) {
            return list
        }
        for (song in mListSong!!) {
            if (song.isLatest() && list.size < Constant.MAX_COUNT_LATEST) {
                list.add(song)
            }
        }
        return list
    }

    private fun searchSong() {
        val strKey = mFragmentHomeBinding?.edtSearchName?.text.toString().trim { it <= ' ' }
        mListSong?.clear()
        getListSongFromFirebase(strKey)
        GlobalFuntion.hideSoftKeyboard(activity)
    }

    private fun goToSongDetail(song: Song) {
        MusicService.clearListSongPlaying()
        MusicService.mListSongPlaying?.add(song)
        MusicService.isPlaying = false
        GlobalFuntion.startMusicService(activity, Constant.PLAY, 0)
        GlobalFuntion.startActivity(activity, PlayMusicActivity::class.java)
    }
}