    package com.example.music.fragment

    import android.os.Bundle
    import android.util.Log
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.GridLayoutManager
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
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

    class MyAdapter(private val userList: List<String>) :
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
            fun bind(listName: String) {
                val listNameTextView = itemView.findViewById<TextView>(R.id.list_name_text)
                listNameTextView.text = listName
                println(listName)
            }
        }

    }


    class ListFragment : Fragment() {
        private lateinit var firebaseAuth: FirebaseAuth
        private var mFragmentListBinding: FragmentListBinding? = null
        val userList = ArrayList<String>()

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            firebaseAuth = FirebaseAuth.getInstance()
            mFragmentListBinding = FragmentListBinding.inflate(inflater, container, false)
            println(firebaseAuth.currentUser?.uid.toString())
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
        }

        private fun getUserList() {
            if (activity == null) {
                return
            }
            MyApplication[activity].getListDatabaseReference(firebaseAuth.currentUser?.uid.toString())?.addValueEventListener(object : ValueEventListener {
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