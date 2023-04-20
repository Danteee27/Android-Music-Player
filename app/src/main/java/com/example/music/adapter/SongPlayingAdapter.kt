package com.example.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.music.R
import com.example.music.adapter.SongPlayingAdapter.SongPlayingViewHolder
import com.example.music.databinding.ItemSongPlayingBinding
import com.example.music.listener.IOnClickSongPlayingItemListener
import com.example.music.model.Song
import com.example.music.utils.GlideUtils

class SongPlayingAdapter(private val mListSongs: MutableList<Song?>?,
                         private val iOnClickSongPlayingItemListener: IOnClickSongPlayingItemListener?) : RecyclerView.Adapter<SongPlayingViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongPlayingViewHolder {
        val itemSongPlayingBinding = ItemSongPlayingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongPlayingViewHolder(itemSongPlayingBinding)
    }

    override fun onBindViewHolder(holder: SongPlayingViewHolder, position: Int) {
        val song = mListSongs?.get(position) ?: return
        if (song.isPlaying()) {
            holder.mItemSongPlayingBinding.layoutItem.setBackgroundResource(R.color.background_bottom)
            holder.mItemSongPlayingBinding.imgPlaying.visibility = View.VISIBLE
        } else {
            holder.mItemSongPlayingBinding.layoutItem.setBackgroundResource(R.color.white)
            holder.mItemSongPlayingBinding.imgPlaying.visibility = View.GONE
        }
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongPlayingBinding.imgSong)
        holder.mItemSongPlayingBinding.tvSongName.text = song.getTitle()
        holder.mItemSongPlayingBinding.tvArtist.text = song.getArtist()
        holder.mItemSongPlayingBinding.layoutItem.setOnClickListener {
            iOnClickSongPlayingItemListener?.onClickItemSongPlaying(holder.adapterPosition) }
    }

    override fun getItemCount(): Int {
        return mListSongs?.size ?: 0
    }

    class SongPlayingViewHolder(val mItemSongPlayingBinding: ItemSongPlayingBinding) : RecyclerView.ViewHolder(mItemSongPlayingBinding.root)
}