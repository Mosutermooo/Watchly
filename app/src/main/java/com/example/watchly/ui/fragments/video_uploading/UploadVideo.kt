package com.example.watchly.ui.fragments.video_uploading

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.databinding.FragmentUploadVideoBinding
import com.example.watchly.models.Channel
import com.example.watchly.models.UploadVideo
import com.example.watchly.services.UploadVideoService
import com.example.watchly.ui.viewmodels.ChannelViewModel
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.ui.viewmodels.VideoViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.initLoadingDialog
import com.example.watchly.uils.ReusableResource.openGallery
import com.example.watchly.uils.ReusableResource.setFragmentResultListener
import com.example.watchly.uils.ReusableResource.showSnackBar
import com.example.watchly.uils.ReusableResource.thumbnailUri
import com.example.watchly.uils.ReusableResource.uid
import com.example.watchly.use_cases.VideoId


class UploadVideo : Fragment() {

    private lateinit var binding: FragmentUploadVideoBinding
    private lateinit var channelViewModel: ChannelViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var videoViewModel: VideoViewModel
    private lateinit var channel: Channel
    private val args by navArgs<UploadVideoArgs>()
    private var downloadedThumbnailUri : Uri? = null
    private var visibility : String = "public"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentUploadVideoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLoadingDialog()

        channelViewModel = ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        ).create(ChannelViewModel::class.java)
        videoViewModel = ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        ).create(VideoViewModel::class.java)

        userViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(UserViewModel::class.java)


        ReusableResource.setupToolBarOnBackPressed(
            binding.toolbar,
            getString(R.string.add_details_video_uploading),
            null,
            requireActivity() as AppCompatActivity
        )

        channelData()

        binding.SetVideoVisibility.setOnClickListener {
            setFragmentResultListener("visibilityResult"){_, result ->
               result.getString("visibility")?.let {
                   visibility = it
                   if(visibility == "public"){
                       binding.visibility.text = getString(R.string.public_video)
                   }else{
                       binding.visibility.text = getString(R.string.private_video)
                   }
               }
            }
            findNavController().navigate(R.id.action_uploadVideo_to_videoVisibility)
        }
        binding.customThumbnail.setOnClickListener {
            openGallery(1005)
        }
        binding.UploadVideo.setOnClickListener {
           startUpload()
        }


    }

    private fun startUpload() {
        if(binding.etName.text.toString().isEmpty()){
            showSnackBar(getString(R.string.name_mission_upload_video))
            return
        }
        if(thumbnailUri == null){
            showSnackBar(getString(R.string.please_add_a_thumbnail_for_the_video))
            return
        }
        cacheUpload()
    }

    private fun cacheUpload() {
        val uploadVideo = UploadVideo(
            null,
            binding.etName.text.toString(),
            binding.etVideoDescription.text.toString(),
            thumbnailUri.toString(),
            args.galleryVideo.absolutePath,
            visibility,
            channel.channelId.toString(),
            uid(),
            VideoId().generate()
        )
        videoViewModel.cacheUploadVideoToDB(uploadVideo)
        Intent(requireActivity(), UploadVideoService::class.java).also {
            requireActivity().startService(it)
        }
        requireActivity().finish()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode  == Activity.RESULT_OK && data?.data != null){
            when (requestCode){
                1005 -> {
                    thumbnailUri = data.data
                    binding.VideoImage.setImageURI(thumbnailUri)
                }
            }
        }
    }

    private fun channelData(){
        channelViewModel.getChannelData(uid())
        channelViewModel.channelState.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    if(it.data != null){
                        channel = it.data
                        displayData()
                    }else{
                        showSnackBar(getString(R.string.something_went_wrong_getting_channel_data))
                    }
                }
            }
        }
    }
    private fun displayData(){
        Glide.with(this)
            .load(channel.image)
            .centerCrop().placeholder(R.color.GrayTextColor)
            .into(binding.channelImage)
        binding.channelName.text = channel.name
        Glide.with(this).load("file://" + args.galleryVideo.thumb)
            .skipMemoryCache(false)
            .centerCrop()
            .placeholder(R.color.GrayTextColor)
            .into(binding.VideoImage)

    }





}