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
import com.buzzware.truckerworld.adapters.PostsAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentGroupDetailBinding
import com.buzzware.truckerworld.model.GroupModel
import com.buzzware.truckerworld.model.Products
import com.buzzware.truckerworld.model.User
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class GroupDetailFragment : Fragment(), PersonalRouteFragment.OnDataChangeListener {

    lateinit var binding: FragmentGroupDetailBinding
    private lateinit var fragmentContext: Context
    lateinit var adapter: PostsAdapter
    private lateinit var dialog: ProgressDialog
    private var postList: ArrayList<Products?>? = ArrayList()
    private var userList: ArrayList<User?>? = ArrayList()
    private var currentGroupID = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupDetailBinding.inflate(layoutInflater)
        dialog = ProgressDialog(requireContext())
        currentGroupID = arguments?.getString("groupID")!!
        getData()

        return binding.root
    }

    private fun getData() {
        postList!!.clear()
        showDialog()
        FirebaseFirestore.getInstance().collection("PersonalRoutes").document(currentGroupID)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    hideDialog()
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener;
                }

                if (value!!.exists()) {
                    val groupDetails = value.toObject(GroupModel::class.java)
                    var currentGroupUsers: MutableSet<String> =
                        groupDetails?.members?.filterValues { it == "accepted" }?.keys!!.toMutableSet()
                    currentGroupUsers.add(groupDetails.userId)
                    getProductVideos(currentGroupUsers)
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

    private fun getProductVideos(currentGroupUsers: Set<String>?) {

        if (currentGroupUsers != null) {
            postList!!.clear()
            for (groupUser in currentGroupUsers) {
                FirebaseFirestore.getInstance().collection("Products")
                    .whereEqualTo("userId", groupUser)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            hideDialog()
                            Log.d("LOGGER", "Exception " + error.message);
                            return@addSnapshotListener;
                        }
                        postList!!.clear()
                        value!!.forEach { query ->
                            val post = query.toObject(Products::class.java)
                            post.postId = query.id
                            postList!!.add(post)
                        }
                        getAllUser()
                    }
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
        binding.recyclerView.setLayoutManager(
            LinearLayoutManager(
                fragmentContext,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        binding.recyclerView.adapter = PostsAdapter(
            fragmentContext,
            postList,userList,
            Constants.currentUser.userId
        )
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onDataChanged(data: String, groupID: String) {
        currentGroupID = groupID
    }
}