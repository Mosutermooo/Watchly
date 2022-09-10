package com.example.watchly.ui.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.databinding.ActivityChannelSettingsBinding
import com.example.watchly.models.Channel
import com.example.watchly.ui.dialogs.ChannelDescriptionChangeDialog
import com.example.watchly.ui.dialogs.ChannelNameChangeDialog
import com.example.watchly.ui.viewmodels.ChannelViewModel
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Constants.changeChannelDescriptionDialogTAG
import com.example.watchly.uils.Constants.changeChannelNameDialogTAG
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.openGallery
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialogFragment
import com.example.watchly.uils.ReusableResource.showSnackBar
import com.example.watchly.uils.ReusableResource.uid

class ChannelSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChannelSettingsBinding
    private lateinit var channelViewModel: ChannelViewModel
    private lateinit var channelModel: Channel
    private var channelImageUri : Uri? = null
    private var channelBannerUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        channelViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(ChannelViewModel::class.java)

        ReusableResource.setupToolBarOnBackPressed(
            binding.toolbar,
            getString(R.string.channel_settings),
            getString(R.string.change_channel_settings),
            this
        )
        channelViewModel.getChannelData(uid())
        channelViewModel.channelState.observe(this){
            when(it){
                is Resource.Error -> {
                    Toast.makeText(
                        this,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    if(it.data != null){
                        channelModel = it.data
                        displayData(it.data)
                    }
                }
            }
        }


        binding.ChangeChannelName.setOnClickListener {
            ChannelNameChangeDialog(
                channelName = channelModel.name.toString(),
                channelId = channelModel.channelId.toString()
            ).show(
                supportFragmentManager,
                changeChannelNameDialogTAG
            )
        }

        binding.ChangeChannelDescription.setOnClickListener {
            ChannelDescriptionChangeDialog(
                channelDescription = channelModel.description.toString(),
                channelId = channelModel.channelId.toString()
            ).show(
                supportFragmentManager,
                changeChannelDescriptionDialogTAG
            )
        }

        binding.channelImage.setOnClickListener {
            openGallery(103)
        }
        binding.channelBanner.setOnClickListener {
            openGallery(104)
        }

        binding.updateImage.setOnClickListener {
            channelViewModel.updateChannelImageAndBanner(
                channelBannerUri,
                channelImageUri,
                channelModel.channelId.toString()
            )
            channelViewModel.channelUpdateState.observe(this){
                when(it){
                    is Resource.Error -> {
                        hideLoadingDialog(
                            Constants.LoadingDialogTag,
                            this
                        )
                        Toast.makeText(
                            this,
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.updateImage.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        showLoadingDialogFragment(this)
                    }
                    is Resource.Success -> {
                        hideLoadingDialog(
                            Constants.LoadingDialogTag,
                            this
                        )
                        binding.updateImage.visibility = View.GONE
                    }
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data?.data != null){
            when(requestCode){
                103 -> {
                    channelImageUri = data.data
                    binding.channelImage.setImageURI(channelImageUri)
                    binding.updateImage.visibility = View.VISIBLE
                }
                104 -> {
                    channelBannerUri = data.data
                    binding.channelBanner.setImageURI(channelBannerUri)
                    binding.updateImage.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun displayData(channel: Channel) {
        Glide.with(this)
            .load(channel.image)
            .placeholder(R.color.GrayTextColor)
            .centerCrop()
            .into(binding.channelImage)
        Glide.with(this)
            .load(channel.banner)
            .placeholder(android.R.color.darker_gray)
            .centerCrop()
            .into(binding.channelBanner)
        binding.channelName.text = channel.name
        if(channel.description != "" && channel.description != null){
            binding.channelDescription.text = channel.description
        }else{
            binding.channelDescription.text = getString(R.string.no_description)
        }

    }

}