package com.example.music.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.music.fragment.ListSongPlayingFragment
import com.example.music.fragment.PlaySongFragment

class MusicViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ListSongPlayingFragment()
            1 -> PlaySongFragment()
            else -> PlaySongFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}