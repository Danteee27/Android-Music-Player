package com.example.myapplication.di

import android.app.Application
import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

//@Module
//@InstallIn(Application::class)
object AppMoudule {

//    @Singleton
//    @Provides
//    fun provideGlideInstance(
//       @ApplicationContext context: Context
//    ) = Glide.with(context).setDefaultRequestOptions(
//        RequestOptions().placeholder(R.drawable.ic_launcher_foreground)
//            .error(R.drawable.ic_launcher_foreground)
//            .diskCacheStrategy(DiskCacheStrategy.DATA)
//        //note: in instruction ic_laucher_foreground = ic_image
//    )
}