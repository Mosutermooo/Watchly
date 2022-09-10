package com.example.watchly.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.watchly.R
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.Resource
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChannelActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_activity)
        userViewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(UserViewModel::class.java)
        findViewById<FloatingActionButton>(R.id.AddChannel).setOnClickListener {
            userViewModel.getUserData()
            userViewModel.userData.observe(this){
                when(it){
                    is Resource.Success -> {
                        if(it.data != null){
                            if(it.data.channel != null){
                                Intent(this, VideoUploadActivity::class.java).also {i->
                                    startActivity(i)
                                }
                            }else{
                                Toast.makeText(
                                    this,
                                    getString(R.string.please_create_a_channel_first),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}