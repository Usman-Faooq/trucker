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
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class CreateGroupFragment : Fragment() {

    lateinit var binding : FragmentCreateGroupBinding
    private lateinit var fragmentContext: Context
    lateinit var mDialog : ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentCreateGroupBinding.inflate(layoutInflater)

        mDialog = ProgressDialog(fragmentContext)
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        setView()
        setListener()


        return binding.root
    }

    private fun setView() {

    }

    private fun setListener() {

        binding.addMembersTV.setOnClickListener {
            val intent = Intent(fragmentContext, AddMembersActivity::class.java)
            startActivity(intent)
        }


        binding.saveTV.setOnClickListener {

            if (!binding.groupNameET.text.isEmpty()){
                mDialog.show()
                var groupId = UUID.randomUUID().toString()

                val groupMap = hashMapOf(
                    "groupId" to groupId,
                    "groupName" to binding.groupNameET.text.toString(),
                    "membersCount" to Constants.userMap.size,
                    "adminId" to Constants.currentUser.userId,
                    "members" to Constants.userMap,
                )

                FirebaseFirestore.getInstance().collection("Groups")
                    .document(groupId).set(groupMap)
                    .addOnSuccessListener {

                        val requestedUserIDs = Constants.userMap
                            ?.filterValues { it == "Requested" }
                            ?.keys

                        if (requestedUserIDs != null) {
                            val updateCounter = requestedUserIDs.size
                            Log.d("LOGGER", "Size : $updateCounter")
                            var completedUpdates = 0
                            for (userID in requestedUserIDs) {
                                Log.d("LOGGER", "Position Out Side : $completedUpdates")
                                FirebaseFirestore.getInstance().collection("Users")
                                    .document(userID).update("groups.${groupId}", "Requested")
                                    .addOnSuccessListener {
                                        completedUpdates++
                                        if (completedUpdates == updateCounter) {
                                            mDialog.dismiss()
                                            Constants.userMap.clear()
                                            Toast.makeText(fragmentContext, "Group Created", Toast.LENGTH_SHORT).show()
                                            requireActivity().onBackPressed()
                                        }else{
                                            Log.d("LOGGER", "Else Part ${it.toString()}")
                                        }
                                    }.addOnFailureListener {
                                        mDialog.dismiss()
                                        Log.d("LOGGER", "E: ${it.message}")
                                        Toast.makeText(fragmentContext, "E: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }

                            }
                        }

                    }.addOnFailureListener {
                        mDialog.dismiss()
                        Log.d("LOGGER", "Error: ${it.message}")
                        Toast.makeText(fragmentContext, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
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

        binding.timePickerET.setText(Constants.userMap.size.toString())

    }

}