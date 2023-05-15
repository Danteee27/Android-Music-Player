package com.example.music.adapter

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.appcompat.widget.ListPopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.example.music.MyApplication
import com.example.music.R
import com.example.music.adapter.SongAdapter.SongViewHolder
import com.example.music.constant.Constant
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.ItemSongBinding
import com.example.music.databinding.SongButtonBinding
import com.example.music.listener.IOnClickSongItemListener
import com.example.music.model.Song
import com.example.music.service.MusicService
import com.example.music.service.MusicService.Companion.mListSongPlaying
import com.example.music.utils.GlideUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList

class CustomArrayAdapter(context: Context, userList: List<String>, private val songId: Int) :
    ArrayAdapter<String>(context, 0, userList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_small_list, parent, false)

        val listNameTextView = listItemView.findViewById<TextView>(R.id.button3)
        val listName = getItem(position)
        if (listName != null) {
            if (listName.length > 10) {
                val truncatedListName = TextUtils.ellipsize(listName, listNameTextView.paint, 100f, TextUtils.TruncateAt.END)
                listNameTextView.text = truncatedListName
            } else {
                listNameTextView.text = listName
            }
        } else {
            listNameTextView.text = ""
        }

        listNameTextView.setOnClickListener {
            handleButtonClick(listName, songId)
        }

        return listItemView
    }
    private fun handleButtonClick(listName: String?, songId: Int) {
        if (listName != null) {
            println("Selected playlist: $listName")
            println("Song ID: $songId")

            // Add the song ID to the specified playlist in the Firebase Realtime Database
            val firebaseAuth = FirebaseAuth.getInstance()
            val mFirebaseDatabase = FirebaseDatabase.getInstance(Constant.FIREBASE_URL)
            val uid = firebaseAuth.currentUser?.uid
            if (uid != null) {
                val playlistRef = mFirebaseDatabase?.getReference("/playList/$uid/$listName")
                playlistRef?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val childrenCount = snapshot.childrenCount
                        val newIndex = childrenCount + 1
                        playlistRef.child(newIndex.toString()).setValue(songId)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Song ID added to $listName successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(context, "Failed to add song ID to $listName: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to $listName playlist: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
class SongAdapter(private val mListSongs: MutableList<Song>?,
                  private val iOnClickSongItemListener: IOnClickSongItemListener?) : RecyclerView.Adapter<SongViewHolder?>() {

    private var firebaseAuth: FirebaseAuth =  FirebaseAuth.getInstance()
    var userList = ArrayList<String>()
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

    class SongViewHolder(val mItemSongBinding: ItemSongBinding) : RecyclerView.ViewHolder(mItemSongBinding.root)
}