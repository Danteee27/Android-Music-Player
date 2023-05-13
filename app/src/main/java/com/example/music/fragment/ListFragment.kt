    package com.example.music.fragment

    import android.os.Bundle
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
    import com.example.music.constant.GlobalFuntion
    import com.example.music.databinding.FragmentListBinding
    import com.example.music.fragment.placeholder.PlaceholderContent
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
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val user = userList[position]
            holder.bind(user)
        }

        override fun getItemCount(): Int {
            return userList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(user: String) {
                // Bind the data to the views in your item layout
                // For example, you can set the user name to a TextView
                val userNameTextView = itemView.findViewById<TextView>(R.id.textView)
                userNameTextView.text = user
            }
        }
    }


    class ListFragment : Fragment() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        private var columnCount = 1
        private var mFragmentListBinding: FragmentListBinding? = null
        private var mUserList: MutableList<Song>? = null
        val userList = ArrayList<String>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            arguments?.let {
                columnCount = it.getInt(ARG_COLUMN_COUNT)
            }
        }
        private fun getUserList() {
            if (activity == null) {
                return
            }
            MyApplication[activity].getListDatabaseReference(uid)?.addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot in snapshot.children) {
                        val value = dataSnapshot.getValue(String::class.java)
                        if (value != null) {
                            userList.add(value)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    GlobalFuntion.showToastMessage(activity, getString(R.string.msg_get_date_error))
                }
            })
        }
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            getUserList()
            val view = inflater.inflate(R.layout.fragment_list, container, false)

            // Set the adapter
            if (view is RecyclerView) {
                with(view) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    adapter = MyAdapter(userList)
                }
            }
            return view
        }

        companion object {

            // TODO: Customize parameter argument names
            const val ARG_COLUMN_COUNT = "column-count"

            // TODO: Customize parameter initialization
            @JvmStatic
            fun newInstance(columnCount: Int) =
                ListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
        }
    }