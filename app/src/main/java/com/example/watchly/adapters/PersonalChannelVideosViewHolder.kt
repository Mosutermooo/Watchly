package com.example.watchly.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.databinding.ChannelSectionsBinding
import com.example.watchly.databinding.PersonalChannelVideosViewBinding
import com.example.watchly.databinding.PrivateVideoViewBinding
import com.example.watchly.databinding.VideosHomeDisplayAdapterLayoutBinding
import com.example.watchly.models.Channel
import com.example.watchly.models.HomeRecyclerViewItem
import com.example.watchly.models.PersonalVideosRecyclerViewItem
import com.example.watchly.models.Sections
import com.example.watchly.uils.Constants
import com.google.firebase.firestore.FirebaseFirestore

sealed class PersonalChannelVideosViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)  {


    class VideoViewHolder(
        private val binding: PersonalChannelVideosViewBinding
    ) : PersonalChannelVideosViewHolder(binding = binding){
        fun bind(video: PersonalVideosRecyclerViewItem.Video){
            binding.VideoTitle.text = video.name
            if(video.views != 0){
                binding.videoViews.text = video.views?.toString()
            }else{
                binding.videoViews.text = "0"
            }
            Glide.with(binding.root)
                .load(video.thumbnail)
                .placeholder(R.color.MidGrayColor)
                .centerCrop()
                .into(binding.videoThumbnail)
        }
    }

    class PrivateVideoViewHolder(
        private val binding: PrivateVideoViewBinding
    ) : PersonalChannelVideosViewHolder(binding = binding){
        fun bind(video: PersonalVideosRecyclerViewItem.PrivateVideo){
            binding.VideoTitle.text = video.name
            if(video.views != 0){
                binding.videoViews.text = video.views?.toString()
            }else{
                binding.videoViews.text = "0"
            }
            Glide.with(binding.root)
                .load(video.thumbnail)
                .placeholder(R.color.MidGrayColor)
                .centerCrop()
                .into(binding.videoThumbnail)
        }
    }

    class Sections(
        private val binding: ChannelSectionsBinding
    ) : PersonalChannelVideosViewHolder(binding = binding){
        fun bind(section: PersonalVideosRecyclerViewItem.Sections){
            binding.SectionName.text = section.sectionName
        }
    }




}