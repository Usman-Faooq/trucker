package com.buzzware.truckerworld.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.adapters.AddGroupMemberAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentAddGroupMemberBinding
import com.buzzware.truckerworld.model.User
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList


class AddGroupMemberFragment : Fragment() {

    private lateinit var mDialog: ProgressDialog
    lateinit var binding : FragmentAddGroupMemberBinding
    private var userList: ArrayList<User?>? = ArrayList()
    private lateinit var fragmentContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentAddGroupMemberBinding.inflate(layoutInflater)
        mDialog = ProgressDialog(requireContext())
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)
        getUsers()

        return binding.root
    }


    private fun getUsers() {
        mDialog.show()
        val requestedUserIDs = Constants.currentUser.friendsList
            ?.filterValues { it == "accepted" }
            ?.keys
        Log.d("userRequest", "getUsers: $requestedUserIDs")
        userList!!.clear()
        if (requestedUserIDs != null) {
            for (userID in requestedUserIDs) {

                FirebaseFirestore.getInstance().collection("Users").document(userID)
                    .get().addOnSuccessListener {
                        mDialog.dismiss()
                        val userData = it.toObject(User::class.java)
                        userData!!.userId = it.id
                        userList!!.add(userData)
                        setAdapter()

                    }.addOnFailureListener {
                        mDialog.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }


    private fun setAdapter() {
        binding.recyclerView.setLayoutManager(LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false))
        binding.recyclerView.setAdapter(AddGroupMemberAdapter(fragmentContext, userList))
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}