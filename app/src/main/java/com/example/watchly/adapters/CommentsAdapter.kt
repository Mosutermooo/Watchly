package com.example.watchly.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.watchly.R
import com.example.watchly.databinding.CommentsItemViewBinding
import com.example.watchly.models.Comment
import com.example.watchly.models.HomeRecyclerViewItem

class CommentsAdapter(private var videoId: String): RecyclerView.Adapter<CommentsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder.CommentsViewHolder(
            CommentsItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        if(holder is CommentsViewHolder.CommentsViewHolder){
            holder.bind(differ.currentList[position], videoId)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<Comment>(){
        override fun areItemsTheSame(
            oldItem: Comment,
            newItem: Comment,
        ): Boolean {
            return oldItem.commentId == newItem.commentId
        }

        override fun areContentsTheSame(
            oldItem: Comment,
            newItem: Comment,
        ): Boolean {
            return oldItem == newItem
        }


    }

    val differ = AsyncListDiffer(this, differCallback)

}