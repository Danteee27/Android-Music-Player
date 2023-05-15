package com.example.music.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.music.R
import com.example.music.adapter.FeedbackAdapter
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.FragmentAdminFeedbackBinding
import com.example.music.model.Feedback
import com.example.music.model.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminFeedbackFragment : Fragment() {

    private var mFeedBackAdapter : FeedbackAdapter? = null
    private var firebaseAuth : FirebaseAuth? = FirebaseAuth.getInstance()
    private var firebaseDatabase : FirebaseDatabase? = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var mFeedbackFragmentBinding = FragmentAdminFeedbackBinding.inflate(inflater,container,false)
        mFeedBackAdapter = FeedbackAdapter(activity,getFeedbacks());
        // Inflate the layout for this fragment
        val layoutManager = GridLayoutManager(activity, 3)
        mFeedbackFragmentBinding.rcvData.isNestedScrollingEnabled = false
        mFeedbackFragmentBinding.rcvData.isFocusable = false
        mFeedbackFragmentBinding.rcvData.layoutManager = layoutManager
        mFeedbackFragmentBinding.rcvData.adapter = mFeedBackAdapter
        return mFeedbackFragmentBinding.root
    }

    private fun getFeedbacks(): MutableList<Feedback>{
        val feedbackArrayList: MutableList<Feedback> = ArrayList()
        firebaseDatabase?.getReference("feedback")?.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                   feedbackArrayList?.add(dataSnapshot.getValue(Feedback::class.java))
                }

            }

            override fun onCancelled(error: DatabaseError) {
                GlobalFuntion.showToastMessage(activity, "Feedbacks")
            }
        })
        return feedbackArrayList;
    }

    override fun onDestroy() {
        super.onDestroy()
        mFeedBackAdapter?.release()
    }

}