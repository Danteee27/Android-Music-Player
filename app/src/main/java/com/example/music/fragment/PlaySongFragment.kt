package com.example.music.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.music.R
import com.example.music.constant.Constant
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.FragmentPlaySongBinding
import com.example.music.model.Song
import com.example.music.service.MusicService
import com.example.music.utils.AppUtil
import com.example.music.utils.GlideUtils
import java.util.*

class PlaySongFragment : Fragment(), View.OnClickListener {

    private var mFragmentPlaySongBinding: FragmentPlaySongBinding? = null
    private var mTimer: Timer? = null
    private var mAction = 0
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0)
            handleMusicAction()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentPlaySongBinding = FragmentPlaySongBinding.inflate(inflater, container, false)
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity!!).registerReceiver(mBroadcastReceiver,
                    IntentFilter(Constant.CHANGE_LISTENER))
        }
        initControl()
        showInforSong()
        mAction = MusicService.mAction
        handleMusicAction()
        return mFragmentPlaySongBinding?.root
    }

    private fun initControl() {
        mTimer = Timer()
        mFragmentPlaySongBinding?.imgPrevious?.setOnClickListener(this)
        mFragmentPlaySongBinding?.imgPlay?.setOnClickListener(this)
        mFragmentPlaySongBinding?.imgNext?.setOnClickListener(this)
        mFragmentPlaySongBinding?.seekbar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                MusicService.mPlayer?.seekTo(seekBar.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
        })
    }

    private fun showInforSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying!!.isEmpty()) {
            return
        }
        val currentSong: Song? = MusicService.mListSongPlaying!![MusicService.mSongPosition]
        mFragmentPlaySongBinding?.tvSongName?.text = currentSong?.getTitle()
        mFragmentPlaySongBinding?.tvArtist?.text = currentSong?.getArtist()
        GlideUtils.loadUrl(currentSong?.getImage(), mFragmentPlaySongBinding?.imgSong!!)
    }

    private fun handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            if (activity != null) {
                activity!!.onBackPressed()
            }
            return
        }
        when (mAction) {
            Constant.PREVIOUS -> {
                stopAnimationPlayMusic()
                showInforSong()
            }
            Constant.NEXT -> {
                stopAnimationPlayMusic()
                showInforSong()
            }
            Constant.PLAY -> {
                showInforSong()
                if (MusicService.isPlaying) {
                    startAnimationPlayMusic()
                }
                showSeekBar()
                showStatusButtonPlay()
            }
            Constant.PAUSE -> {
                stopAnimationPlayMusic()
                showSeekBar()
                showStatusButtonPlay()
            }
            Constant.RESUME -> {
                startAnimationPlayMusic()
                showSeekBar()
                showStatusButtonPlay()
            }
        }
    }

    private fun startAnimationPlayMusic() {
        val runnable: Runnable = object : Runnable {
            override fun run() {
                mFragmentPlaySongBinding?.imgSong?.animate()?.rotationBy(360f)?.withEndAction(this)?.setDuration(15000)
                        ?.setInterpolator(LinearInterpolator())?.start()
            }
        }
        mFragmentPlaySongBinding?.imgSong?.animate()?.rotationBy(360f)?.withEndAction(runnable)?.setDuration(15000)
                ?.setInterpolator(LinearInterpolator())?.start()
    }

    private fun stopAnimationPlayMusic() {
        mFragmentPlaySongBinding?.imgSong?.animate()?.cancel()
    }

    private fun showSeekBar() {
        mTimer?.schedule(object : TimerTask() {
            override fun run() {
                if (activity == null) {
                    return
                }
                activity!!.runOnUiThread {
                    if (MusicService.mPlayer != null) {
                        mFragmentPlaySongBinding?.tvTimeCurrent?.text = AppUtil.getTime(MusicService.mPlayer!!.currentPosition)
                        mFragmentPlaySongBinding?.tvTimeMax?.text = AppUtil.getTime(MusicService.mLengthSong)
                        mFragmentPlaySongBinding?.seekbar?.max = MusicService.mLengthSong
                        mFragmentPlaySongBinding?.seekbar?.progress = MusicService.mPlayer!!.currentPosition
                    }
                }
            }
        }, 0, 1000)
    }

    private fun showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            mFragmentPlaySongBinding?.imgPlay?.setImageResource(R.drawable.ic_pause_black)
        } else {
            mFragmentPlaySongBinding?.imgPlay?.setImageResource(R.drawable.ic_play_black)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mBroadcastReceiver)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_previous -> clickOnPrevButton()
            R.id.img_play -> clickOnPlayButton()
            R.id.img_next -> clickOnNextButton()
        }
    }

    private fun clickOnPrevButton() {
        GlobalFuntion.startMusicService(activity, Constant.PREVIOUS, MusicService.mSongPosition)
    }

    private fun clickOnNextButton() {
        GlobalFuntion.startMusicService(activity, Constant.NEXT, MusicService.mSongPosition)
    }

    private fun clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFuntion.startMusicService(activity, Constant.PAUSE, MusicService.mSongPosition)
        } else {
            GlobalFuntion.startMusicService(activity, Constant.RESUME, MusicService.mSongPosition)
        }
    }
}