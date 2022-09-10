package com.example.watchly.ui.fragments.user_perspective_chanel

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.adapters.PersonalChannelVideosAdapter
import com.example.watchly.databinding.FragmentChannelHome2Binding
import com.example.watchly.models.Channel
import com.example.watchly.models.Video
import com.example.watchly.ui.activities.VideoPlayActivity
import com.example.watchly.ui.viewmodels.ChannelViewModel
import com.example.watchly.ui.viewmodels.VideoViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.showSnackBar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

class UserPrespChannelHome : Fragment() {

    private lateinit var binding: FragmentChannelHome2Binding
    private lateinit var channelViewModel: ChannelViewModel
    private lateinit var videoViewModel: VideoViewModel
    private lateinit var viewAdapter: PersonalChannelVideosAdapter
    private lateinit var channel: Channel
    private var isSubscribed: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChannelHome2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channelViewModel = ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        ).create(ChannelViewModel::class.java)
        videoViewModel = ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        ).create(VideoViewModel::class.java)

        channelViewModel.getCachedChannel().observe(viewLifecycleOwner){
            if(it != null){
                displayData(it)
                channel = it
            }
        }



        binding.Subscribe.setOnClickListener {
            if(!isSubscribed){
                channelViewModel.subscribe(channel.channelId.toString(), channel.ownedBy.toString())
                lifecycleScope.launchWhenStarted {
                    channelViewModel.subscribeState.collect {
                        when(it){
                            is Resource.Success -> {
                                alreadySubscribed()
                            }
                        }
                    }
                }
            }else{
                channelViewModel.unSubscribe(channel.channelId.toString())
                lifecycleScope.launchWhenStarted {
                    channelViewModel.unSubscribeState.collect {
                        when(it){
                            is Resource.Success -> {
                                notSubscribed()
                            }
                        }
                    }
                }
            }
            isSubscribed = !isSubscribed
        }





    }
    private fun alreadySubscribed(){
        binding.Subscribe.setBackgroundResource(R.drawable.channel_settings_button_click)
        binding.Subscribe.text = getString(R.string.unSubscribe)
    }


    private fun notSubscribed(){
        binding.Subscribe.setBackgroundResource(R.drawable.manage_videos_background)
        binding.Subscribe.text = getString(R.string.Subscribe)
    }

    private fun isAlreadySubscribed(ownedBy: String?) {
        channelViewModel.isAlreadySubscribed(ownedBy.toString())
        lifecycleScope.launchWhenStarted {
            channelViewModel.isAlredySubscriber.collect {
                when(it){
                    is Resource.Error -> {
                        isSubscribed = false
                        notSubscribed()
                    }
                    is Resource.Success -> {
                        isSubscribed = true
                        alreadySubscribed()
                    }
                }
            }
        }
    }

    private fun displayData(channel: Channel){
        Glide.with(requireContext())
            .load(channel.image)
            .placeholder(R.color.GrayTextColor)
            .centerCrop()
            .into(binding.channelImage)
        Glide.with(requireContext())
            .load(channel.banner)
            .placeholder(android.R.color.transparent)
            .centerCrop()
            .into(binding.channelBanner)
        binding.channelName.text = channel.name
        binding.channelDescription.text = channel.description

        setupRecyclerView()
        fetchVideos(channel.ownedBy)

        viewAdapter.setOnVideoClickListener {
            val intent =  Intent(requireActivity(), VideoPlayActivity::class.java)
            val video = Video(
                it.id,
                it.name,
                it.description,
                it.thumbnail,
                it.video,
                it.visibility,
                it.channel,
                it.user,
                it.videoId,
            )
            intent.putExtra("video", video)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        isAlreadySubscribed(channel.channelId.toString())

    }


    private fun fetchVideos(ownedBy: String?) {
        videoViewModel.fetchChannelVideos(ownedBy.toString())
        lifecycleScope.launchWhenStarted {
            videoViewModel.fetchChannelVideosState.collectLatest {
                when(it){
                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar(it.message.toString())
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        viewAdapter.differ.submitList(it.data)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(){
        viewAdapter = PersonalChannelVideosAdapter()
        binding.uploadsRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = viewAdapter
        }
    }

    private fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        binding.progressBar.visibility = View.GONE
    }



}