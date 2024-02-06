package com.buzzware.truckerworld.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ItemDesignFriendRequestLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class FriendRequestAdapter(val context: Context, val list: ArrayList<com.buzzware.truckerworld.model.User?>?, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignFriendRequestLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var model = list?.get(position)

        Glide.with(context).load(model?.image)
            .error(R.drawable.profile_dummy)
            .placeholder(R.drawable.profile_dummy)
            .into(holder.binding.profileIV)
        holder.binding.userNameTV.setText("${model?.firstName} ${model?.lastName}")


        holder.binding.AcceptTV.setOnClickListener {
            listener.onDateClick(position, "Accept")
        }

        holder.binding.ignoreTV.setOnClickListener {
            listener.onDateClick(position, "Ignore")
        }


    }

    interface OnItemClickListener {
        fun onDateClick(position: Int, check: String)
    }

    inner class ViewHolder(val binding: ItemDesignFriendRequestLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
