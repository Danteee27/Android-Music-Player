package com.example.music.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.music.R
import com.example.music.constant.Constant
import com.example.music.constant.GlobalFuntion
import com.example.music.databinding.ActivityMainBinding
import com.example.music.fragment.*
import com.example.music.model.Genre
import com.example.music.model.Song
import com.example.music.service.MusicService
import com.example.music.utils.GlideUtils
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), View.OnClickListener {
    private var mTypeScreen = TYPE_HOME
    private var mActivityMainBinding: ActivityMainBinding? = null
    private var mAction = 0
    private lateinit var firebaseAuth: FirebaseAuth


    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0)
            handleMusicAction()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        mActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mActivityMainBinding?.root)
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                IntentFilter(Constant.CHANGE_LISTENER))
        openHomeScreen()
        initListener()
        displayLayoutBottom()

        Log.d("Auth", firebaseAuth.currentUser?.email.toString());

    }

    private fun initToolbar(title: String?) {
        mActivityMainBinding?.header?.imgLeft?.setImageResource(R.drawable.ic_menu_left)
        mActivityMainBinding?.header?.tvTitle?.text = title
    }

    private fun initListener() {
        mActivityMainBinding?.header?.imgLeft?.setOnClickListener(this)
        mActivityMainBinding?.header?.layoutPlayAll?.setOnClickListener(this)
        mActivityMainBinding?.menuLeft?.layoutClose?.setOnClickListener(this)
        mActivityMainBinding?.menuLeft?.tvMenuHome?.setOnClickListener(this)
        mActivityMainBinding?.menuLeft?.tvMenuMylist?.setOnClickListener(this)
        mActivityMainBinding?.menuLeft?.tvMenuAllSongs?.setOnClickListener(this)
        mActivityMainBinding?.menuLeft?.tvMenuFeaturedSongs?.setOnClickListener(this)
        mActivityMainBinding?.menuLeft?.tvMenuPopularSongs?.setOnClickListener(this)
        mActivityMainBinding?.menuLeft?.tvMenuNewSongs?.setOnClickListener(this)
        mActivityMainBinding?.menuLeft?.tvMenuLikedSongs?.setOnClickListener(this)
        mActivityMainBinding?.menuLeft?.tvMenuFeedback?.setOnClickListener(this)
        mActivityMainBinding?.menuLeft?.tvMenuContact?.setOnClickListener(this)
        mActivityMainBinding?.menuLeft?.tvMenuLibrary?.setOnClickListener(this)
        mActivityMainBinding?.menuLeft?.tvMenuAccount?.setOnClickListener(this)
        mActivityMainBinding?.layoutBottom?.imgPrevious?.setOnClickListener(this)
        mActivityMainBinding?.layoutBottom?.imgPlay?.setOnClickListener(this)
        mActivityMainBinding?.layoutBottom?.imgNext?.setOnClickListener(this)
        mActivityMainBinding?.layoutBottom?.imgClose?.setOnClickListener(this)
        mActivityMainBinding?.layoutBottom?.layoutText?.setOnClickListener(this)
        mActivityMainBinding?.layoutBottom?.imgSong?.setOnClickListener(this)
        if(!isAdmin()){
            mActivityMainBinding?.menuLeft?.layoutMenuUpload?.visibility = View.GONE
            mActivityMainBinding?.menuLeft?.layoutMenuComment?.visibility = View.GONE
        }else{
            mActivityMainBinding?.menuLeft?.tvMenuUpload?.setOnClickListener(this)
            mActivityMainBinding?.menuLeft?.tvMenuComment?.setOnClickListener(this)
        }
    }

   fun isAdmin(): Boolean {
        return  firebaseAuth.currentUser?.email.toString().equals("admin@gmail.com")
    }
    private fun openHomeScreen() {
        replaceFragment(HomeFragment())
        mTypeScreen = TYPE_HOME
        initToolbar(getString(R.string.app_name))
        displayLayoutPlayAll()
        displayShuffle()
    }

    fun openAllSongsScreen(){
        replaceFragment(AllSongsFragment())
        mTypeScreen = TYPE_ALL_SONGS
        initToolbar(getString(R.string.menu_all_songs))
        displayLayoutPlayAll()
        displayShuffle()
    }

    fun openGenreSongsScreen(genre:Genre?){
        replaceFragment(GenreSongsFragment(genre))
        mTypeScreen = TYPE_GENRE_SONGS
        initToolbar(genre?.getName() ?:"Genre")
        displayLayoutPlayAll()
        displayShuffle()
    }

    fun openPopularSongsScreen() {
        replaceFragment(PopularSongsFragment())
        mTypeScreen = TYPE_POPULAR_SONGS
        initToolbar(getString(R.string.menu_popular_songs))
        displayLayoutPlayAll()
        displayShuffle()
    }

    fun openNewSongsScreen() {
        replaceFragment(NewSongsFragment())
        mTypeScreen = TYPE_NEW_SONGS
        initToolbar(getString(R.string.menu_new_songs))
        displayLayoutPlayAll()
        displayShuffle()
    }
    fun openListScreen() {
        replaceFragment(ListFragment())
        mTypeScreen = TYPE_LIST
        initToolbar("My Playlists")
        displayLayoutPlayAll()
        displayShuffle()
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.layout_close -> mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
            R.id.img_left -> mActivityMainBinding?.drawerLayout?.openDrawer(GravityCompat.START)
            R.id.tv_menu_home -> {
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                openHomeScreen()
            }
            R.id.tv_menu_all_songs -> {
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                replaceFragment(AllSongsFragment())
                mTypeScreen = TYPE_ALL_SONGS
                initToolbar(getString(R.string.menu_all_songs))
                displayLayoutPlayAll()
                displayShuffle()
            }
            R.id.tv_menu_featured_songs -> {
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                replaceFragment(FeaturedSongsFragment())
                mTypeScreen = TYPE_FEATURED_SONGS
                initToolbar(getString(R.string.menu_featured_songs))
                displayLayoutPlayAll()
                displayShuffle()
            }
            R.id.tv_menu_popular_songs -> {
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                openPopularSongsScreen()
            }
            R.id.tv_menu_new_songs -> {
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                openNewSongsScreen()
            }
            R.id.tv_menu_mylist -> {
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                openListScreen()
            }
            R.id.tv_menu_feedback -> {
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                replaceFragment(FeedbackFragment())
                mTypeScreen = TYPE_FEEDBACK
                initToolbar(getString(R.string.menu_feedback))
            }
            R.id.tv_menu_contact -> {
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                replaceFragment(ContactFragment())
                mTypeScreen = TYPE_CONTACT
                initToolbar(getString(R.string.menu_contact))
            }
            R.id.tv_menu_account -> {
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                replaceFragment(AccountFragment())
                mTypeScreen = TYPE_ACCOUNT
                initToolbar("Account")
                displayLayoutPlayAll()
                displayShuffle()
            }
            R.id.tv_menu_upload->{
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                replaceFragment(UploadFragment())
                mTypeScreen = TYPE_UPLOAD
                initToolbar(getString(R.string.menu_upload))
            }
            R.id.tv_menu_comment->{
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                replaceFragment(AdminFeedbackFragment())
                mTypeScreen = TYPE_ADMIN_FEEDBACK
                initToolbar(getString(R.string.menu_user_feedback))
            }
            R.id.tv_menu_liked_songs->{
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                replaceFragment(LikedSongFragment())
                initToolbar("Liked Song")
                mTypeScreen = TYPE_FEATURED_SONGS
                displayLayoutPlayAll()
                displayShuffle()
            }
            R.id.tv_menu_library->{
                mActivityMainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
                replaceFragment(LibraryFragment())
                mTypeScreen = TYPE_LIBRARY
                initToolbar(getString(R.string.menu_library))
            }
            R.id.img_previous -> clickOnPrevButton()
            R.id.img_play -> clickOnPlayButton()
            R.id.img_next -> clickOnNextButton()
            R.id.img_close -> clickOnCloseButton()
            R.id.layout_text -> openPlayMusicActivity()
            R.id.img_song -> openPlayMusicActivity()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame, fragment).commitAllowingStateLoss()
    }

    private fun showConfirmExitApp() {
        MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive { _: MaterialDialog?, _: DialogAction? -> finish() }
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show()
    }

    private fun displayLayoutPlayAll() {
        when (mTypeScreen) {
            TYPE_ALL_SONGS -> mActivityMainBinding?.header?.layoutPlayAll?.visibility = View.VISIBLE
            TYPE_FEATURED_SONGS -> mActivityMainBinding?.header?.layoutPlayAll?.visibility = View.VISIBLE
            TYPE_POPULAR_SONGS -> mActivityMainBinding?.header?.layoutPlayAll?.visibility = View.VISIBLE
            TYPE_NEW_SONGS -> mActivityMainBinding?.header?.layoutPlayAll?.visibility = View.VISIBLE
            TYPE_GENRE_SONGS -> mActivityMainBinding?.header?.layoutPlayAll?.visibility = View.VISIBLE
            TYPE_LIST-> mActivityMainBinding?.header?.layoutPlayAll?.visibility = View.VISIBLE
            else -> mActivityMainBinding?.header?.layoutPlayAll?.visibility = View.GONE
        }
    }

    private fun displayShuffle() {
        when (mTypeScreen) {
            TYPE_ALL_SONGS -> mActivityMainBinding?.header?.layoutShuffle?.visibility = View.VISIBLE
            TYPE_FEATURED_SONGS -> mActivityMainBinding?.header?.layoutShuffle?.visibility = View.VISIBLE
            TYPE_POPULAR_SONGS -> mActivityMainBinding?.header?.layoutShuffle?.visibility = View.VISIBLE
            TYPE_NEW_SONGS -> mActivityMainBinding?.header?.layoutShuffle?.visibility = View.VISIBLE
            TYPE_GENRE_SONGS -> mActivityMainBinding?.header?.layoutShuffle?.visibility = View.VISIBLE
            TYPE_LIST-> mActivityMainBinding?.header?.layoutShuffle?.visibility = View.VISIBLE
            else -> mActivityMainBinding?.header?.layoutShuffle?.visibility = View.GONE
        }
    }

    private fun displayLayoutBottom() {
        if (MusicService.mPlayer == null) {
            mActivityMainBinding?.layoutBottom?.layoutItem?.visibility = View.GONE
            return
        }
        mActivityMainBinding?.layoutBottom?.layoutItem?.visibility = View.VISIBLE
        showInforSong()
        showStatusButtonPlay()
    }

    private fun handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            mActivityMainBinding?.layoutBottom?.layoutItem?.visibility = View.GONE
            return
        }
        mActivityMainBinding?.layoutBottom?.layoutItem?.visibility = View.VISIBLE
        showInforSong()
        showStatusButtonPlay()
    }

    private fun showInforSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying!!.isEmpty()) {
            return
        }
        val currentSong: Song? = MusicService.mListSongPlaying!![MusicService.mSongPosition]
        mActivityMainBinding?.layoutBottom?.tvSongName?.text = currentSong?.getTitle()
        mActivityMainBinding?.layoutBottom?.tvArtist?.text = currentSong?.getArtist()
        mActivityMainBinding?.layoutBottom?.imgSong?.let { GlideUtils.loadUrl(currentSong?.getImage(), it) }
    }

    private fun showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            mActivityMainBinding?.layoutBottom?.imgPlay?.setImageResource(R.drawable.ic_pause_black)
        } else {
            mActivityMainBinding?.layoutBottom?.imgPlay?.setImageResource(R.drawable.ic_play_black)
        }
    }

    private fun clickOnPrevButton() {
        GlobalFuntion.startMusicService(this, Constant.PREVIOUS, MusicService.mSongPosition)
    }

    private fun clickOnNextButton() {
        GlobalFuntion.startMusicService(this, Constant.NEXT, MusicService.mSongPosition)
    }

    private fun clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFuntion.startMusicService(this, Constant.PAUSE, MusicService.mSongPosition)
        } else {
            GlobalFuntion.startMusicService(this, Constant.RESUME, MusicService.mSongPosition)
        }
    }

    private fun clickOnCloseButton() {
        GlobalFuntion.startMusicService(this, Constant.CANNEL_NOTIFICATION, MusicService.mSongPosition)
    }

    private fun openPlayMusicActivity() {
        GlobalFuntion.startActivity(this, PlayMusicActivity::class.java)
    }

    fun getActivityMainBinding(): ActivityMainBinding? {
        return mActivityMainBinding
    }

    override fun onBackPressed() {
        showConfirmExitApp()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver)
    }

    companion object {
        const val TYPE_HOME = 1
        const val TYPE_ALL_SONGS = 2
        const val TYPE_FEATURED_SONGS = 3
        const val TYPE_POPULAR_SONGS = 4
        const val TYPE_NEW_SONGS = 5
        const val TYPE_FEEDBACK = 6
        const val TYPE_CONTACT = 7
        const val TYPE_UPLOAD = 8
        const val TYPE_LIBRARY = 9
        const val TYPE_GENRE_SONGS = 10
        const val TYPE_LIST = 11
        const val TYPE_ACCOUNT = 12
        const val TYPE_ADMIN_FEEDBACK = 13
    }
}