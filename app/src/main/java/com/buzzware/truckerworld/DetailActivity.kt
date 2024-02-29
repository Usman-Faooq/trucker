package com.buzzware.truckerworld

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.adapters.CommentAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ActivityDetailBinding
import com.buzzware.truckerworld.model.Comments
import com.buzzware.truckerworld.model.Products
import com.buzzware.truckerworld.utils.convertSecondsToTimeFormat
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*


class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    lateinit var posts: Products
    private val TAG = "Detail_Activity_TAG"
    var userName = ""
    var userImage = ""
    var postType = ""

    lateinit var adapter: CommentAdapter
    private var commentsList: ArrayList<Comments?>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        posts = intent.getParcelableExtra("post_data")!!
        userName = intent.getStringExtra("userName")!!
        userImage = intent.getStringExtra("userImage")!!
        postType = intent.getStringExtra("postType")!!


        setView()
        setListener()
        getCommentData()
    }


    private fun getCommentData() {
        commentsList!!.clear()
        FirebaseFirestore.getInstance().collection("Products")
            .document(posts.postId).collection("Comments")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { queryDocumentSnapshots, error ->
                if (error != null) {
                    Log.d("LOGGER", "Exception " + error.message);
                    return@addSnapshotListener
                }
                commentsList!!.clear()
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    for (documentSnapshot in queryDocumentSnapshots) {
                        val model = documentSnapshot.toObject(Comments::class.java)
                        commentsList!!.add(model)
                        Log.d("LOGGER", "getCommentData: ${commentsList!!.size}")
                    }
                    setAdapter()
                }

            }

    }

    private fun getLikesCount() {
        FirebaseFirestore.getInstance().collection("Products")
            .document(posts.postId)
            .addSnapshotListener { queryDocumentSnapshots, error ->
                if (error != null) {
                    Log.d("LOGGER", "Exception " + error.message);
                    return@addSnapshotListener;
                }
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.exists()) {
                    val model = queryDocumentSnapshots.toObject(Products::class.java)
                    model?.postId = queryDocumentSnapshots.id
                    binding.apply {
                        if (model != null) {
                            thumbUpTV.text = model.likedBy.filterValues { it }.keys.size.toString()
                            thumbDownTV.text =
                                model.likedBy.filterValues { !it }.keys.size.toString()
                        }
                    }
                }
            }
    }

    private fun setAdapter() {
        //mDialog.dismiss()
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        binding.recyclerView.layoutManager = layoutManager
        adapter = CommentAdapter(this, commentsList, posts.postId)
        binding.recyclerView.adapter = adapter
    }

    private fun setView() {

        Glide.with(this).load(posts!!.thumbnailImage)
            .error(R.drawable.dummy_holder_image)
            .placeholder(R.drawable.dummy_holder_image)
            .into(binding.coverIV)
        binding.coverIV.scaleType = ImageView.ScaleType.CENTER_CROP

        binding.titleTV.text = posts.description
        binding.clockTV.text = convertSecondsToTimeFormat(posts.duration)
        binding.dateTV.text = formatTimestampToCustomFormat(posts.publishDate)
        binding.apply {
            if (postType.equals("image")) {
                clockTV.visibility = View.INVISIBLE
                clockIV.visibility = View.INVISIBLE
                playIV.visibility = View.INVISIBLE
            }
            thumbUpTV.text = posts.likedBy!!.filterValues { it }.keys.size.toString()
            thumbDownTV.text = posts.likedBy!!.filterValues { !it }.keys.size.toString()
        }
    }

    private fun setListener() {

        binding.backIV.setOnClickListener {
            finish()
        }

        binding.coverIV.setOnClickListener { view ->

            if (posts.postType.equals("image")) {
                startActivity(Intent(this, ImagePreviewActivity::class.java).putExtra(
                        "URL",
                        posts.thumbnailImage
                    )
                )
            } else {
                startActivity(
                    Intent(this, VideoViewActivity::class.java).putExtra("URL", posts.videoLink)
                )
            }
        }

        var currentUserLiked = posts?.likedBy?.get(Constants.currentUser.userId)
        Log.d(TAG, "setListener: $currentUserLiked")
        updateLikeCountIcon(currentUserLiked)

        binding.apply {
            val postsRef =
                FirebaseFirestore.getInstance().collection("Products").document(posts.postId)

            thumbUpIV.setOnClickListener {
                if (currentUserLiked != null) {
                    if (currentUserLiked!!) {
                        postsRef.update(
                            "likedBy.${Constants.currentUser.userId}",
                            FieldValue.delete()
                        )
                        thumbUpIV.setImageResource(R.drawable.thumb_up_unfill)
                        thumbDownIV.setImageResource(R.drawable.thumb_down_unfill)
                        getLikesCount()
                        currentUserLiked = false
                    } else {
                        postsRef.update("likedBy.${Constants.currentUser.userId}", true)
                        thumbUpIV.setImageResource(R.drawable.thumb_up_filled)
                        thumbDownIV.setImageResource(R.drawable.thumb_down_unfill)
                        getLikesCount()
                        currentUserLiked = true
                    }
                } else {
                    postsRef.update("likedBy.${Constants.currentUser.userId}", true)
                    thumbUpIV.setImageResource(R.drawable.thumb_up_filled)
                    thumbDownIV.setImageResource(R.drawable.thumb_down_unfill)
                    getLikesCount()
                    currentUserLiked = true
                }
            }

            thumbDownIV.setOnClickListener {
                if (currentUserLiked != null) {
                    if (!currentUserLiked!!) {
                        postsRef.update(
                            "likedBy.${Constants.currentUser.userId}",
                            FieldValue.delete()
                        )
                        thumbUpIV.setImageResource(R.drawable.thumb_up_unfill)
                        thumbDownIV.setImageResource(R.drawable.thumb_down_unfill)
                        getLikesCount()
                        currentUserLiked = true
                    } else {
                        postsRef.update("likedBy.${Constants.currentUser.userId}", false)
                            .addOnSuccessListener {
                                thumbUpIV.setImageResource(R.drawable.thumb_up_unfill)
                                thumbDownIV.setImageResource(R.drawable.thumb_down_filled)
                                getLikesCount()
                                currentUserLiked = false
                            }
                            .addOnFailureListener { e ->
                                Log.e("dislike", "Error adding dislike: ${e.message}", e)
                            }
                    }
                } else {
                    postsRef.update("likedBy.${Constants.currentUser.userId}", false)
                        .addOnSuccessListener {
                            thumbUpIV.setImageResource(R.drawable.thumb_up_unfill)
                            thumbDownIV.setImageResource(R.drawable.thumb_down_filled)
                            getLikesCount()
                            currentUserLiked = false
                        }
                        .addOnFailureListener { e ->
                            Log.e("dislike", "Error adding dislike: ${e.message}", e)
                        }
                }
            }
        }


        binding.sendIV.setOnClickListener {
            var commentText = binding.commentET.text.toString()
            if (commentText.isEmpty()) {
                Toast.makeText(this, "Field Empty", Toast.LENGTH_SHORT).show()
            } else {

                var commentId = UUID.randomUUID().toString()
                val commentMap = hashMapOf(
                    "commentId" to commentId,
                    "content" to commentText,
                    "timeStamp" to System.currentTimeMillis(),
                    "fromId" to Constants.currentUser.userId,
                    "toId" to posts.userId,
                    "type" to "text"
                )

                FirebaseFirestore.getInstance().collection("Products")
                    .document(posts.postId).collection("Comments")
                    .document(commentId).set(commentMap)
                    .addOnSuccessListener {

                        FirebaseFirestore.getInstance().collection("Products")
                            .document(posts.postId)
                            .update("commentUsers.${Constants.currentUser.userId}", true);
                        binding.commentET.setText("");

                    }.addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun updateLikeCountIcon(currentUserLiked: Boolean?) {
        binding.apply {
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
        }
    }

    private fun formatTimestampToCustomFormat(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return dateFormat.format(date)
    }
}