package com.example.watchly.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.watchly.databinding.ChannelSectionsBinding
import com.example.watchly.databinding.PersonalChannelVideosViewBinding
import com.example.watchly.databinding.PrivateVideoViewBinding
import com.example.watchly.databinding.VideosHomeDisplayAdapterLayoutBinding
import com.example.watchly.models.HomeRecyclerViewItem
import com.example.watchly.models.PersonalVideosRecyclerViewItem
import java.lang.IllegalArgumentException

class PersonalChannelVideosAdapter : RecyclerView.Adapter<PersonalChannelVideosViewHolder>() {

    companion object {
        const val VIDEO_VIEW_TYPE_CHANNEL: Int = 1
        const val PRIVATE_VIDEO_VIEW_TYPE: Int = 2
        const val SECTION_VIEW_TYPE: Int = 3
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PersonalChannelVideosViewHolder {
        return when(viewType){
            VIDEO_VIEW_TYPE_CHANNEL -> PersonalChannelVideosViewHolder.VideoViewHolder(
                PersonalChannelVideosViewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            PRIVATE_VIDEO_VIEW_TYPE -> PersonalChannelVideosViewHolder.PrivateVideoViewHolder(
                PrivateVideoViewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            SECTION_VIEW_TYPE -> PersonalChannelVideosViewHolder.Sections(
                ChannelSectionsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw IllegalArgumentException("Invalid View Type Binded")
        }
    }

    override fun onBindViewHolder(holder: PersonalChannelVideosViewHolder, position: Int) {
        when(holder){
            is PersonalChannelVideosViewHolder.VideoViewHolder -> {
                holder.bind(differ.currentList[position] as PersonalVideosRecyclerViewItem.Video)
                holder.itemView.setOnClickListener {
                    onVideoClickListener?.invoke(differ.currentList[position] as PersonalVideosRecyclerViewItem.Video)
                }
                holder.itemView.setOnLongClickListener {
                    onVideoClickListener?.invoke(differ.currentList[position] as PersonalVideosRecyclerViewItem.Video)
                    true
                }
            }
            is PersonalChannelVideosViewHolder.PrivateVideoViewHolder -> {
                holder.bind(differ.currentList[position] as PersonalVideosRecyclerViewItem.PrivateVideo)
                holder.itemView.setOnClickListener {
                    onPrivateVideoClickListener?.invoke(differ.currentList[position] as PersonalVideosRecyclerViewItem.PrivateVideo)
                }
                holder.itemView.setOnLongClickListener {
                    onPrivateVideoClickListener?.invoke(differ.currentList[position] as PersonalVideosRecyclerViewItem.PrivateVideo)
                    true
                }
            }
            is PersonalChannelVideosViewHolder.Sections -> {
                holder.bind(differ.currentList[position] as PersonalVideosRecyclerViewItem.Sections)
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        val item = differ.currentList[position]
        return when(item){
            is PersonalVideosRecyclerViewItem.Video -> VIDEO_VIEW_TYPE_CHANNEL
            is PersonalVideosRecyclerViewItem.PrivateVideo -> PRIVATE_VIDEO_VIEW_TYPE
            is PersonalVideosRecyclerViewItem.Sections -> SECTION_VIEW_TYPE
        }
    }


    private val differCallback = object : DiffUtil.ItemCallback<PersonalVideosRecyclerViewItem>(){
        override fun areItemsTheSame(
            oldItem: PersonalVideosRecyclerViewItem,
            newItem: PersonalVideosRecyclerViewItem,
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PersonalVideosRecyclerViewItem,
            newItem: PersonalVideosRecyclerViewItem,
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    private var onVideoClickListener: ((PersonalVideosRecyclerViewItem.Video) -> Unit)? = null

    fun setOnVideoClickListener(listener: (PersonalVideosRecyclerViewItem.Video) -> Unit){
        onVideoClickListener = listener
    }

    private var onPrivateVideoClickListener: ((PersonalVideosRecyclerViewItem.PrivateVideo) -> Unit)? = null

    fun setOnPrivateVideoClickListener(listener: (PersonalVideosRecyclerViewItem.PrivateVideo) -> Unit){
        onPrivateVideoClickListener = listener
    }

}