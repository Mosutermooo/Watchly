package com.example.watchly.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.databinding.UserPreviewLayoutBinding
import com.example.watchly.models.Channel
import com.example.watchly.models.User
import com.example.watchly.ui.fragments.user_perspective_chanel.ChannelBottomSheet
import com.example.watchly.ui.viewmodels.ChannelViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showSnackBar
import com.example.watchly.uils.ReusableResource.uid
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UserPreview(private var user: User) : BottomSheetDialogFragment() {

    private lateinit var binding: UserPreviewLayoutBinding
    private lateinit var channelViewModel: ChannelViewModel
    private lateinit var channel: Channel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserPreviewLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        channelViewModel = ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        ).create(ChannelViewModel::class.java)

        if(user.channel != null){
            getChannelData()
        }else{
            binding.channelLayout.visibility = View.GONE
        }
        if(user.uid == uid()){
            binding.usernameNameLastname.text = "${user.name} ${user.lastname} - ${user.username} - You"
            Glide.with(this).load(user.image).centerCrop().placeholder(R.color.GrayTextColor).into(binding.UserImage)
        }else{
            binding.usernameNameLastname.text = "${user.name} ${user.lastname} - ${user.username}"
            Glide.with(this).load(user.image).centerCrop().placeholder(R.color.GrayTextColor).into(binding.UserImage)
        }

        binding.layoutChannel.setOnClickListener {
            ChannelBottomSheet(channel).show(parentFragmentManager, "channel")
        }

        binding.close.setOnClickListener {
            dismiss()
        }



    }

    private fun getChannelData() {
        channelViewModel.getChannelData(user.uid)
        channelViewModel.channelState.observe(viewLifecycleOwner){
            when(it){
                is Resource.Error -> {
                    showSnackBar(it.message.toString())
                }
                is Resource.Success -> {
                    if(it.data != null){
                        binding.ChannelName.text = it.data.name
                        Glide.with(this).load(it.data.image).centerCrop().placeholder(R.color.GrayTextColor).into(binding.channelImage)
                        binding.channelLayout.visibility = View.VISIBLE
                        channel = it.data
                    }
                }
            }
        }
    }

}