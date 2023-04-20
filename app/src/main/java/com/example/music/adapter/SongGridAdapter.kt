package com.example.music.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.music.adapter.SongGridAdapter.SongGridViewHolder
import com.example.music.databinding.ItemSongGridBinding
import com.example.music.listener.IOnClickSongItemListener
import com.example.music.model.Song
import com.example.music.utils.GlideUtils

class SongGridAdapter(private val mListSongs: MutableList<Song>?,
                      private val iOnClickSongItemListener: IOnClickSongItemListener?) : RecyclerView.Adapter<SongGridViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongGridViewHolder {
        val itemSongGridBinding = ItemSongGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongGridViewHolder(itemSongGridBinding)
    }

    override fun onBindViewHolder(holder: SongGridViewHolder, position: Int) {
        val song = mListSongs?.get(position) ?: return
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongGridBinding.imgSong)
        holder.mItemSongGridBinding.tvSongName.text = song.getTitle()
        holder.mItemSongGridBinding.tvArtist.text = song.getArtist()
        holder.mItemSongGridBinding.layoutItem.setOnClickListener { iOnClickSongItemListener?.onClickItemSong(song) }
    }

    override fun getItemCount(): Int {
        return mListSongs?.size ?: 0
    }

    class SongGridViewHolder(val mItemSongGridBinding: ItemSongGridBinding) : RecyclerView.ViewHolder(mItemSongGridBinding.root)
}