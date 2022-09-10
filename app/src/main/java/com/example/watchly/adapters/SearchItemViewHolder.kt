package com.example.watchly.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.databinding.*
import com.example.watchly.models.Channel
import com.example.watchly.models.HomeRecyclerViewItem
import com.example.watchly.models.PersonalVideosRecyclerViewItem
import com.example.watchly.models.SearchItemView
import com.example.watchly.uils.Constants
import com.google.firebase.firestore.FirebaseFirestore

sealed class SearchItemViewHolder (binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class Sections(
        private val binding: ChannelSectionsBinding
    ) : SearchItemViewHolder(binding = binding){
        fun bind(section: SearchItemView.Sections){
            binding.SectionName.text = section.sectionName
        }
    }


    class VideoViewHolder(
        private val binding: PersonalChannelVideosViewBinding
    ) : SearchItemViewHolder(binding = binding){
        fun bind(video: SearchItemView.Video){
            binding.VideoTitle.text = video.name
            Glide.with(binding.root)
                .load(video.thumbnail)
                .placeholder(R.color.MidGrayColor)
                .centerCrop()
                .into(binding.videoThumbnail)
            binding.videoViews.text = video.views.toString()
        }
    }

    class UserViewHolder(
        private val binding: UserItemViewBinding
    ): SearchItemViewHolder(binding) {
        fun bind(user: SearchItemView.User){
            Glide.with(binding.root)
                .load(user.image)
                .centerCrop()
                .placeholder(R.color.GrayTextColor)
                .into(binding.userChannelImage)
            binding.username.text = user.username
        }
    }

    class ChannelViewHolder(
        private val binding: ChannelUserItemViewBinding
    ): SearchItemViewHolder(binding) {
        fun bind(channel: SearchItemView.Channel){
            Glide.with(binding.root)
                .load(channel.image)
                .centerCrop()
                .placeholder(R.color.GrayTextColor)
                .into(binding.userChannelImage)
            binding.userChannelName.text = channel.name
        }
    }

}