package com.example.music.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.music.MyApplication
import com.example.music.R
import com.example.music.activity.MainActivity
import com.example.music.constant.Constant
import com.example.music.constant.GlobalFuntion
import com.example.music.model.Song
import com.example.music.utils.StringUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.app.Activity
import com.google.firebase.database.DatabaseReference
import java.util.*

class MusicService : Service(), OnPreparedListener, OnCompletionListener {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (mPlayer == null) {
            mPlayer = MediaPlayer()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey(Constant.MUSIC_ACTION)) {
                mAction = bundle.getInt(Constant.MUSIC_ACTION)
            }
            if (bundle.containsKey(Constant.SONG_POSITION)) {
                mSongPosition = bundle.getInt(Constant.SONG_POSITION)
            }
            handleActionMusic(mAction)
        }
        return START_NOT_STICKY
    }

    private fun handleActionMusic(action: Int) {
        when (action) {
            Constant.PLAY -> playSong()
            Constant.PREVIOUS -> prevSong()
            Constant.NEXT -> nextSong()
            Constant.PAUSE -> pauseSong()
            Constant.RESUME -> resumeSong()
            Constant.CANNEL_NOTIFICATION -> cannelNotification()
        }
    }

    private fun playSong() {
        val songUrl = mListSongPlaying?.get(mSongPosition)?.getUrl()
        if (!StringUtil.isEmpty(songUrl)) {
            playMediaPlayer(songUrl)
        }

//        if(mListSongPlaying?.get(mSongPosition) != null){
//            val database = MyApplication[this].getSongsDatabaseReference()
//            val updates = hashMapOf<String, Any>("count" to "${mListSongPlaying?.get(mSongPosition)?.getCount()?.plus(1)}")
//            val newSongRef = database?.child(mListSongPlaying?.get(mSongPosition)?.getId().toString())?.updateChildren(updates, object : DatabaseReference.CompletionListener {
//                override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
//                    if (databaseError != null) {
//                        // Update operation failed
//                        // Handle the error here
//                        Log.e("Update", "Update failed: ${databaseError.message}")
//                    } else {
//                        // Update operation succeeded
//                        Log.d("Update", "Update succeeded")
//                    }
//                }
//            })
//        }
    }

    private fun pauseSong() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer!!.pause()
            isPlaying = false
            sendMusicNotification()
            sendBroadcastChangeListener()
        }
    }

    private fun cannelNotification() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer!!.pause()
            isPlaying = false
        }
        val notifManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notifManager.cancelAll()
        sendBroadcastChangeListener()
        stopSelf()
    }

    private fun resumeSong() {
        if (mPlayer != null) {
            mPlayer!!.start()
            isPlaying = true
            sendMusicNotification()
            sendBroadcastChangeListener()
        }
    }

    private fun prevSong() {
        if (mListSongPlaying == null || mListSongPlaying!!.isEmpty()) {
            return
        }
        if (mListSongPlaying!!.size > 1) {
            if (mSongPosition > 0) {
                mSongPosition--
            } else {
                mSongPosition = mListSongPlaying!!.size - 1
            }
        } else {
            mSongPosition = 0
        }
        sendMusicNotification()
        sendBroadcastChangeListener()
        playSong()
    }

    private fun nextSong() {
        if (mListSongPlaying == null || mListSongPlaying!!.isEmpty()) {
            return
        }
        if (mListSongPlaying!!.size > 1 && mSongPosition < mListSongPlaying!!.size - 1) {
            mSongPosition++
        } else {
            mSongPosition = 0
        }
        sendMusicNotification()
        sendBroadcastChangeListener()
        playSong()
    }

    private fun playMediaPlayer(songUrl: String?) {
        try {
            if (mPlayer == null) {
                return
            }
            if (!mPlayer!!.isPlaying) {
                mPlayer!!.reset()
                mPlayer!!.setDataSource(songUrl)
                mPlayer!!.prepareAsync()
                initControl()
            } else {
                mPlayer!!.stop()
                mPlayer!!.reset()
                mPlayer!!.setDataSource(songUrl)
                mPlayer!!.prepareAsync()
                initControl()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initControl() {
        mPlayer?.setOnPreparedListener(this)
        mPlayer?.setOnCompletionListener(this)
    }

    private fun sendMusicNotification() {
        val song = mListSongPlaying?.get(mSongPosition)
        val pendingFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val intent = Intent(this, MainActivity::class.java)
        @SuppressLint("UnspecifiedImmutableFlag") val pendingIntent = PendingIntent.getActivity(
            this,
            0, intent, pendingFlag
        )
        val remoteViews = RemoteViews(packageName, R.layout.layout_push_notification_music)
        remoteViews.setTextViewText(R.id.tv_song_name, song?.getTitle())
        remoteViews.setTextViewText(R.id.tv_artist, song?.getArtist())

        // Set listener
        remoteViews.setOnClickPendingIntent(
            R.id.img_previous,
            GlobalFuntion.openMusicReceiver(this, Constant.PREVIOUS)
        )
        remoteViews.setOnClickPendingIntent(
            R.id.img_next,
            GlobalFuntion.openMusicReceiver(this, Constant.NEXT)
        )
        if (isPlaying) {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_pause_white)
            remoteViews.setOnClickPendingIntent(
                R.id.img_play,
                GlobalFuntion.openMusicReceiver(this, Constant.PAUSE)
            )
        } else {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_play_white)
            remoteViews.setOnClickPendingIntent(
                R.id.img_play,
                GlobalFuntion.openMusicReceiver(this, Constant.RESUME)
            )
        }
        remoteViews.setOnClickPendingIntent(
            R.id.img_close,
            GlobalFuntion.openMusicReceiver(this, Constant.CANNEL_NOTIFICATION)
        )
        val notification = NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_small_push_notification)
            .setContentIntent(pendingIntent)
            .setCustomContentView(remoteViews)
            .setSound(null)
            .build()
        startForeground(1, notification)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mAction = Constant.NEXT
        nextSong()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (mPlayer != null) {
            mLengthSong = mPlayer!!.duration
        }
        mp?.start()
        isPlaying = true
        mAction = Constant.PLAY
        sendMusicNotification()
        sendBroadcastChangeListener()
        changeCountViewSong()
    }

    private fun sendBroadcastChangeListener() {
        val intent = Intent(Constant.CHANGE_LISTENER)
        intent.putExtra(Constant.MUSIC_ACTION, mAction)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun changeCountViewSong() {
        val songId = mListSongPlaying?.get(mSongPosition)?.getId()
        MyApplication[this].getCountViewDatabaseReference(songId!!)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentCount: Int? = snapshot.getValue<Int>(Int::class.java)
                    val newCount = currentCount?.plus(1)
                    MyApplication[this@MusicService].getCountViewDatabaseReference(songId)
                        ?.removeEventListener(this)
                    MyApplication[this@MusicService].getCountViewDatabaseReference(songId)
                        ?.setValue(newCount)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    public fun changeLikeSong() {
        val songId = mListSongPlaying?.get(mSongPosition)?.getId()
        MyApplication[this].getLikedDatabaseReference(songId!!)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val current: Boolean? = snapshot.getValue<Boolean>(Boolean::class.java)
                    var newCurrent = false;
                    if (current == true) {
                        newCurrent = true;
                    }
                    MyApplication[this@MusicService].getLikedDatabaseReference(songId)
                        ?.removeEventListener(this)
                    MyApplication[this@MusicService].getLikedDatabaseReference(songId)
                        ?.setValue(newCurrent)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayer != null) {
            mPlayer!!.release()
            mPlayer = null
        }
    }

    companion object {
        var isPlaying = false
        var mListSongPlaying: MutableList<Song?>? = null
        var mSongPosition = 0
        var mPlayer: MediaPlayer? = null
        var mLengthSong = 0
        var mAction = -1
        fun clearListSongPlaying() {
            if (mListSongPlaying != null) {
                mListSongPlaying!!.clear()
            } else {
                mListSongPlaying = ArrayList()
            }
        }

        fun initialListSongPlaying() {
            if (mListSongPlaying == null) {
                mListSongPlaying = ArrayList()
            }
        }
    }
}