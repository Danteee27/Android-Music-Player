package com.example.music.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.music.constant.Constant
import com.example.music.constant.GlobalFuntion

class MusicReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.extras!!.getInt(Constant.MUSIC_ACTION)
        GlobalFuntion.startMusicService(context, action, MusicService.mSongPosition)
    }
}