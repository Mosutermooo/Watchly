package com.example.watchly.ui.fragments.user_perspective_chanel

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchly.adapters.PersonalChannelVideosAdapter
import com.example.watchly.databinding.FragmentVideos2Binding
import com.example.watchly.models.Channel
import com.example.watchly.models.Video
import com.example.watchly.ui.activities.VideoPlayActivity
import com.example.watchly.ui.viewmodels.ChannelViewModel
import com.example.watchly.ui.viewmodels.VideoViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.showSnackBar
import kotlinx.coroutines.flow.collectLatest

class ChannelVideos : Fragment() {

    private lateinit var binding: FragmentVideos2Binding
    private lateinit var videoViewModel: VideoViewModel
    private lateinit var viewAdapter: PersonalChannelVideosAdapter
    private lateinit var channelViewModel: ChannelViewModel
    private var channel: Channel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentVideos2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewAdapter = PersonalChannelVideosAdapter()
        videoViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(VideoViewModel::class.java)
        channelViewModel = ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        ).create(ChannelViewModel::class.java)

        channelViewModel.getCachedChannel().observe(viewLifecycleOwner){channelSaved ->
            if(channelSaved != null){
                fetchVideos(channelSaved.ownedBy.toString())
                channel = channelSaved
            }
        }
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
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }


        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchVideos(channel?.ownedBy)
        }

    }



    private fun fetchVideos(ownedBy: String?) {
        videoViewModel.fetchChannelVideos(ownedBy.toString())
        lifecycleScope.launchWhenStarted {
            videoViewModel.fetchChannelVideosState.collectLatest {
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
                        if(it.data != null && it.data.isNotEmpty()){
                            viewAdapter.differ.submitList(it.data)
                            binding.noVideos.visibility = View.GONE
                            binding.noVideosText.visibility = View.GONE
                            binding.noVideos.pauseAnimation()
                        }else{
                            binding.noVideos.visibility = View.VISIBLE
                            binding.noVideos.playAnimation()
                            binding.noVideosText.visibility = View.VISIBLE
                        }
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


