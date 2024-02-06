package com.buzzware.truckerworld

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.adapters.AddGroupMemberAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ActivityAddMembersBinding
import com.buzzware.truckerworld.model.User
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class AddMembersActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddMembersBinding
    private var userList: ArrayList<User?>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)


        getUsers()

    }



    private fun getUsers() {


        val requestedUserIDs = Constants.currentUser.friends
            ?.filterValues { it == "Accepted" }
            ?.keys


        userList!!.clear()
        if (requestedUserIDs != null) {
            for (userID in requestedUserIDs) {

                FirebaseFirestore.getInstance().collection("Users").document(userID)
                    .get().addOnSuccessListener {

                        val userData = it.toObject(User::class.java)
                        userList!!.add(userData)
                        setAdapter()

                    }.addOnFailureListener {

                    }
            }
        }

    }


    private fun setAdapter() {
        binding.recyclerView.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
        binding.recyclerView.setAdapter(AddGroupMemberAdapter(this, userList))
    }
}