package com.example.watchly.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchly.R
import com.example.watchly.adapters.SearchItemAdapter
import com.example.watchly.databinding.FragmentSearchBinding
import com.example.watchly.models.Channel
import com.example.watchly.models.User
import com.example.watchly.models.Video
import com.example.watchly.ui.activities.VideoPlayActivity
import com.example.watchly.ui.dialogs.UserPreview
import com.example.watchly.ui.fragments.user_perspective_chanel.ChannelBottomSheet
import com.example.watchly.ui.viewmodels.SearchViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.uid
import kotlinx.coroutines.flow.collect


class SearchFragment : Fragment() {

    private lateinit var binding : FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var viewAdapter: SearchItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(SearchViewModel::class.java)

        setupRecyclerView()
        getFeedData()

        binding.swipeRefreshLayout2.setOnRefreshListener {
            getFeedData()
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

        viewAdapter.setOnChannelClickListener {
            if(it.ownedBy != uid()){
                val channel = Channel(
                    it.id,
                    it.name,
                    it.description,
                    it.image,
                    it.banner,
                    it.channelId,
                    it.ownedBy,
                    it.createAt
                )
                ChannelBottomSheet(channel).show(parentFragmentManager, "channel")
            }
        }

        viewAdapter.setOnUserClickListener {
            val user = User(
                it.name,
                it.lastname,
                it.username,
                it.uid,
                it.email,
                it.image,
                it.channel,
                it.registrationState,
                it.fcmToken
            )
            UserPreview(user).show(parentFragmentManager, "userPreview")
        }

    }

    private fun getFeedData() {
        searchViewModel.getFeedData()
        lifecycleScope.launchWhenStarted {
            searchViewModel.feedDataState.collect{
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading -> {
                        binding.swipeRefreshLayout2.isRefreshing = true
                        binding.pg.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.swipeRefreshLayout2.isRefreshing = false
                        binding.pg.visibility = View.GONE
                        if(it.data != null){
                            viewAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }

    }

    private fun setupRecyclerView() {
        viewAdapter = SearchItemAdapter()
        binding.feedSearchRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = viewAdapter
        }
    }


}