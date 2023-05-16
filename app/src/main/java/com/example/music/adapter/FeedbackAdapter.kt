package com.example.music.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.music.databinding.ItemFeedbackBinding
import com.example.music.model.Feedback

class FeedbackAdapter(private var context: Context?, private val listFeedback: MutableList<Feedback>?
                     ) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val itemFeedbackBinding = ItemFeedbackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedbackViewHolder(itemFeedbackBinding)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = listFeedback?.get(position) ?: return

        holder.mFeedbackBinding.tvEmail.text = feedback.getEmail();
        holder.mFeedbackBinding.tvFeedback.text = feedback.getComment();

    }



    override fun getItemCount(): Int {
        return listFeedback?.size ?: 0
    }

    fun release() {
        context = null
    }

    class FeedbackViewHolder(val mFeedbackBinding: ItemFeedbackBinding) : RecyclerView.ViewHolder(mFeedbackBinding.root)
}