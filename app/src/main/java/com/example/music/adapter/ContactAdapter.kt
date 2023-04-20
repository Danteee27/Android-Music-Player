package com.example.music.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.music.R
import com.example.music.adapter.ContactAdapter.ContactViewHolder
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.ItemContactBinding
import com.example.music.model.Contact

class ContactAdapter(private var context: Context?, private val listContact: MutableList<Contact>?,
                     private val iCallPhone: ICallPhone?) : RecyclerView.Adapter<ContactViewHolder?>() {
    interface ICallPhone {
        fun onClickCallPhone()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemContactBinding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(itemContactBinding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = listContact?.get(position) ?: return
        holder.mItemContactBinding.imgContact.setImageResource(contact.getImage())
        when (contact.getId()) {
            Contact.FACEBOOK -> holder.mItemContactBinding.tvContact.text = context?.getString(R.string.label_facebook)
            Contact.HOTLINE -> holder.mItemContactBinding.tvContact.text = context?.getString(R.string.label_call)
            Contact.GMAIL -> holder.mItemContactBinding.tvContact.text = context?.getString(R.string.label_gmail)
            Contact.SKYPE -> holder.mItemContactBinding.tvContact.text = context?.getString(R.string.label_skype)
            Contact.YOUTUBE -> holder.mItemContactBinding.tvContact.text = context?.getString(R.string.label_youtube)
            Contact.ZALO -> holder.mItemContactBinding.tvContact.text = context?.getString(R.string.label_zalo)
        }
        holder.mItemContactBinding.layoutItem.setOnClickListener {
            when (contact.getId()) {
                Contact.FACEBOOK -> context?.let { GlobalFuntion.onClickOpenFacebook(it) }
                Contact.HOTLINE -> iCallPhone?.onClickCallPhone()
                Contact.GMAIL -> GlobalFuntion.onClickOpenGmail(context)
                Contact.SKYPE -> context?.let { GlobalFuntion.onClickOpenSkype(it) }
                Contact.YOUTUBE -> context?.let { GlobalFuntion.onClickOpenYoutubeChannel(it) }
                Contact.ZALO -> context?.let { GlobalFuntion.onClickOpenZalo(it) }
            }
        }
    }

    override fun getItemCount(): Int {
        return listContact?.size ?: 0
    }

    fun release() {
        context = null
    }

    class ContactViewHolder(val mItemContactBinding: ItemContactBinding) : RecyclerView.ViewHolder(mItemContactBinding.root)
}