package com.example.music.adapter

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.*
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.example.music.MyApplication
import com.example.music.R
import com.example.music.adapter.SongGridAdapter.SongGridViewHolder
import com.example.music.constant.Constant
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.ItemSongGridBinding
import com.example.music.databinding.SongButtonBinding
import com.example.music.listener.IOnClickSongItemListener
import com.example.music.model.Song
import com.example.music.service.MusicService
import com.example.music.utils.GlideUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class SongGridAdapter(private val mListSongs: MutableList<Song>?,
                      private val iOnClickSongItemListener: IOnClickSongItemListener?) : RecyclerView.Adapter<SongGridViewHolder?>() {
    private var firebaseAuth: FirebaseAuth =  FirebaseAuth.getInstance()
    var userList = ArrayList<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongGridViewHolder {
        val itemSongGridBinding = ItemSongGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongGridViewHolder(itemSongGridBinding)
    }

    override fun onBindViewHolder(holder: SongGridViewHolder, position: Int) {
        val song = mListSongs?.get(position) ?: return
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongGridBinding.imgSong)
        holder.mItemSongGridBinding.tvSongName.text = song.getTitle()
        holder.mItemSongGridBinding.tvArtist.text = song.getArtist()
        holder.mItemSongGridBinding.cardItem.setOnClickListener { iOnClickSongItemListener?.onClickItemSong(song) }
        holder.mItemSongGridBinding.imgMoreAction.setOnClickListener {
            val inflater = LayoutInflater.from(holder.itemView.context)
            val popupView = SongButtonBinding.inflate(inflater)

            val popupWindow = PopupWindow(popupView.root, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true)

            val btnOption1 = popupView.btnList
            val btnOption2 = popupView.btnQueue
            val activity = holder.itemView.context as? Activity
            // Set click listeners for the buttons
            btnOption1.setOnClickListener {
                if (activity != null) {
                    getUserList(activity)

                    val adapter = CustomArrayAdapter(activity, userList, song.getId())
                    val popupWindow2 = ListPopupWindow(holder.itemView.context)
                    popupWindow2.setAdapter(adapter)
                    popupWindow2.anchorView = btnOption1
                    popupWindow2.setOnItemClickListener { parent, view, position, id ->
                        popupWindow2.dismiss() // Dismiss the popup window
                    }

                    popupWindow2.show()
                    popupWindow.dismiss() // Optionally dismiss the popup window
                }
            }

            btnOption2.setOnClickListener {
                // Handle click on button2
                if (MusicService.mListSongPlaying == null) {
                    MusicService.mListSongPlaying = mutableListOf()
                }
                MusicService.mListSongPlaying?.add(song)
                Toast.makeText(holder.itemView.context, "Song added to queue", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss() // Optionally dismiss the popup window
            }

            popupWindow.setOnDismissListener {
                // Handle dismiss actions here
            }

            val xOffset = 0
            val yOffset = 0
            popupWindow.showAsDropDown(holder.mItemSongGridBinding.imgMoreAction, xOffset, yOffset, Gravity.END)
        }
    }
    private fun getUserList(activity: Activity?) {
        if (activity == null) {
            return
        }
        val uid = firebaseAuth.currentUser?.uid.toString()
        val reference = MyApplication[activity].getListDatabaseReference(uid)

        reference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (dataSnapshot in snapshot.children) {
                    val listName = dataSnapshot.key
                    if (listName != null) {
                        userList.add(listName)
                    }
                } // Call displayList() after retrieving the data
            }

            override fun onCancelled(error: DatabaseError) {
                GlobalFuntion.showToastMessage(activity,"error")
            }
        })
    }

    override fun getItemCount(): Int {
        return mListSongs?.size ?: 0
    }

    class SongGridViewHolder(val mItemSongGridBinding: ItemSongGridBinding) : RecyclerView.ViewHolder(mItemSongGridBinding.root)
}