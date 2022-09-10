package com.example.watchly.use_cases

import com.example.watchly.uils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class DeleteComment {
    private val firestore = FirebaseFirestore.getInstance()
    fun execute(videoId: String, commentId: String) {
        firestore.collection(Constants.Videos)
            .document(videoId)
            .collection(Constants.Comments)
            .document(commentId).delete()
    }

}