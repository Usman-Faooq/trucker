package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.DetailActivity
import com.buzzware.truckerworld.OtherUserProfileActivity
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ItemDesignMainLayoutBinding
import com.buzzware.truckerworld.model.Posts
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import wseemann.media.FFmpegMediaMetadataRetriever
import java.text.SimpleDateFormat
import java.util.*


class PostsAdapter(val context: Context, val list: ArrayList<Posts?>?, var userId : String) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    var fName = ""
    var lName = ""
    var imageUrl = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignMainLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list!!.size //list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var posts = list!!.get(position)

        if (posts!!.userId.equals(userId)){

            holder.binding.profileIV.visibility = View.INVISIBLE
            holder.binding.layoutLL.visibility = View.INVISIBLE

        }else{

            FirebaseFirestore.getInstance().collection("Users")
                .document(posts!!.userId).get()
                .addOnSuccessListener {

                    fName = it.get("firstName").toString()
                    lName = it.get("lastName").toString()
                    imageUrl = it.get("image").toString()
                    holder.binding.userNameTV.text = "$fName $lName"

                    if (imageUrl!= null && imageUrl != ""){
                        Glide.with(context).load(imageUrl)
                            .error(R.drawable.profile_dummy)
                            .placeholder(R.drawable.profile_dummy)
                            .into(holder.binding.profileIV)
                    }

                }.addOnFailureListener {

                }


        }


        //val durationInMillis = getVideoDuration(posts!!.videoUrl)
        //val formattedDuration = formatVideoDuration(durationInMillis)


        Glide.with(context).load(posts!!.thumbnailImage)
            .error(R.drawable.post_place_holder_iv)
            .placeholder(R.drawable.post_place_holder_iv)
            .into(holder.binding.coverIV)
        holder.binding.coverIV.scaleType = ImageView.ScaleType.CENTER_CROP
        holder.binding.titleTV.text = posts.description
        holder.binding.dateTV.text = formatTimestampToCustomFormat(posts.publishDate)
        //holder.binding.clockTV.text = formattedDuration

        holder.binding.profileIV.setOnClickListener {
            val intent = Intent(context, OtherUserProfileActivity::class.java)
            intent.putExtra("userId", posts.userId)

            context.startActivity(intent)
        }

        holder.binding.root.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("post_data", posts)
            intent.putExtra("userName", "$fName $lName")
            intent.putExtra("userImage", imageUrl)

            context.startActivity(intent)
        }

        holder.binding.thumbUpTV.text = (posts.likedBy?.size ?: 0).toString()
        if (posts?.likedBy?.contains(Constants.currentUser.userId) == true) {
            holder.binding.thumbUpIV.setImageResource(R.drawable.thumb_up_filled)
        } else {
            holder.binding.thumbUpIV.setImageResource(R.drawable.thumb_up_unfill)
        }


        // Like Button
        holder.binding.thumbUpIV.setOnClickListener {
            if (posts?.likedBy?.contains(Constants.currentUser.userId) == true) {
                FirebaseFirestore.getInstance().collection("Posts")
                    .document(posts.postId).update("likedBy", FieldValue.arrayRemove(Constants.currentUser.userId))
                posts.removeLike(Constants.currentUser.userId)
            } else {
                FirebaseFirestore.getInstance().collection("Posts")
                    .document(posts.postId).update("likedBy", FieldValue.arrayUnion(Constants.currentUser.userId))
                posts.addLike(Constants.currentUser.userId)
            }
            notifyItemChanged(position)
        }

    }

    inner class ViewHolder(val binding: ItemDesignMainLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    private fun formatTimestampToCustomFormat(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return dateFormat.format(date)
    }


}
