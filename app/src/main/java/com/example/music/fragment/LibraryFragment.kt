package com.example.music.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.music.MyApplication
import com.example.music.R
import com.example.music.adapter.GenreGridAdapter
import com.example.music.adapter.SongGridAdapter
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.FragmentLibraryBinding
import com.example.music.listener.IOnClickGenreItemListener
import com.example.music.listener.IOnClickSongItemListener
import com.example.music.model.Genre
import com.example.music.model.Song
import com.example.music.utils.StringUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class LibraryFragment : Fragment() {

    var mFragmentLibraryBinding: FragmentLibraryBinding? = null
    private var mListGenres: MutableList<Genre>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mFragmentLibraryBinding = FragmentLibraryBinding.inflate(inflater,container,false)
        getListGenresFromFirebase()
        return mFragmentLibraryBinding!!.root
    }

    private fun getListGenresFromFirebase() {
        if (activity == null) {
            return
        }
        MyApplication[activity].getGenresDatabaseReference()?.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mListGenres = ArrayList()
                for (dataSnapshot in snapshot.children) {
                    val genre: Genre = dataSnapshot.getValue<Genre>(Genre::class.java) ?: return
                    mListGenres?.add(genre)

                }
//                displayListBannerSongs()
//                displayListPopularSongs()
//                displayListNewSongs()
                displayListGenres()
            }

            override fun onCancelled(error: DatabaseError) {
                GlobalFuntion.showToastMessage(activity, getString(R.string.msg_get_date_error))
            }
        })

    }

    private fun displayListGenres() {
        Log.d("khoa","size of genres: $mListGenres")
        val gridLayoutManager = GridLayoutManager(activity, 2)
        mFragmentLibraryBinding?.rcvGenres?.layoutManager = gridLayoutManager
        val genreGridAdapter = GenreGridAdapter(mListGenres,object :IOnClickGenreItemListener{
            override fun onClickItemGenre(genre: Genre) {
                goToGenreDetail(genre)
            }
        })
        mFragmentLibraryBinding?.rcvGenres?.adapter = genreGridAdapter
    }

    private fun goToGenreDetail(genre: Genre) {

    }
}