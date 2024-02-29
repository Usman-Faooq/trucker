package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.DetailActivity
import com.buzzware.truckerworld.OtherUserProfileActivity
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.VideoViewActivity
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.classes.repo.FirebaseRepo
import com.buzzware.truckerworld.databinding.ItemDesignMainLayoutBinding
import com.buzzware.truckerworld.model.Products
import com.buzzware.truckerworld.model.User
import com.buzzware.truckerworld.utils.convertSecondsToTimeFormat
import com.buzzware.truckerworld.utils.formatTimestampToCustomFormat
import com.buzzware.truckerworld.utils.goneViews
import com.buzzware.truckerworld.utils.inVisible
import com.buzzware.truckerworld.utils.invisibleViews
import com.buzzware.truckerworld.utils.visible
import com.google.firebase.firestore.FirebaseFirestore

class ProfileProductAdapter(
    val context: Context,
    val productsList: ArrayList<Products?>?,
    val userList: ArrayList<User?>?,
    var userId: String
) : RecyclerView.Adapter<ProfileProductAdapter.ViewHolder>(){

    var fName = ""
    var lName = ""
    var imageUrl = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDesignMainLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return productsList!!.size
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = productsList!!.get(position)
        holder.bind(productsList.get(position)!!)

        userList!!.forEach { userItem ->
            if (userItem!!.userId == item!!.userId) {
                fName = userItem.firstName
                lName = userItem.lastName
                imageUrl = userItem.image
                holder.binding.userNameTV.text = "$fName $lName"
                if (imageUrl != null && imageUrl != "") {
                    Glide.with(context).load(imageUrl).into(holder.binding.profileIV)
                }
            }
        }

        holder.apply {
            binding.apply {
                //setLikeCount(item,binding)
                thumbUpTV.text = item!!.likedBy!!.filterValues { it }.keys.size.toString()
                thumbDownTV.text = item.likedBy!!.filterValues { !it }.keys.size.toString()
            }
        }
    }

    private fun setLikeCount(list: Products, binding: ItemDesignMainLayoutBinding) {
        var trueCount = 0
        var falseCount = 0

        list.likedBy?.entries?.forEach { entry ->
            if (entry.value) {
                trueCount++
            } else {
                falseCount++
            }
        }

        binding.thumbUpTV.text = trueCount.toString()
        binding.thumbDownTV.text = falseCount.toString()
    }

    inner class ViewHolder(val binding: ItemDesignMainLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Products) {

            binding.apply {

                if (item.addedby.equals("admin")) {
                    goneViews(
                        thumbDownTV,
                        thumbDownIV,
                        thumbUpTV,
                        thumbUpIV,
                        addFriendTv,
                        userNameTV,
                        profileIV
                    )
                }

                if (item.addedby.equals("admin")) {
                    root.setOnClickListener {
                        val intent = Intent(context, VideoViewActivity::class.java)
                        intent.putExtra("URL", item.videoLink)
                        context.startActivity(intent)
                    }
                } else {
                    root.setOnClickListener {
                        val intent = Intent(context, DetailActivity::class.java)
                        intent.apply {
                            putExtra("post_data", item)
                            putExtra("userName", "$fName $lName")
                            putExtra("postType", item.postType)
                            putExtra("userImage", imageUrl)
                        }
                        context.startActivity(intent)
                    }
                }
                if (item.userId.equals(userId)) {
                    addFriendTv.inVisible()
                    deleteIV.visible()
                }

                deleteIV.setOnClickListener {
                    FirebaseFirestore.getInstance().collection("Products").document(item.postId).delete().addOnSuccessListener {
                        Toast.makeText(context, "${item.description} Deleted!!", Toast.LENGTH_SHORT).show()
                    }
                }
                Glide.with(context).load(item.thumbnailImage)
                    .error(R.drawable.post_place_holder_iv)
                    .placeholder(R.drawable.post_place_holder_iv)
                    .into(coverIV)


                coverIV.scaleType = ImageView.ScaleType.CENTER_CROP
                titleTV.text = item.description
                dateTV.text = formatTimestampToCustomFormat(item.publishDate)
                clockTV.text = convertSecondsToTimeFormat(item.duration)

                profileIV.setOnClickListener {
                    val intent = Intent(context, OtherUserProfileActivity::class.java)
                    intent.putExtra("userId", item.userId)
                    context.startActivity(intent)

                }

                if (item.postType.equals("image")) {
                    invisibleViews(clockTV, clockIV, playIcon)
                }

                val currentUserLiked = item.likedBy[Constants.currentUser.userId]

                if (currentUserLiked != null) {
                    if (currentUserLiked) {
                        // Current user has liked the post
                        thumbUpIV.setImageResource(R.drawable.thumb_up_filled)
                        thumbDownIV.setImageResource(R.drawable.thumb_down_unfill)
                    } else {
                        // Current user has not liked the post
                        thumbUpIV.setImageResource(R.drawable.thumb_up_unfill)
                        thumbDownIV.setImageResource(R.drawable.thumb_down_filled)
                    }
                } else {
                    thumbUpIV.setImageResource(R.drawable.thumb_up_unfill)
                    thumbDownIV.setImageResource(R.drawable.thumb_down_unfill)
                }


                thumbUpIV.setOnClickListener {
                    if (currentUserLiked != null) {
                        if (currentUserLiked) {
                            FirebaseRepo.removeField(post = item)
                            Products().removeLike(Constants.currentUser.userId)
                        } else {
                            FirebaseRepo.handleLikeDislike(post = item, isLike = true, onSuccess = {
                                notifyItemChanged(absoluteAdapterPosition)
                                Products().setUserLike(Constants.currentUser.userId, true)
                            }, onFailure = {
                                Log.e("dislike", "Error adding dislike: $it")
                            })
                        }
                    } else {
                        FirebaseRepo.handleLikeDislike(post = item, isLike = true, onSuccess = {
                            notifyItemChanged(absoluteAdapterPosition)
                            Products().setUserLike(Constants.currentUser.userId, true)
                        }, onFailure = {
                            Log.e("dislike", "Error adding dislike: $it")
                        })
                    }
                    notifyItemChanged(position)
                }

                thumbDownIV.setOnClickListener {
                    if (currentUserLiked != null) {
                        if (!currentUserLiked) {
                            FirebaseRepo.removeField(post = item)
                            Products().removeLike(Constants.currentUser.userId)
                        } else {
                            FirebaseRepo.handleLikeDislike(
                                post = item,
                                isLike = false,
                                onSuccess = {
                                    notifyItemChanged(absoluteAdapterPosition)
                                    Products().setUserLike(Constants.currentUser.userId, false)
                                },
                                onFailure = {
                                    Log.e("dislike", "Error adding dislike: $it")
                                })
                        }
                    } else {
                        FirebaseRepo.handleLikeDislike(post = item, isLike = false, onSuccess = {
                            notifyItemChanged(absoluteAdapterPosition)
                            Products().setUserLike(Constants.currentUser.userId, false)
                        }, onFailure = {
                            Log.e("dislike", "Error adding dislike: $it")
                        })
                    }
                }
            }
        }
    }
}