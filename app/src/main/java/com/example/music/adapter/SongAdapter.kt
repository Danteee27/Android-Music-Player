package com.example.music.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.example.music.adapter.SongAdapter.SongViewHolder
import com.example.music.databinding.ItemSongBinding
import com.example.music.databinding.SongButtonBinding
import com.example.music.listener.IOnClickSongItemListener
import com.example.music.model.Song
import com.example.music.service.MusicService
import com.example.music.service.MusicService.Companion.mListSongPlaying
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
        holder.mItemSongBinding.btnMore.setOnClickListener {
            val inflater = LayoutInflater.from(holder.itemView.context)
            val popupView = SongButtonBinding.inflate(inflater)

            val popupWindow = PopupWindow(popupView.root, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true)

            val btnOption1 = popupView.btnList
            val btnOption2 = popupView.btnQueue

            // Set click listeners for the buttons
            btnOption1.setOnClickListener {
                // Handle click on button1
                val songID = song.getId()
                popupWindow.dismiss() // Optionally dismiss the popup window
            }

            btnOption2.setOnClickListener {
                // Handle click on button2
                if (mListSongPlaying == null) {
                    mListSongPlaying = mutableListOf()
                }
                mListSongPlaying?.add(song)
                popupWindow.dismiss() // Optionally dismiss the popup window
            }

            popupWindow.setOnDismissListener {
                // Handle dismiss actions here
            }

            val xOffset = 0
            val yOffset = 0
            popupWindow.showAsDropDown(holder.mItemSongBinding.btnMore, xOffset, yOffset, Gravity.END)
        }
        holder.mItemSongBinding.layoutItem.setOnClickListener { iOnClickSongItemListener?.onClickItemSong(song) }
    }

    override fun getItemCount(): Int {
        return mListSongs?.size ?: 0
    }

    class SongViewHolder(val mItemSongBinding: ItemSongBinding) : RecyclerView.ViewHolder(mItemSongBinding.root)
}