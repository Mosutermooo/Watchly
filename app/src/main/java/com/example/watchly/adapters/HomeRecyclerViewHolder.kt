package com.example.watchly.adapters

import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.databinding.VideosHomeDisplayAdapterLayoutBinding
import com.example.watchly.models.Channel
import com.example.watchly.models.HomeRecyclerViewItem
import com.example.watchly.ui.fragments.user_perspective_chanel.ChannelBottomSheet
import com.example.watchly.uils.Constants
import com.example.watchly.uils.ReusableResource
import com.google.firebase.firestore.FirebaseFirestore

sealed class HomeRecyclerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class VideoViewHolder(
        private val binding: VideosHomeDisplayAdapterLayoutBinding
        ) : HomeRecyclerViewHolder(binding = binding){
            fun bind(
                video: HomeRecyclerViewItem.Video,
                fragmentManager: FragmentManager
            ){
                binding.VideoTitle.text = video.name
                Glide.with(binding.root)
                    .load(video.thumbnail)
                    .placeholder(R.color.MidGrayColor)
                    .centerCrop()
                    .into(binding.Thumbnail)

                FirebaseFirestore.getInstance().collection(Constants.Channels)
                    .document(video.channel.toString())
                    .addSnapshotListener { value, error ->
                        value?.let {
                            val channel = it.toObject(Channel::class.java)
                            if(channel != null){
                                Glide.with(binding.root)
                                    .load(channel?.image)
                                    .placeholder(R.color.MidGrayColor)
                                    .centerCrop()
                                    .into(binding.ChannelImage)
                                binding.ChannelImage.setOnClickListener {
                                    if(channel.ownedBy != ReusableResource.uid()){
                                        ChannelBottomSheet(channel).show(fragmentManager, "channel")
                                    }
                                }
                            }
                        }
                    }
            }
        }

}