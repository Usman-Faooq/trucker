package com.buzzware.truckerworld

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.adapters.CommentAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ActivityDetailBinding
import com.buzzware.truckerworld.model.Comments
import com.buzzware.truckerworld.model.Posts
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*


class DetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityDetailBinding
    lateinit var posts : Posts
    var userName = ""
    var userImage = ""

    lateinit var adapter: CommentAdapter
    private var commentsList: ArrayList<Comments?>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        posts = intent.getParcelableExtra("post_data")!!
        userName = intent.getStringExtra("userName")!!
        userImage = intent.getStringExtra("userImage")!!


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
                    return@addSnapshotListener;
                }

                commentsList!!.clear()
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    for (documentSnapshot in queryDocumentSnapshots) {
                        val model = documentSnapshot.toObject(Comments::class.java)
                        commentsList!!.add(model)
                    }
                    setAdapter()
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
        binding.dateTV.text = formatTimestampToCustomFormat(posts.publishDate)

        binding.thumbUpTV.text = (posts.likedBy?.size ?: 0).toString()
        if (posts?.likedBy?.contains(Constants.currentUser.userId) == true) {
            binding.thumbUpIV.setImageResource(R.drawable.thumb_up_filled)
        } else {
            binding.thumbUpIV.setImageResource(R.drawable.thumb_up_unfill)
        }
    }

    private fun setListener() {

        binding.backIV.setOnClickListener {
            finish()
        }

        binding.playIV.setOnClickListener { view ->
            startActivity(Intent(this, VideoViewActivity::class.java).putExtra("URL", posts.videoLink)
            )
        }

        binding.thumbUpIV.setOnClickListener {
            if (posts?.likedBy?.contains(Constants.currentUser.userId) == true) {
                FirebaseFirestore.getInstance().collection("Products")
                    .document(posts.postId).update("likedBy.${Constants.currentUser.userId}", false)
                posts.removeLike(Constants.currentUser.userId)
                binding.thumbUpTV.text = posts.likedBy?.size.toString();
                binding.thumbUpIV.setImageResource(R.drawable.thumb_up_unfill)
            } else {
                FirebaseFirestore.getInstance().collection("Products")
                    .document(posts.postId).update("likedBy.${Constants.currentUser.userId}", true)
                posts.addLike(Constants.currentUser.userId)
                binding.thumbUpTV.text = posts.likedBy?.size.toString();
                binding.thumbUpIV.setImageResource(R.drawable.thumb_up_filled)
            }
        }


        binding.sendIV.setOnClickListener {
            var commentText = binding.commentET.text.toString()
            if (commentText.isEmpty()){
                Toast.makeText(this, "Field Empty", Toast.LENGTH_SHORT).show()
            }else{

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
                            .document(posts.postId).update("commentUsers.${Constants.currentUser.userId}", true);
                        binding.commentET.setText("");

                    }.addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun formatTimestampToCustomFormat(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return dateFormat.format(date)
    }
}