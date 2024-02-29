package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.ChatActivity
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ItemChatLayoutBinding
import com.buzzware.truckerworld.databinding.ItemDesignCommentLayoutBinding
import com.buzzware.truckerworld.model.Comments
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CommentAdapter(val context: Context, val list: ArrayList<Comments?>?, val postId: String) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDesignCommentLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model = list!![position]

        FirebaseFirestore.getInstance().collection("Users")
            .document(model!!.fromId).get()
            .addOnSuccessListener {
                var fName = it.getString("firstName")
                var lName = it.getString("lastName")
                var imageUrl = it.getString("image")

                holder.binding.userNameTV.text = "$fName $lName"
                if (imageUrl != null && imageUrl != "") {
                    Glide.with(context).load(imageUrl)
                        .error(R.drawable.post_place_holder_iv)
                        .placeholder(R.drawable.post_place_holder_iv)
                        .into(holder.binding.profileIV)
                }
            }

        holder.apply {
            binding.apply {
                thumbUpTV.text = model.likedBy.filterValues { it }.keys.size.toString()
                thumbDownTV.text = model.likedBy.filterValues { !it }.keys.size.toString()
                contentTV.text = model.content

                var currentUserLiked = model?.likedBy?.get(Constants.currentUser.userId)


                if (currentUserLiked != null) {
                    if (currentUserLiked) {
                        // Current user has liked the post
                        thumbUpIV.setImageResource(R.drawable.ic_like_black)
                        thumbDownIV.setImageResource(R.drawable.thumb_down_unfill)
                    } else {
                        // Current user has not liked the post
                        thumbUpIV.setImageResource(R.drawable.thumb_up_unfill)
                        thumbDownIV.setImageResource(R.drawable.ic_dislike_black)
                    }
                } else {
                    thumbUpIV.setImageResource(R.drawable.thumb_up_unfill)
                    thumbDownIV.setImageResource(R.drawable.thumb_down_unfill)
                }

                val commentRef =
                    FirebaseFirestore.getInstance().collection("Products").document(postId)
                        .collection("Comments").document(model.commentId)

                if(model.fromId == Constants.currentUser.userId){
                    holder.binding.apply {
                        deleteIV.visibility = View.VISIBLE
                        deleteIV.setOnClickListener {
                            commentRef.delete()
                            notifyDataSetChanged()
                        }
                    }
                }

                thumbUpIV.setOnClickListener {
                    if (currentUserLiked != null) {
                        if (currentUserLiked!!) {
                            commentRef.update(
                                "likedBy.${Constants.currentUser.userId}",
                                FieldValue.delete()
                            )
                            currentUserLiked = false
                        } else {
                            commentRef.update("likedBy.${Constants.currentUser.userId}", true)
                            currentUserLiked =true
                        }
                    } else {
                        commentRef.update("likedBy.${Constants.currentUser.userId}", true)
                        currentUserLiked =true
                    }
                    notifyItemChanged(position)
                }
                thumbDownIV.setOnClickListener {
                    if (currentUserLiked != null) {
                        if (!currentUserLiked!!) {
                            commentRef.update("likedBy.${Constants.currentUser.userId}", FieldValue.delete())
                            currentUserLiked = true
                        } else {
                            commentRef.update("likedBy.${Constants.currentUser.userId}", false)
                                .addOnSuccessListener {
                                    notifyItemChanged(position)
                                    currentUserLiked =false
                                }
                                .addOnFailureListener { e ->
                                    Log.e("dislike", "Error adding dislike: ${e.message}", e)
                                }
                        }
                    }else{
                        commentRef.update("likedBy.${Constants.currentUser.userId}", false)
                            .addOnSuccessListener {
                                notifyItemChanged(position)
                                currentUserLiked =false
                            }
                            .addOnFailureListener { e ->
                                Log.e("dislike", "Error adding dislike: ${e.message}", e)
                            }
                    }
                }
            }
        }
    }

    inner class ViewHolder(val binding: ItemDesignCommentLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}
