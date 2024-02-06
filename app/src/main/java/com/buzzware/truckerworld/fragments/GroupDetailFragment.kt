package com.buzzware.truckerworld.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.adapters.ChatAdapter
import com.buzzware.truckerworld.adapters.MainLayoutAdapter
import com.buzzware.truckerworld.adapters.PostsAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentGroupDetailBinding
import com.buzzware.truckerworld.model.Posts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.ArrayList

class GroupDetailFragment : Fragment() {

    lateinit var binding : FragmentGroupDetailBinding
    private lateinit var fragmentContext: Context
    lateinit var adapter: PostsAdapter
    private var postList: ArrayList<Posts?>? = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupDetailBinding.inflate(layoutInflater)

        getData()


        return binding.root
    }

    private fun getData() {
        postList!!.clear()
        //mDialog.show()
        FirebaseFirestore.getInstance().collection("Posts")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { queryDocumentSnapshots, error ->
                if (error != null) {
                    // Handle error
                    //mDialog.dismiss()
                    Log.d("LOGGER", "Exception " + error.message);
                    return@addSnapshotListener;
                }

                postList!!.clear()
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    for (documentSnapshot in queryDocumentSnapshots) {
                        val model = documentSnapshot.toObject(Posts::class.java)
                        if (!model.userId.equals(Constants.currentUser.userId)){
                            postList!!.add(model)
                        }
                    }
                    setAdapter()
                }

            }

    }

    private fun setAdapter() {
        binding.recyclerView.setLayoutManager(LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false))
        binding.recyclerView.setAdapter(PostsAdapter(fragmentContext, postList, Constants.currentUser.userId))
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }


}