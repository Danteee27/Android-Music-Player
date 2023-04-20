package com.example.music.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.music.adapter.BannerSongAdapter.BannerSongViewHolder
import com.example.music.databinding.ItemBannerSongBinding
import com.example.music.listener.IOnClickSongItemListener
import com.example.music.model.Song
import com.example.music.utils.GlideUtils

class BannerSongAdapter(private val mListSongs: MutableList<Song>?,
                        private val iOnClickSongItemListener: IOnClickSongItemListener?) : RecyclerView.Adapter<BannerSongViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerSongViewHolder {
        val itemBannerSongBinding = ItemBannerSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerSongViewHolder(itemBannerSongBinding)
    }

    override fun onBindViewHolder(holder: BannerSongViewHolder, position: Int) {
        val song = mListSongs?.get(position) ?: return
        holder.mItemBannerSongBinding?.imageBanner?.let { GlideUtils.loadUrlBanner(song.getImage(), it) }
        holder.mItemBannerSongBinding?.layoutItem?.setOnClickListener { iOnClickSongItemListener?.onClickItemSong(song) }
    }

    override fun getItemCount(): Int {
        return mListSongs?.size ?: 0
    }

    class BannerSongViewHolder(itemBannerSongBinding: ItemBannerSongBinding) : RecyclerView.ViewHolder(itemBannerSongBinding.root) {
        val mItemBannerSongBinding: ItemBannerSongBinding?

        init {
            mItemBannerSongBinding = itemBannerSongBinding
        }
    }
}