package com.example.watchly.ui.fragments.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchly.adapters.VideosHomeDisplayAdapter
import com.example.watchly.databinding.FragmentHomeBinding
import com.example.watchly.models.Video
import com.example.watchly.ui.activities.VideoPlayActivity
import com.example.watchly.ui.viewmodels.VideoViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.initLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showSnackBar
import kotlinx.coroutines.flow.collect


class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var videoViewModel: VideoViewModel
    private val args by navArgs<HomeArgs>()
    private lateinit var viewAdapter : VideosHomeDisplayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLoadingDialog()
        viewAdapter = VideosHomeDisplayAdapter(parentFragmentManager)
        videoViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(VideoViewModel::class.java)

        videoViewModel.fetchHomeVideos()
        lifecycleScope.launchWhenStarted {
            videoViewModel.fetchHomeVideosState.collect {
                when(it){
                    is Resource.Error -> {
                        hideLoadingDialog()
                        showSnackBar(it.message.toString())
                    }
                    is Resource.Loading -> {
                        showLoadingDialog()
                    }
                    is Resource.Success -> {
                        hideLoadingDialog()
                        viewAdapter.differ.submitList(it.data)
                    }
                }
            }
        }

        binding.VideosDisplay.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = viewAdapter
        }

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
            startActivity(intent)
        }


    }



}