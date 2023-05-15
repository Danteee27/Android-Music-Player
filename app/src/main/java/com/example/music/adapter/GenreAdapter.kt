package com.example.music.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.music.R
import com.example.music.databinding.ItemSelectedGenreBinding
import com.example.music.model.Genre
import org.w3c.dom.Text

class GenreAdapter(context: Context, resource: Int, objects: MutableList<Genre>) :
    ArrayAdapter<Genre>(context, resource, objects) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var curConvertView = LayoutInflater.from(parent.context).inflate(R.layout.item_selected_genre,parent,false)

        var tvSelectedGenre = curConvertView.findViewById<TextView>(R.id.item_selected_tv_selected_genre)

        var genre : Genre? = this.getItem(position)

        if(genre != null)
        {
            tvSelectedGenre.text = genre.getName()
        }

        return curConvertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var curConvertView = LayoutInflater.from(parent.context).inflate(R.layout.item_genre,parent,false)

        var tvGenre = curConvertView.findViewById<TextView>(R.id.item_tv_genre)

        var genre : Genre? = this.getItem(position)

        if(genre != null)
        {
            tvGenre.text = genre.getName()
        }

        return curConvertView

    }
}