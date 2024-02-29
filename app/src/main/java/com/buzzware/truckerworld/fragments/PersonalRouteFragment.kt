package com.buzzware.truckerworld.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.adapters.ChatAdapter
import com.buzzware.truckerworld.adapters.PersonalRouteAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ActivityDashBoardBinding
import com.buzzware.truckerworld.databinding.FragmentPersonalRouteBinding
import com.buzzware.truckerworld.model.GroupModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class PersonalRouteFragment : Fragment(), PersonalRouteAdapter.OnItemClickListener {

    lateinit var binding: FragmentPersonalRouteBinding
    private lateinit var fragmentContext: Context
    private lateinit var mDialog: ProgressDialog

    private var groupList: ArrayList<GroupModel?>? = ArrayList()

    interface OnDataChangeListener {
        fun onDataChanged(data: String, groupID: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPersonalRouteBinding.inflate(layoutInflater)

        mDialog = ProgressDialog(requireContext())
        mDialog.apply {
            setMessage("Please wait...")
            setCancelable(false)
        }

        getMyGroups()

        return binding.root
    }


    private fun getMyGroups() {
        lateinit var currentUserGroups: Set<String>
        FirebaseFirestore.getInstance().collection("PersonalRoutes")
            .addSnapshotListener { snapShot, error ->

                groupList!!.clear()
                if (snapShot != null && !snapShot.isEmpty) {
                    snapShot.forEach {
                        val model = it.toObject(GroupModel::class.java)
                        model.docId = it.id

                        if (Constants.currentUser.userId == model.userId) {
                            groupList!!.add(model)
                        }
                    }
                    if (Constants.currentUser.groups?.size != null) {
                        currentUserGroups =
                            Constants.currentUser.groups?.filterValues { it == "accepted" }?.keys!!

                        Log.d("currentUserGroups", "getMyGroups: ${currentUserGroups}")
                        getData(currentUserGroups)
                    }
                    setAdapter()
                    mDialog.dismiss()
                }
            }


    }

    private fun getData(groupKey: Set<String>) {

        mDialog.show()
        for (groupID in groupKey) {
            FirebaseFirestore.getInstance().collection("PersonalRoutes").document(groupID)
                .addSnapshotListener { value, error ->
                    val group = value!!.toObject(GroupModel::class.java)
                    group!!.docId = value.id
                    groupList!!.add(group)

                    setAdapter()
                    mDialog.dismiss()
                    if (error != null) {
                        mDialog.dismiss()
                        Log.d("LOGGER", "Exception " + error.message);
                        return@addSnapshotListener;
                    }
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
        binding.recyclerView.adapter =
            PersonalRouteAdapter(fragmentContext, groupList, this) { itemPos ->
                mDialog.show()
                FirebaseFirestore.getInstance().collection("PersonalRoutes").document(itemPos.docId)
                    .delete().addOnSuccessListener {
                        Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show()
                        mDialog.dismiss()
                    }
            }
    }

    override fun onDateClick(itemName: String, groupId: String) {
        val activity = activity as? OnDataChangeListener
        activity?.onDataChanged(itemName, groupId)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}