package com.buzzware.truckerworld.classes.repo

import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.model.Products
import com.buzzware.truckerworld.model.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseRepo {
    fun handleLikeDislike(
        post: Products,
        isLike: Boolean,
        onSuccess: () -> Unit,
        onFailure: (error: String?) -> Unit
    ) {
        FirebaseFirestore.getInstance().collection("Products").document(post.postId)
            .update("likedBy.${Constants.currentUser.userId}", isLike)
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e.message)
            }
    }

    fun removeField(post: Products) {
        FirebaseFirestore.getInstance().collection("Products").document(post.postId)
            .update("likedBy.${Constants.currentUser.userId}", FieldValue.delete())
    }

    fun getAllUsers(onSuccess: (user: List<User>) -> Unit, onFailure: (error: String?) -> Unit) {

        FirebaseFirestore.getInstance().collection("Users")
            .get().addOnSuccessListener { query ->
                if (query.isEmpty.not()) {
                    query.forEach {
                        val userData = query.toObjects(User::class.java)
                        onSuccess.invoke(userData)
                    }
                } else {
                    onFailure.invoke("List Not found")
                }
            }.addOnFailureListener { exception ->

                onFailure.invoke(exception.message)
            }
    }
}