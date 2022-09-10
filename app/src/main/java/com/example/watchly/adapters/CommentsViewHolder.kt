package com.example.watchly.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.databinding.CommentsItemViewBinding
import com.example.watchly.models.Comment
import com.example.watchly.models.User
import com.example.watchly.uils.Constants
import com.example.watchly.uils.ReusableResource.uid
import com.example.watchly.use_cases.DeleteComment
import com.google.firebase.firestore.FirebaseFirestore

sealed class CommentsViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    class CommentsViewHolder(
        private var binding: CommentsItemViewBinding
    ) : com.example.watchly.adapters.CommentsViewHolder(binding) {
        fun bind(comment: Comment, videoId: String){
            FirebaseFirestore.getInstance().collection(Constants.USERS)
                .document(comment.commentedBy.toString())
                .get().addOnSuccessListener {
                    val user = it.toObject(User::class.java)
                    Glide.with(binding.root)
                        .load(user?.image)
                        .placeholder(R.color.GrayTextColor)
                        .centerCrop()
                        .into(binding.UserProfileImage)
                }
            binding.comment.text = comment.comment
            binding.time.text = comment.time
            if(comment.commentedBy != uid()){
                binding.deleteComment.visibility = View.GONE
            }
            binding.deleteComment.setOnClickListener {
                DeleteComment().execute(
                    videoId,
                    comment.commentId.toString()
                )
            }

        }
    }

}