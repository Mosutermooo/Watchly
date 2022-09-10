package com.example.watchly.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.watchly.databinding.PersonalChannelVideosViewBinding
import com.example.watchly.models.HomeRecyclerViewItem
import com.example.watchly.models.PersonalVideosRecyclerViewItem
import com.example.watchly.models.Video

class NewVideoSuggestionsAdapter : RecyclerView.Adapter<NewVideoSuggestionsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NewVideoSuggestionsViewHolder {
        return NewVideoSuggestionsViewHolder.VideoBind(
            PersonalChannelVideosViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewVideoSuggestionsViewHolder, position: Int) {
        if(holder is NewVideoSuggestionsViewHolder.VideoBind){
            holder.bind(differ.currentList[position])
            holder.itemView.setOnClickListener {
                onVideoClickListener?.invoke(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size


    private val differCallback = object : DiffUtil.ItemCallback<Video>(){
        override fun areItemsTheSame(
            oldItem: Video,
            newItem: Video,
        ): Boolean {
            return oldItem.videoId == newItem.videoId
        }

        override fun areContentsTheSame(
            oldItem: Video,
            newItem: Video,
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    private var onVideoClickListener: ((Video) -> Unit)? = null

    fun setOnVideoClickListener(listener: (Video) -> Unit){
        onVideoClickListener = listener
    }



}