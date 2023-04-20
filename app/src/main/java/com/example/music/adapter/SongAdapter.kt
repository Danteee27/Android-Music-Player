package com.example.music.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.music.adapter.SongAdapter.SongViewHolder
import com.example.music.databinding.ItemSongBinding
import com.example.music.listener.IOnClickSongItemListener
import com.example.music.model.Song
import com.example.music.utils.GlideUtils

class SongAdapter(private val mListSongs: MutableList<Song>?,
                  private val iOnClickSongItemListener: IOnClickSongItemListener?) : RecyclerView.Adapter<SongViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(itemSongBinding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = mListSongs?.get(position) ?: return
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongBinding.imgSong)
        holder.mItemSongBinding.tvSongName.text = song.getTitle()
        holder.mItemSongBinding.tvArtist.text = song.getArtist()
        holder.mItemSongBinding.tvCountView.text = song.getCount().toString()
        holder.mItemSongBinding.layoutItem.setOnClickListener { iOnClickSongItemListener?.onClickItemSong(song) }
    }

    override fun getItemCount(): Int {
        return mListSongs?.size ?: 0
    }

    class SongViewHolder(val mItemSongBinding: ItemSongBinding) : RecyclerView.ViewHolder(mItemSongBinding.root)
}