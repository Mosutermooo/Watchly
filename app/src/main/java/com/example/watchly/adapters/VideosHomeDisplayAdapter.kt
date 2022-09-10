package com.example.watchly.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.watchly.databinding.VideosHomeDisplayAdapterLayoutBinding
import com.example.watchly.models.HomeRecyclerViewItem
import java.lang.IllegalArgumentException

class VideosHomeDisplayAdapter(private var parentFragmentManager: FragmentManager) : RecyclerView.Adapter<HomeRecyclerViewHolder>() {

    companion object {
        const val VIDEO_VIEW_TYPE: Int = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder {
        return when(viewType){
            VIDEO_VIEW_TYPE -> HomeRecyclerViewHolder.VideoViewHolder(
                VideosHomeDisplayAdapterLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw IllegalArgumentException("Invalid View Type Binded")
        }
    }

    override fun onBindViewHolder(holder: HomeRecyclerViewHolder, position: Int) {
        when(holder){
            is HomeRecyclerViewHolder.VideoViewHolder -> {
                holder.bind(
                    differ.currentList[position] as HomeRecyclerViewItem.Video,
                    parentFragmentManager
                )
                holder.itemView.setOnClickListener {
                    onVideoClickListener?.invoke(differ.currentList[position] as HomeRecyclerViewItem.Video)
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return when(differ.currentList[position]){
            is HomeRecyclerViewItem.Video -> VIDEO_VIEW_TYPE
        }
    }

    private var onVideoClickListener: ((HomeRecyclerViewItem.Video) -> Unit)? = null

    fun setOnVideoClickListener(listener: (HomeRecyclerViewItem.Video) -> Unit){
        onVideoClickListener = listener
    }

    private val differCallback = object : DiffUtil.ItemCallback<HomeRecyclerViewItem>(){
        override fun areItemsTheSame(
            oldItem: HomeRecyclerViewItem,
            newItem: HomeRecyclerViewItem,
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: HomeRecyclerViewItem,
            newItem: HomeRecyclerViewItem,
        ): Boolean {
            return oldItem == newItem
        }


    }

    val differ = AsyncListDiffer(this, differCallback)



}