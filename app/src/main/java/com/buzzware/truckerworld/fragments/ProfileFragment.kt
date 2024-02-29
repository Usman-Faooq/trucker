package com.buzzware.truckerworld.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.adapters.PostsAdapter
import com.buzzware.truckerworld.adapters.ProfileProductAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentProfileBinding
import com.buzzware.truckerworld.model.Products
import com.buzzware.truckerworld.model.User
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList


class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private lateinit var fragmentContext: Context
    private lateinit var dialog: ProgressDialog
    private var postList: ArrayList<Products?>? = ArrayList()
    private var userList: ArrayList<User?>? = ArrayList()
    lateinit var adapter: ProfileProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        dialog = ProgressDialog(requireContext())

        getData()
        setView()

        return binding.root
    }

    private fun getData() {
        showDialog()
        FirebaseFirestore.getInstance().collection("Products")
            .addSnapshotListener { queryDocumentSnapshots, error ->
                if (error != null) {
                    // Handle error
                    hideDialog()
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener;
                }

                postList!!.clear()
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {

                    queryDocumentSnapshots.forEach {
                        val model = it.toObject(Products::class.java)
                        if (model.userId.equals(Constants.currentUser.userId)) {
                            model.postId = it.id
                            postList!!.add(model)
                        }
                    }

                    getAllUser()
                }
            }
    }


    private fun getAllUser() {
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
                    hideDialog()
                }
            }
    }

    private fun setAdapter() {
        val layoutManager =
            LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView1.layoutManager = layoutManager
        adapter = ProfileProductAdapter(fragmentContext, postList, userList, Constants.currentUser.userId)
        binding.recyclerView1.adapter = adapter

    }

    private fun setView() {

        binding.apply {
            userNameTV.text =
                "${Constants.currentUser.firstName}  ${Constants.currentUser.lastName}"
            cityTV.text = Constants.currentUser.address
        }

        Glide.with(requireActivity()).load(Constants.currentUser.image)
            .placeholder(R.drawable.post_place_holder_iv).fitCenter().into(binding.profileIV)
        binding.profileIV.scaleType = ImageView.ScaleType.FIT_XY


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

    override fun onResume() {
        super.onResume()
        setView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}