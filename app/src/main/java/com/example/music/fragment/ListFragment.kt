    package com.example.music.fragment

    import android.app.Activity
    import android.os.Bundle
    import android.util.Log
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.GridLayoutManager
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageButton
    import android.widget.TextView
    import com.example.music.MyApplication
    import com.example.music.R
    import com.example.music.adapter.SongAdapter
    import com.example.music.constant.GlobalFuntion
    import com.example.music.databinding.FragmentAllSongsBinding
    import com.example.music.databinding.FragmentListBinding
    import com.example.music.fragment.placeholder.PlaceholderContent
    import com.example.music.listener.IOnClickSongItemListener
    import com.example.music.model.Song
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.ValueEventListener
    import java.util.ArrayList

    /**
     * A fragment representing a list of Items.
     */
    class MyAdapter(private var userList: List<String>) :
        RecyclerView.Adapter<MyAdapter.ViewHolder?>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val listName = userList[position]
            holder.bind(listName)
        }

        override fun getItemCount(): Int {
            return userList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val deleteButton: ImageButton = itemView.findViewById(R.id.delete)
            fun bind(listName: String) {
                val listNameTextView = itemView.findViewById<TextView>(R.id.textView)
                listNameTextView.text = listName
                deleteButton.setOnClickListener {
                    // Handle delete button click event
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        // Perform delete operation for the item at the clicked position
                        val deletedItem = userList[position]
                        val mutableList = userList.toMutableList()
                        mutableList.removeAt(position)
                        userList = mutableList.toList()
                        notifyItemRemoved(position)

                        // Perform any additional actions related to the delete operation
                        val activity = itemView.context as Activity
                        val firebaseAuth = FirebaseAuth.getInstance()
                        deleteItemFromDatabase(activity, listName, firebaseAuth)

                    }
                }
            }
        }

    }

    private fun deleteItemFromDatabase(activity: Activity, playlistName: String,firebaseAuth: FirebaseAuth) {
        val currentUserUid = firebaseAuth.currentUser?.uid
        val playlistRef = MyApplication[activity].getListDatabaseReference(currentUserUid)

        // Get a reference to the specific playlist node using its name
        val playlistNodeRef = playlistRef?.child(playlistName)

        // Remove the playlist node and its child nodes
        playlistNodeRef?.removeValue()
    }
    class ListFragment : Fragment() {
        private lateinit var firebaseAuth: FirebaseAuth
        private var mFragmentListBinding: FragmentListBinding? = null
        var userList = ArrayList<String>()
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            firebaseAuth = FirebaseAuth.getInstance()
            mFragmentListBinding = FragmentListBinding.inflate(inflater, container, false)
            //println(firebaseAuth.currentUser?.uid.toString())
            getUserList()
            return mFragmentListBinding?.root
        }

        private fun displayList() {
            if (activity == null) {
                return
            }
            val linearLayoutManager = LinearLayoutManager(activity)
            mFragmentListBinding?.recyclerView?.layoutManager = linearLayoutManager
            val adapter = MyAdapter(userList)
            mFragmentListBinding?.recyclerView?.adapter = adapter
            val button = mFragmentListBinding?.root?.findViewById<Button>(R.id.button2)
            button?.setOnClickListener {
                val editText = mFragmentListBinding?.root?.findViewById<EditText>(R.id.editListName)
                val str_listName = editText?.text.toString()
                //println(str_listName)
                addNewPlaylist(str_listName)
                editText?.setText("")
            }
        }

        private fun addNewPlaylist(playlistName: String) {
            val currentUserUid = firebaseAuth.currentUser?.uid
            println(currentUserUid)
            val playlistRef = MyApplication[activity].getListDatabaseReference(currentUserUid)

            // Create a new child node with the playlist name as the key
            val newPlaylistRef = playlistRef?.child(playlistName)

            // Set a value to the newly created child node (e.g., an empty string)
            newPlaylistRef?.setValue("")

            // Alternatively, you can set a custom object as the value
            // val playlist = Playlist(playlistName, ...)
            // newPlaylistRef?.setValue(playlist)
        }
        private fun getUserList() {
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
                            println(listName)
                            userList.add(listName)
                        }
                    }
                    displayList() // Call displayList() after retrieving the data
                }

                override fun onCancelled(error: DatabaseError) {
                    GlobalFuntion.showToastMessage(activity, getString(R.string.msg_get_date_error))
                }
            })
        }
    }