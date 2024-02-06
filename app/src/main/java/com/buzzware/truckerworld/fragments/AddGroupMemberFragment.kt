package com.buzzware.truckerworld.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.adapters.AddGroupMemberAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentAddGroupMemberBinding
import com.buzzware.truckerworld.model.User
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList


class AddGroupMemberFragment : Fragment() {

    lateinit var binding : FragmentAddGroupMemberBinding
    private var userList: ArrayList<User?>? = ArrayList()
    private lateinit var fragmentContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentAddGroupMemberBinding.inflate(layoutInflater)

        getUsers()

        return binding.root
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
        binding.recyclerView.setLayoutManager(LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false))
        binding.recyclerView.setAdapter(AddGroupMemberAdapter(fragmentContext, userList))
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}