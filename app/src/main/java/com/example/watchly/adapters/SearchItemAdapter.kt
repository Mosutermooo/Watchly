package com.example.watchly.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.watchly.databinding.*
import com.example.watchly.models.*
import java.lang.IllegalArgumentException

class SearchItemAdapter : RecyclerView.Adapter<SearchItemViewHolder>() {

    companion object{
        const val Section_View = 1
        const val Video_View = 2
        const val Channel_View = 3
        const val User_View = 4
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
       return when(viewType){
           Section_View -> SearchItemViewHolder.Sections(
               ChannelSectionsBinding.inflate(
                   LayoutInflater.from(parent.context),
                   parent,
                   false
               )
           )
           Video_View -> SearchItemViewHolder.VideoViewHolder(
               PersonalChannelVideosViewBinding.inflate(
                   LayoutInflater.from(parent.context),
                   parent,
                   false
               )
           )
           Channel_View -> SearchItemViewHolder.ChannelViewHolder(
               ChannelUserItemViewBinding.inflate(
                   LayoutInflater.from(parent.context),
                   parent,
                   false
               )
           )
           User_View -> SearchItemViewHolder.UserViewHolder(
               UserItemViewBinding.inflate(
                   LayoutInflater.from(parent.context),
                   parent,
                   false
               )
           )
           else -> throw IllegalArgumentException("Invalid View Type Bound")
       }
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
       val currentView = differ.currentList[position]
       when(holder){
           is SearchItemViewHolder.Sections -> {
               holder.bind(currentView as SearchItemView.Sections)
           }
           is SearchItemViewHolder.VideoViewHolder -> {
               holder.bind(currentView as SearchItemView.Video)
               holder.itemView.setOnClickListener {
                   onVideoClickListener?.invoke(currentView as SearchItemView.Video)
               }
           }
           is SearchItemViewHolder.ChannelViewHolder -> {
               holder.bind(currentView as SearchItemView.Channel)
               holder.itemView.setOnClickListener {
                   onChannelClickListener?.invoke(currentView as SearchItemView.Channel)
               }
           }
           is SearchItemViewHolder.UserViewHolder -> {
               holder.bind(currentView as SearchItemView.User)
               holder.itemView.setOnClickListener {
                   onUserClickListener?.invoke(currentView as SearchItemView.User)
               }
           }
       }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return when(differ.currentList[position]){
            is SearchItemView.Sections -> Section_View
            is SearchItemView.Video -> Video_View
            is SearchItemView.Channel -> Channel_View
            is SearchItemView.User -> User_View
        }
    }

    private var onVideoClickListener: ((SearchItemView.Video) -> Unit)? = null

    fun setOnVideoClickListener(listener: (SearchItemView.Video) -> Unit){
        onVideoClickListener = listener
    }

    private var onUserClickListener: ((SearchItemView.User) -> Unit)? = null

    fun setOnUserClickListener(listener: (SearchItemView.User) -> Unit){
        onUserClickListener = listener
    }

    private var onChannelClickListener: ((SearchItemView.Channel) -> Unit)? = null

    fun setOnChannelClickListener(listener: (SearchItemView.Channel) -> Unit){
        onChannelClickListener = listener
    }


    private val differCallback = object : DiffUtil.ItemCallback<SearchItemView>(){
        override fun areItemsTheSame(
            oldItem: SearchItemView,
            newItem: SearchItemView,
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SearchItemView,
            newItem: SearchItemView,
        ): Boolean {
            return oldItem == newItem
        }


    }

    val differ = AsyncListDiffer(this, differCallback)

}