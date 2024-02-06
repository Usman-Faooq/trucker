package com.buzzware.truckerworld.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.adapters.FriendRequestAdapter
import com.buzzware.truckerworld.adapters.GroupRequestAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentRequestsBinding
import com.buzzware.truckerworld.model.GroupModel
import com.buzzware.truckerworld.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.ArrayList

class RequestsFragment : Fragment(), FriendRequestAdapter.OnItemClickListener, GroupRequestAdapter.OnItemClickListener {

    lateinit var binding : FragmentRequestsBinding
    private lateinit var fragmentContext: Context
    lateinit var mDialog : ProgressDialog
    private var userList: ArrayList<User?>? = ArrayList()
    private var groupList: ArrayList<GroupModel?>? = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentRequestsBinding.inflate(layoutInflater)

        mDialog = ProgressDialog(fragmentContext)
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)


        getFriendRequestList()
        //getGroupRequestList()

        setView()
        setListener()

        return binding.root
    }

    private fun getFriendRequestList() {


        val requestedUserIDs = Constants.currentUser.friends
            ?.filterValues { it == "Requested" }
            ?.keys


        userList!!.clear()
        if (requestedUserIDs != null) {
            for (userID in requestedUserIDs) {

                FirebaseFirestore.getInstance().collection("Users").document(userID)
                    .get().addOnSuccessListener {

                        val userData = it.toObject(User::class.java)
                        userList!!.add(userData)
                        setFriendRequestAdapter()

                    }.addOnFailureListener {

                    }
            }
        }

    }


    private fun getGroupRequestList() {


        val requestedGroupIDs = Constants.currentUser.groups
            ?.filterValues { it == "Requested" }
            ?.keys


        groupList!!.clear()
        if (requestedGroupIDs != null) {
            for (groupID in requestedGroupIDs) {

                FirebaseFirestore.getInstance().collection("Groups").document(groupID)
                    .get().addOnSuccessListener {

                        val userData = it.toObject(GroupModel::class.java)
                        groupList!!.add(userData)
                        setGroupRequestAdapter()

                    }.addOnFailureListener {

                    }
            }
        }

    }


    private fun setView() {

    }

    private fun setListener() {

        binding.friendRequestTV.setOnClickListener {
            binding.friendRequestTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.friendRequestTV.setBackgroundResource(R.color.gray_color)
            binding.groupRequestTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray_color))
            binding.groupRequestTV.setBackgroundResource(R.color.white)

            if (!userList?.isEmpty()!!){
                getFriendRequestList()
            }else{
                binding.recyclerView.visibility = View.INVISIBLE
            }


            //setFriendRequestAdapter()
        }

        binding.groupRequestTV.setOnClickListener {
            binding.recyclerView.visibility = View.VISIBLE
            binding.groupRequestTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.groupRequestTV.setBackgroundResource(R.color.gray_color)
            binding.friendRequestTV.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray_color))
            binding.friendRequestTV.setBackgroundResource(R.color.white)

            getGroupRequestList()
        }

    }

    private fun setFriendRequestAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = FriendRequestAdapter(fragmentContext, userList, this)
    }

    private fun setGroupRequestAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = GroupRequestAdapter(fragmentContext, groupList, this)
    }

    override fun onDateClick(position: Int, check: String) {

        if (check == "Accept"){

            FirebaseFirestore.getInstance().collection("Users")
                .document(Constants.currentUser.userId).update("friends.${userList?.get(position)?.userId}", "Accepted")
            FirebaseFirestore.getInstance().collection("Users")
                .document(userList?.get(position)!!.userId).update("friends.${Constants.currentUser.userId}", "Accepted")
            Constants.currentUser.friends?.set(userList!!.get(position)!!.userId, "Accepted")

            userList!!.removeAt(position)
            setFriendRequestAdapter()

        }else{

            FirebaseFirestore.getInstance().collection("Users")
                .document(Constants.currentUser.userId).update("friends.${userList?.get(position)?.userId}", "Ignore")
            Constants.currentUser.friends?.set(userList!!.get(position)!!.userId, "Ignore")

            userList!!.removeAt(position)
            setFriendRequestAdapter()

        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onGroupClicked(position: Int, check: String) {
        if (check == "Accept") {

            FirebaseFirestore.getInstance().collection("Users")
                .document(Constants.currentUser.userId)
                .update("groups.${groupList?.get(position)?.groupId}", "Accepted")
            FirebaseFirestore.getInstance().collection("Groups")
                .document(groupList?.get(position)!!.groupId)
                .update("members.${Constants.currentUser.userId}", "Accepted")
            Constants.currentUser.friends?.set(groupList!!.get(position)!!.groupId, "Accepted")

            groupList!!.removeAt(position)
            setGroupRequestAdapter()

        } else {

            FirebaseFirestore.getInstance().collection("Users")
                .document(Constants.currentUser.userId)
                .update("groups.${groupList?.get(position)?.groupId}", "Ignore")
            Constants.currentUser.friends?.set(groupList!!.get(position)!!.groupId, "Ignore")

            groupList!!.removeAt(position)
            setGroupRequestAdapter()

        }

    }
}