package com.example.watchly.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.databinding.PersonalChannelVideosViewBinding
import com.example.watchly.models.Video

sealed class NewVideoSuggestionsViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class VideoBind(
        private val binding: PersonalChannelVideosViewBinding
    ): NewVideoSuggestionsViewHolder(binding){
        fun bind(video: Video){
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

}