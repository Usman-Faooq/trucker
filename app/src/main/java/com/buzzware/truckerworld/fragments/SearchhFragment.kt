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
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.adapters.PostsAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentSearchhBinding
import com.buzzware.truckerworld.model.Products
import com.buzzware.truckerworld.model.User
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList
import java.util.Locale

class SearchhFragment : Fragment() {

    private lateinit var binding: FragmentSearchhBinding
    private lateinit var adapter: PostsAdapter
    private lateinit var mDialog: ProgressDialog
    private lateinit var fragmentContext: Context
    private var postList: ArrayList<Products?>? = ArrayList()
    private var userList: ArrayList<User?>? = ArrayList()
    private var queryPostList: ArrayList<Products?>? = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchhBinding.inflate(layoutInflater)

        mDialog = ProgressDialog(requireContext())
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        getData()
        binding.apply {
            postSendIV.setOnClickListener {
                searchQuery()
            }
        }

        return binding.root

    }

    private fun getData() {
        postList!!.clear()
        mDialog.show()
        FirebaseFirestore.getInstance().collection("Products")
            .addSnapshotListener { queryDocumentSnapshots, error ->
                if (error != null) {
                    mDialog.dismiss()
                    Log.d("LOGGER", "Exception " + error.message);
                    return@addSnapshotListener;
                }

                postList!!.clear()
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty) {
                    queryDocumentSnapshots.forEach {
                        val model = it.toObject(Products::class.java)
                        model.postId = it.id
                        when (model.addedby) {
                            "user" -> {
                                postList!!.add(model)
                            }
                        }
                    }
                }
                getAllUser(postList!!)
            }

    }


    private fun getAllUser(postsList: ArrayList<Products?>) {
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
                    setAdapter(postsList)
                    mDialog.dismiss()
                }
            }
    }

    private fun setAdapter(postsList: ArrayList<Products?>) {
        val layoutManager =
            LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        adapter = PostsAdapter(fragmentContext, postsList!!, userList, Constants.currentUser.userId)
        binding.recyclerView.adapter = adapter
    }

    private fun setView() {

        /*  binding.searchTV.isSubmitButtonEnabled = true
          binding.searchTV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
              override fun onQueryTextSubmit(query: String?): Boolean {
                  binding.searchTV.clearFocus()
                  return true
              }

              override fun onQueryTextChange(newText: String?): Boolean {
                  queryPostList!!.clear()
                  val searchText = newText!!.toLowerCase(Locale.getDefault())
                  if (searchText.isNotEmpty()) {
                      Log.d("onQueryTextChange", "onQueryTextChange: ${queryPostList!!.size}")
                      postList!!.forEach {
                          if (it!!.description.toLowerCase(Locale.getDefault())
                                  .contains(searchText)
                          ) {
                              queryPostList!!.add(it)
                              Log.d("onQueryTextChange", "onQueryTextChange: ${queryPostList!!.size}")
                          }
                          setAdapter(queryPostList!!)
                      }
                  } else {
                      setAdapter(postList!!)
                  }
                  return false
              }
          })*/

    }

    private fun searchQuery() {
        queryPostList!!.clear()
        val newText = binding.searchTV.text.toString()
        queryPostList!!.clear()
        val searchText = newText.toLowerCase(Locale.getDefault())
        if (searchText.isNotEmpty()) {
            Log.d("onQueryTextChange", "onQueryTextChange: ${queryPostList!!.size}")
            postList!!.forEach {
                if (it!!.description.toLowerCase(Locale.getDefault()).contains(searchText)) {
                    queryPostList!!.add(it)
                    Log.d("onQueryTextChange", "onQueryTextChange: ${queryPostList!!.size}")
                }
                setAdapter(queryPostList!!)
            }
        } else {
            setAdapter(postList!!)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}