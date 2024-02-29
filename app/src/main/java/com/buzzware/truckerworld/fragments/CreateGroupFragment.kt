package com.buzzware.truckerworld.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.buzzware.truckerworld.AddMembersActivity
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentCreateGroupBinding
import com.buzzware.truckerworld.model.GroupModel
import com.buzzware.truckerworld.model.Products
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class CreateGroupFragment : Fragment() {

    lateinit var binding: FragmentCreateGroupBinding
    private lateinit var fragmentContext: Context
    lateinit var mDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateGroupBinding.inflate(layoutInflater)

        mDialog = ProgressDialog(fragmentContext)
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        setListener()


        return binding.root
    }
    private fun setListener() {

        binding.addMembersTV.setOnClickListener {
            val intent = Intent(fragmentContext, AddMembersActivity::class.java)
            startActivity(intent)
        }

        binding.saveTV.setOnClickListener {
            if (!binding.groupNameET.text.isEmpty()) {


                val groupLimit = binding.groupLimitET.text.toString()
                if (Constants.userMap.size > groupLimit.toInt()) {
                    Toast.makeText(
                        context,
                        "You cannot add more then $groupLimit members in this group.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    mDialog.show()
                    var groupId = UUID.randomUUID().toString()

                    val groupMap = hashMapOf(
                        "createdDate" to System.currentTimeMillis(),
                        "groupCategory" to binding.categoryLV.text.toString(),
                        "groupName" to binding.groupNameET.text.toString(),
                        "groupLimit" to groupLimit.toLong(),
                        "userId" to Constants.currentUser.userId,
                        "members" to Constants.userMap,
                    )

                    FirebaseFirestore.getInstance().collection("PersonalRoutes")
                        .document(groupId).set(groupMap)
                        .addOnSuccessListener {

                            val requestedUserIDs = Constants.userMap
                                ?.filterValues { it == "requested" }
                                ?.keys

                            if (!requestedUserIDs.isNullOrEmpty()) {
                                val updateCounter = requestedUserIDs.size
                                Log.d("LOGGER", "requestedUserIDs Size : $updateCounter")
                                var completedUpdates = 0
                                for (userID in requestedUserIDs) {
                                    Log.d("LOGGER", "Position Out Side : $completedUpdates")
                                    FirebaseFirestore.getInstance().collection("Users")
                                        .document(userID).update("groups.${groupId}", "requested")
                                        .addOnSuccessListener {
                                            completedUpdates++
                                            if (completedUpdates == updateCounter) {
                                                mDialog.dismiss()
                                                Constants.userMap.clear()
                                                Toast.makeText(
                                                    fragmentContext,
                                                    "Group Created",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                requireActivity().onBackPressed()
                                            } else {
                                                Log.d("LOGGER", "Else Part ${it.toString()}")
                                            }
                                        }.addOnFailureListener {
                                            mDialog.dismiss()
                                            Log.d("LOGGER", "E: ${it.message}")
                                            Toast.makeText(
                                                fragmentContext,
                                                "E: ${it.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                }
                            } else {
                                mDialog.dismiss()
                                Toast.makeText(
                                    fragmentContext,
                                    "Group Created without members",
                                    Toast.LENGTH_SHORT
                                ).show()
                                requireActivity().onBackPressed()
                            }

                        }.addOnFailureListener {
                            mDialog.dismiss()
                            Log.d("LOGGER", "Error: ${it.message}")
                            Toast.makeText(
                                fragmentContext,
                                "Error: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onResume() {
        super.onResume()
        Log.d("GroupMembers", "onResume:${Constants.userMap.keys} ")

    }

}
