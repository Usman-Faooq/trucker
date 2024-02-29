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
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.adapters.OnlineMemberAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentFriendsListBinding
import com.buzzware.truckerworld.databinding.FragmentOnlineMemberBinding
import com.buzzware.truckerworld.model.User
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class FriendsListFragment : Fragment() {

    lateinit var binding: FragmentFriendsListBinding
    private lateinit var fragmentContext: Context
    private lateinit var dialog: ProgressDialog
    private var userList: ArrayList<User?>? = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsListBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        dialog = ProgressDialog(requireContext())
        getUsers()

        return binding.root
    }

    private fun getUsers() {

        showDialog()
        val requestedUserIDs = Constants.currentUser.friendsList
            ?.filterValues { it == "accepted" }
            ?.keys
        Log.d("userRequest", "getUsers: $requestedUserIDs")
        userList!!.clear()
        if (requestedUserIDs != null) {
            for (userID in requestedUserIDs) {

                FirebaseFirestore.getInstance().collection("Users").document(userID)
                    .get().addOnSuccessListener {
                        hideDialog()
                        val userData = it.toObject(User::class.java)
                        userData!!.userId = it.id
                        userList!!.add(userData)
                        setAdapter()

                    }.addOnFailureListener {
                        hideDialog()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    private fun showDialog(msg: String = "Please wait...") {
        dialog.apply {
            setMessage(msg)
            setCancelable(true)
            show()
        }
    }

    private fun hideDialog() {
        dialog.dismiss()
    }
    private fun setAdapter() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = OnlineMemberAdapter(fragmentContext, userList)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }


}