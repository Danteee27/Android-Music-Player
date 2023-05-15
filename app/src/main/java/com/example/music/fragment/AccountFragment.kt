package com.example.music.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.music.R
import com.example.music.activity.MainActivity
import com.example.music.activity.SignInActivity
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth


class AccountFragment : Fragment() {

    private var mFragmentAccountBinding: FragmentAccountBinding? = null
    private var firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance();
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentAccountBinding = FragmentAccountBinding.inflate(inflater,container, false);
        mFragmentAccountBinding?.btnLogout?.setOnClickListener{onClickLogout()}
        return mFragmentAccountBinding?.root;
    }

    private fun onClickLogout() {
        if(activity == null) {
            return
        }
        val activity = activity as MainActivity?
        firebaseAuth.signOut();
        GlobalFuntion.showToastMessage(activity, "You are signed out");
        val intent = Intent(getActivity(), SignInActivity::class.java)
        startActivity(intent)
    }

}