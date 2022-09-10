package com.example.watchly.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.watchly.R

class GalleryImageAdapter : RecyclerView.Adapter<GalleryImageAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.image)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.images_from_gallery,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = differ.currentList[position]
        Glide.with(holder.itemView).load("file://" + video)
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

    private val differCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)


    private var onClickListener: ((String) -> Unit)? = null

    fun setOnClickListener(listener: (String) -> Unit){
        onClickListener = listener
    }

}