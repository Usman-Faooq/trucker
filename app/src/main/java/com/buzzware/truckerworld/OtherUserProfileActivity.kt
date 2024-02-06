package com.buzzware.truckerworld

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.adapters.PostsAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ActivityOtherUserProfileBinding
import com.buzzware.truckerworld.model.Posts
import com.buzzware.truckerworld.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.ArrayList
import java.util.Objects

class OtherUserProfileActivity : AppCompatActivity() {

    lateinit var binding : ActivityOtherUserProfileBinding
    private var postList: ArrayList<Posts?>? = ArrayList()
    lateinit var adapter: PostsAdapter

    var userId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId")!!

        getUserData()
        getData()
        setView()
        setListener()

    }

    private fun getUserData() {

        FirebaseFirestore.getInstance().collection("Users")
            .document(userId).addSnapshotListener { value, error ->
                if (error != null) {
                    // Handle the exception
                    Log.e("Firestore Listener", "Error listening to user document: $error")
                    return@addSnapshotListener
                }

                if (value != null && value.exists()) {
                    val user = value.toObject(User::class.java)

                    Constants.otherName = "${user!!.firstName} ${user.lastName}"
                    Constants.otherImage = user.image

                    binding.userNameTV.text = "${user.firstName} ${user.lastName}"
                    Glide.with(this).load(user.image)
                        .error(R.drawable.profile_dummy)
                        .placeholder(R.drawable.profile_dummy)
                        .into(binding.profileIV)

                    if (user.friends?.get(Constants.currentUser.userId).equals("request", ignoreCase = true) ||
                        user.friends?.get(Constants.currentUser.userId).equals("requested", ignoreCase = true)){
                        binding.addFriendTV.text = "Requested"
                        binding.chatIV.visibility = View.INVISIBLE
                    }else if (user.friends?.get(Constants.currentUser.userId).equals("accept", ignoreCase = true) ||
                        user.friends?.get(Constants.currentUser.userId).equals("accepted", ignoreCase = true)){
                        binding.addFriendTV.text = "Accepted"
                        binding.chatIV.visibility = View.VISIBLE
                    }else{
                        binding.addFriendTV.text = "Add Friend"
                        binding.chatIV.visibility = View.INVISIBLE
                    }

                }

            }

    }


    private fun setView() {

        binding.menuIV.setImageResource(R.drawable.back_icon)
        binding.titleTV.setText("Profile")




    }

    private fun setListener() {

        binding.menuIV.setOnClickListener {
            finish()
        }

        binding.chatIV.setOnClickListener {
            var intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("chatId", "")
            startActivity(intent)
        }

        binding.addFriendTV.setOnClickListener {
            if (binding.addFriendTV.text.equals("Add Friend")){

                FirebaseFirestore.getInstance().collection("Users")
                    .document(userId).update("friends.${Constants.currentUser.userId}", "request")

            }else if (binding.addFriendTV.text.equals("request") || binding.addFriendTV.text.equals("requested")){

                Toast.makeText(this, "Already Requested", Toast.LENGTH_SHORT).show()

            }else if (binding.addFriendTV.text.equals("accept") || binding.addFriendTV.text.equals("accepted")){

                Toast.makeText(this, "Already Friend", Toast.LENGTH_SHORT).show()

            }


        }


    }

    private fun getData() {
        postList!!.clear()
        FirebaseFirestore.getInstance().collection("Posts")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { queryDocumentSnapshots, error ->
                if (error != null) {
                    // Handle error
                    Log.d("LOGGER", "Exception " + error.message);
                    return@addSnapshotListener;
                }

                postList!!.clear()
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    for (documentSnapshot in queryDocumentSnapshots) {
                        val model = documentSnapshot.toObject(Posts::class.java)
                        if (model.userId == userId){
                            postList!!.add(model)
                        }
                    }
                    setAdapter()
                }

            }

    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView1.layoutManager = layoutManager
        adapter = PostsAdapter(this, postList, userId)
        binding.recyclerView1.adapter = adapter

    }

}