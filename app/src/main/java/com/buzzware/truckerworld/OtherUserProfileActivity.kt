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
import com.buzzware.truckerworld.model.Products
import com.buzzware.truckerworld.model.User
import com.buzzware.truckerworld.utils.gone
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class OtherUserProfileActivity : AppCompatActivity() {

    lateinit var binding : ActivityOtherUserProfileBinding
    private var postList: ArrayList<Products?>? = ArrayList()
    private var userList: ArrayList<User?>? = ArrayList()
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

        val friendsList = Constants.currentUser.friendsList
            ?.filterValues { it == "accepted" }
            ?.keys

        FirebaseFirestore.getInstance().collection("Users")
            .document(userId).addSnapshotListener { value, error ->
                if (error != null) {
                    // Handle the exception
                    Log.e("Firestore Listener", "Error listening to user document: $error")
                    return@addSnapshotListener
                }

                if (value != null && value.exists()) {
                    val user = value.toObject(User::class.java)
                    user!!.userId = value.id
                    Constants.otherName = "${user!!.firstName} ${user.lastName}"
                    Constants.otherImage = user.image

                    binding.userNameTV.text = "${user.firstName} ${user.lastName}"
                    binding.cityTV.text = user.address
                    if(user.userId == Constants.currentUser.userId){
                        binding.addFriendTV.gone()
                    }
                    Glide.with(this).load(user.image)
                        .error(R.drawable.profile_dummy)
                        .placeholder(R.drawable.profile_dummy)
                        .into(binding.profileIV)

                    if (user.friendsList?.get(Constants.currentUser.userId).equals("request", ignoreCase = true) ||
                        user.friendsList?.get(Constants.currentUser.userId).equals("requested", ignoreCase = true)){
                        binding.addFriendTV.text = "Requested"
                        binding.chatIV.visibility = View.INVISIBLE
                    }else if (user.friendsList?.get(Constants.currentUser.userId).equals("accept", ignoreCase = true) ||
                        user.friendsList?.get(Constants.currentUser.userId).equals("accepted", ignoreCase = true)){
                        binding.addFriendTV.text = "Friend"
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

                //add request to friend's database
                FirebaseFirestore.getInstance().collection("Users")
                    .document(userId).update("friendsList.${Constants.currentUser.userId}", "request")

                //add request to my database
                FirebaseFirestore.getInstance().collection("Users").document(Constants.currentUser.userId)
                    .update("friendsList.$userId", "request")

            }else if (binding.addFriendTV.text.equals("Request") || binding.addFriendTV.text.equals("Requested")){

                Toast.makeText(this, "Already Requested", Toast.LENGTH_SHORT).show()

            }else if (binding.addFriendTV.text.equals("Accept") || binding.addFriendTV.text.equals("Accepted")){

                Toast.makeText(this, "Already Friend", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun getData() {
        postList!!.clear()
        FirebaseFirestore.getInstance().collection("Products")
            .addSnapshotListener { queryDocumentSnapshots, error ->
                if (error != null) {
                    // Handle error
                    Log.d("LOGGER", "Exception " + error.message);
                    return@addSnapshotListener;
                }

                postList!!.clear()
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    for (documentSnapshot in queryDocumentSnapshots) {
                        val model = documentSnapshot.toObject(Products::class.java)
                        if (model.userId == userId){
                            model.postId = documentSnapshot.id
                            postList!!.add(model)
                        }
                    }
                    getAllUser()
                }

            }

    }

    private fun getAllUser() {
        userList!!.clear()
        FirebaseFirestore.getInstance().collection("Users")
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val tempUserList = mutableListOf<User>()
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    queryDocumentSnapshots.forEach {
                        val model = it.toObject(User::class.java)
                        model.userId = it.id
                        tempUserList.add(model)
                    }
                    userList?.addAll(tempUserList)
                    setAdapter()
                }
            }
    }
    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView1.layoutManager = layoutManager
        adapter = PostsAdapter(this, postList,userList, userId)
        binding.recyclerView1.adapter = adapter

    }

}