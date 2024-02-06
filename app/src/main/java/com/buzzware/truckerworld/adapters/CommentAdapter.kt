package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
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
        return ViewHolder(ItemDesignCommentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list!!.size//list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model = list!![position]

        FirebaseFirestore.getInstance().collection("Users")
            .document(model!!.fromId).get()
            .addOnSuccessListener {
                var fName = it.getString("firstName")
                var lName = it.getString("lastName")
                var imageUrl = it.getString("imageUrl")

                holder.binding.userNameTV.text = "$fName $lName"
                if (imageUrl!= null && imageUrl != ""){
                    Glide.with(context).load(imageUrl)
                        .error(R.drawable.profile_dummy)
                        .placeholder(R.drawable.profile_dummy)
                        .into(holder.binding.profileIV)
                }
            }

        holder.binding.contentTV.text = model.content

        holder.binding.thumbUpTV.text = (model.likedBy?.size ?: 0).toString()
        holder.binding.thumbDownTV.text = (model.dislikedBy?.size ?: 0).toString()
        if (model?.likedBy?.contains(Constants.currentUser.userId) == true) {
            holder.binding.thumbUpIV.setImageResource(R.drawable.thumb_up_filled)
        } else {
            holder.binding.thumbUpIV.setImageResource(R.drawable.thumb_up_unfill)
        }

        if (model?.dislikedBy?.contains(Constants.currentUser.userId) == true) {
            holder.binding.thumbDownIV.setImageResource(R.drawable.thumb_down_filled)
        } else {
            holder.binding.thumbDownIV.setImageResource(R.drawable.thumb_down_unfill)
        }

        // Like Button
        holder.binding.thumbUpIV.setOnClickListener {
            if (model?.likedBy?.contains(Constants.currentUser.userId) == true) {
                FirebaseFirestore.getInstance().collection("Posts")
                    .document(postId).collection("Comments")
                    .document(model.commentId).update("likedBy", FieldValue.arrayRemove(Constants.currentUser.userId))
                model.removeLike(Constants.currentUser.userId)
            } else {
                FirebaseFirestore.getInstance().collection("Posts")
                    .document(postId).collection("Comments")
                    .document(model.commentId).update("likedBy", FieldValue.arrayUnion(Constants.currentUser.userId))
                model.addLike(Constants.currentUser.userId)
            }
            notifyItemChanged(position)
        }

        // Dislike Button
        holder.binding.thumbDownIV.setOnClickListener {
            if (model?.dislikedBy?.contains(Constants.currentUser.userId) == true) {
                FirebaseFirestore.getInstance().collection("Posts")
                    .document(postId).collection("Comments")
                    .document(model.commentId).update("dislikedBy", FieldValue.arrayRemove(Constants.currentUser.userId))
                model.removeDislike(Constants.currentUser.userId)
            } else {
                FirebaseFirestore.getInstance().collection("Posts")
                    .document(postId).collection("Comments")
                    .document(model.commentId).update("dislikedBy", FieldValue.arrayUnion(Constants.currentUser.userId))
                model.addDislike(Constants.currentUser.userId)
            }
            notifyItemChanged(position)
        }

    }

    inner class ViewHolder(val binding: ItemDesignCommentLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
