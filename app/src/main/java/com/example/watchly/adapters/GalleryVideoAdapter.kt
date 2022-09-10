package com.example.watchly.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.models.GalleryVideo
import com.example.watchly.models.SubUser

class GalleryVideoAdapter : RecyclerView.Adapter<GalleryVideoAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.vv_video)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.videos_from_gallery,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = differ.currentList[position]
        Glide.with(holder.itemView).load("file://" + video.thumb)
            .skipMemoryCache(false)
            .centerCrop()
            .placeholder(R.color.GrayTextColor)
            .into(holder.imageView)
        holder.itemView.setOnClickListener {
            onClickListener?.invoke(video)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<GalleryVideo>(){
        override fun areItemsTheSame(oldItem: GalleryVideo, newItem: GalleryVideo): Boolean {
            return oldItem.absolutePath == newItem.absolutePath
        }

        override fun areContentsTheSame(oldItem: GalleryVideo, newItem: GalleryVideo): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)


    private var onClickListener: ((GalleryVideo) -> Unit)? = null

    fun setOnClickListener(listener: (GalleryVideo) -> Unit){
        onClickListener = listener
    }

}