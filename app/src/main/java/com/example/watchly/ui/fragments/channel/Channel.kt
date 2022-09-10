package com.example.watchly.ui.fragments.channel

import android.content.res.Resources
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.watchly.R
import com.example.watchly.adapters.PagerAdapter
import com.example.watchly.databinding.FragmentChannelBinding
import com.example.watchly.models.Channel
import com.example.watchly.ui.activities.ChannelActivity
import com.example.watchly.ui.viewmodels.ChannelViewModel
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.initLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showSnackBar
import com.example.watchly.uils.ReusableResource.uid
import com.google.android.material.tabs.TabLayoutMediator

class Channel : Fragment() {

    private lateinit var binding: FragmentChannelBinding
    private lateinit var channelViewModel: ChannelViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLoadingDialog()
        channelViewModel = ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        ).create(ChannelViewModel::class.java)
        userViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(UserViewModel::class.java)
        getChannelData()


        channelViewModel.getCachedChannel().observe(viewLifecycleOwner){
            if(it != null){
                displayData(it)
            }
        }

        val channelActivity = requireActivity() as ChannelActivity
        channelActivity.setSupportActionBar(binding.toolbar)
        channelActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_white)
        }
        setHasOptionsMenu(true)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }







        binding.channelViewPager.adapter = PagerAdapter(requireActivity())
        TabLayoutMediator(binding.tabLayout, binding.channelViewPager){tab, index ->
            tab.text = when(index){
                0 -> getString(R.string.home)
                1 -> getString(R.string.videos)
                2 -> getString(R.string.about)
                else -> {throw Resources.NotFoundException()}
            }
        }.attach()



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.channel_menu, menu)
    }




    private fun getChannelData() {
        channelViewModel.getChannelData(uid())
        channelViewModel.channelState.observe(viewLifecycleOwner){
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
                    if(it.data != null){
                        channelViewModel.deleteCachedChannel()
                        channelViewModel.cacheChannel(it.data)

                    }
                }
            }
        }
    }

    private fun displayData(channel: Channel) {
        binding.toolbar.title = channel.name
    }

}