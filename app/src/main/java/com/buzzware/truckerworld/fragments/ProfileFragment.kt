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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.adapters.PostsAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentProfileBinding
import com.buzzware.truckerworld.model.Posts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.ArrayList


class ProfileFragment : Fragment() {

    lateinit var binding : FragmentProfileBinding
    private lateinit var fragmentContext: Context
    private var postList: ArrayList<Posts?>? = ArrayList()
    lateinit var adapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        getData()
        setView()
        setListener()

        return binding.root
    }

    private fun getData() {
        FirebaseFirestore.getInstance().collection("Products")
            .addSnapshotListener { queryDocumentSnapshots, error ->
                if (error != null) {
                    // Handle error
                    Log.d("LOGGER", "Exception " + error.message);
                    return@addSnapshotListener;
                }

                postList!!.clear()
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {

                    queryDocumentSnapshots.forEach {
                        val model = it.toObject(Posts::class.java)
                        if (model.userId.equals(Constants.currentUser.userId)){
                            postList!!.add(model)
                        }
                    }
                    setAdapter()
                }

            }
        postList!!.clear()

    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView1.layoutManager = layoutManager
        adapter = PostsAdapter(fragmentContext, postList, Constants.currentUser.userId)
        binding.recyclerView1.adapter = adapter

    }

    private fun setView() {

        binding.userNameTV.text = Constants.currentUser.firstName + " " + Constants.currentUser.lastName
        Glide.with(requireActivity()).load(Constants.currentUser.image).placeholder(R.drawable.profile_dummy).fitCenter().into(binding.profileIV)
        binding.profileIV.scaleType = ImageView.ScaleType.FIT_XY



    }

    private fun setListener() {


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