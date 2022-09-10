package com.example.watchly.ui.activities


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.watchly.R
import com.example.watchly.databinding.ActivityMainBinding
import com.example.watchly.models.HomeRecyclerViewItem
import com.example.watchly.models.Video
import com.example.watchly.ui.fragments.main.Gaming
import com.example.watchly.ui.fragments.main.Home
import com.example.watchly.ui.fragments.main.Profile
import com.example.watchly.ui.search.SearchFragment
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.DataStore
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import java.security.Key


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataStore: DataStore
    private lateinit var userViewModel: UserViewModel

    private var home = Home()
    private var search = SearchFragment()
    private var gaming = Gaming()
    private var profile = Profile()
    var active: Fragment = home
    var fm = supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        dataStore = DataStore(this)

        fm.beginTransaction().add(R.id.flFragment, profile, "4").hide(profile).commit()
        fm.beginTransaction().add(R.id.flFragment, gaming, "3").hide(gaming).commit()
        fm.beginTransaction().add(R.id.flFragment, search, "2").hide(search).commit()
        fm.beginTransaction().add(R.id.flFragment,home, "1").commit()
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        userViewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(UserViewModel::class.java)

        binding.Add.setOnClickListener {
            Intent(this, VideoUploadActivity::class.java).also {
                startActivity(it)
            }
        }
        val userHashMap = HashMap<String, Any>()
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            userHashMap["fcmToken"] = it
            userViewModel.updateUserData(userHashMap)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.AppSettings ->{
                Intent(this, AppSettings::class.java).apply {
                    startActivity(this)
                }
            }
            R.id.Uploading -> {
                Intent(this, VideoUploadActivity::class.java).also {
                    startActivity(it)
                }
            }


        }
        return true
    }


    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home2 -> {
                    fm.beginTransaction().hide(active).show(home).commit()
                    active = home
                    setToolbarVisibility(View.VISIBLE)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.gaming -> {
                    fm.beginTransaction().hide(active).show(gaming).commit()
                    active = gaming
                    setToolbarVisibility(View.VISIBLE)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    fm.beginTransaction().hide(active).show(profile).commit()
                    active = profile
                    setToolbarVisibility(View.VISIBLE)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.search ->{
                    fm.beginTransaction().hide(active).show(search).commit()
                    active = search
                    setToolbarVisibility(View.GONE)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun setToolbarVisibility(visibility: Int){
        binding.toolbar.visibility = visibility
    }





}