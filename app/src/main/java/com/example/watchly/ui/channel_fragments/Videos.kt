package com.example.watchly.ui.channel_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchly.R
import com.example.watchly.adapters.PersonalChannelVideosAdapter
import com.example.watchly.databinding.FragmentVideosBinding
import com.example.watchly.models.Video
import com.example.watchly.ui.activities.VideoPlayActivity
import com.example.watchly.ui.viewmodels.VideoViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.showSnackBar
import kotlinx.coroutines.flow.collectLatest

class Videos : Fragment() {

    private lateinit var binding: FragmentVideosBinding
    private lateinit var videoViewModel: VideoViewModel
    private lateinit var viewAdapter: PersonalChannelVideosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewAdapter = PersonalChannelVideosAdapter()
        videoViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(VideoViewModel::class.java)
        fetchVideos()
        setupRecyclerView()

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

        viewAdapter.setOnPrivateVideoClickListener {
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


        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchVideos()
        }

    }



    private fun fetchVideos() {
        videoViewModel.fetchPersonalVideos()
        lifecycleScope.launchWhenStarted {
            videoViewModel.fetchPersonalVideosState.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        hideProgressBar()
                        showSnackBar(it.message.toString())
                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        binding.swipeRefreshLayout.isRefreshing = false
                        viewAdapter.differ.submitList(it.data)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(){
        binding.videosRv.apply {
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


