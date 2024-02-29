package com.buzzware.truckerworld.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.adapters.DietPlanDetailsAdpater
import com.buzzware.truckerworld.adapters.NotificationAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentNotificationBinding
import com.buzzware.truckerworld.databinding.FragmentPersonalRouteBinding
import com.buzzware.truckerworld.model.NotificationModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.RemoteMessage.Notification


class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var fragmentContext: Context
    private val notificationList: ArrayList<NotificationModel> = ArrayList()
    private lateinit var dialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        dialog = ProgressDialog(requireContext())
        getData()
        return binding.root
    }

    private fun getData() {
        showDialog()
        FirebaseFirestore.getInstance().collection("Notification")
            .whereEqualTo("toId", Constants.currentUser.userId)
            .addSnapshotListener { value, error ->
                if (value != null && !value.isEmpty) {
                    value.forEach {
                        val notification = it.toObject(NotificationModel::class.java)
                        notificationList.add(notification)
                        Log.d("notificationList", "getData: ${notificationList.size}")
                        setAdapter(notificationList)
                    }
                    hideDialog()
                }
            }

    }

    private fun setAdapter(list: ArrayList<NotificationModel>) {
        binding.apply {
            notificationRv.layoutManager =
                LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
            notificationRv.adapter = NotificationAdapter(list)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}