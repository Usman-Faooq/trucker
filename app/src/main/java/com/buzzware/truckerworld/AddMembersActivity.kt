package com.buzzware.truckerworld

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
    private lateinit var mDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDialog = ProgressDialog(this)
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)
        getUsers()
    }

    private fun getUsers() {

        mDialog.show()
        val requestedUserIDs = Constants.currentUser.friendsList
            ?.filterValues { it == "accepted" }
            ?.keys


        userList!!.clear()
        if (requestedUserIDs != null) {
            for (userID in requestedUserIDs) {

                FirebaseFirestore.getInstance().collection("Users").document(userID)
                    .get().addOnSuccessListener {
                        mDialog.dismiss()
                        val userData = it.toObject(User::class.java)
                        userList!!.add(userData)
                        userData!!.userId = it.id
                        setAdapter()

                    }.addOnFailureListener {
                        mDialog.dismiss()
                        Toast.makeText(this@AddMembersActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }


    private fun setAdapter() {
        binding.recyclerView.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
        binding.recyclerView.setAdapter(AddGroupMemberAdapter(this, userList))
    }
}