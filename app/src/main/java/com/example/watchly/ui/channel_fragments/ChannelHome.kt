package com.example.watchly.ui.channel_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.databinding.FragmentChannelHomeBinding
import com.example.watchly.models.Channel
import com.example.watchly.models.SubUser
import com.example.watchly.ui.activities.ChannelSettingsActivity
import com.example.watchly.ui.viewmodels.ChannelViewModel

class ChannelHome : Fragment() {

    private lateinit var binding: FragmentChannelHomeBinding
    private lateinit var channelViewModel: ChannelViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChannelHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channelViewModel = ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        ).create(ChannelViewModel::class.java)

        channelViewModel.getCachedChannel().observe(viewLifecycleOwner){channel ->
            if(channel != null){
                displayData(channel)
            }
        }
        binding.manageVideos.setOnClickListener {

        }
        binding.channelSettings.setOnClickListener {
            Intent(requireActivity(), ChannelSettingsActivity::class.java).also {
                startActivity(it)
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
    }


}