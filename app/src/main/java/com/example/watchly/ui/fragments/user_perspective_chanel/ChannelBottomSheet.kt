package com.example.watchly.ui.fragments.user_perspective_chanel

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.watchly.R
import com.example.watchly.adapters.PagerAdapter
import com.example.watchly.adapters.PagerAdapter2
import com.example.watchly.databinding.ChannelBottomSheetBinding
import com.example.watchly.models.Channel
import com.example.watchly.ui.viewmodels.ChannelViewModel
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.initLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showSnackBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator

class ChannelBottomSheet(private var channel: Channel) : DialogFragment() {

    private lateinit var binding: ChannelBottomSheetBinding
    private lateinit var channelViewModel: ChannelViewModel
    private lateinit var userViewModel: UserViewModel



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChannelBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLoadingDialog()
        channelViewModel = ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        ).create(ChannelViewModel::class.java)
        getChannelData()
        binding.channelViewPager.adapter = PagerAdapter2(requireActivity())
        TabLayoutMediator(binding.tabLayout, binding.channelViewPager){tab, index ->
            tab.text = when(index){
                0 -> getString(R.string.home)
                1 -> getString(R.string.video)
                2 -> getString(R.string.about)
                else -> {throw Resources.NotFoundException()}
            }
        }.attach()

        binding.Close.setOnClickListener {
            dismiss()
        }
        channelViewModel.getCachedChannel().observe(viewLifecycleOwner){
            if(it != null){
                binding.ChannelName.text = it.name
            }
        }


    }


    private fun getChannelData() {
        channelViewModel.getChannelData(channel.ownedBy.toString())
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

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
    }


}