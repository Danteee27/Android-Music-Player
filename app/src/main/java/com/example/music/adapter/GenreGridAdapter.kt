package com.example.music.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.music.R
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.ItemGenreBinding
import com.example.music.databinding.ItemGenreGridBinding
import com.example.music.listener.IOnClickGenreItemListener
import com.example.music.model.Genre

class GenreGridAdapter(
    private val mListGenres: MutableList<Genre>?,
    private val iOnClickGenreItemListener: IOnClickGenreItemListener?
) :
    RecyclerView.Adapter<GenreGridAdapter.GenreGridViewHolder?>() {

    class GenreGridViewHolder(val mItemGenreGridBinding: ItemGenreGridBinding):RecyclerView.ViewHolder(mItemGenreGridBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreGridViewHolder {
        val itemGenreGridBinding =
            ItemGenreGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GenreGridViewHolder(itemGenreGridBinding)
    }


    override fun onBindViewHolder(holder: GenreGridViewHolder, position: Int) {
        val genre = mListGenres?.get(position) ?: return
//        holder.mItemGenreGridBinding.cardGenre.setCardBackgroundColor(GlobalFuntion.getHexColorGenre(genre.getColor()))
        holder.mItemGenreGridBinding.cardGenre.setBackgroundResource(GlobalFuntion.getResourceBackgroundGenre(genre.getColor()))
        holder.mItemGenreGridBinding.tvGenreName.setText(genre.getName())
        holder.mItemGenreGridBinding.itemGenreGrid.setOnClickListener { iOnClickGenreItemListener?.onClickItemGenre(genre) }
    }

    override fun getItemCount(): Int {
        return mListGenres?.size ?: 0
    }
}