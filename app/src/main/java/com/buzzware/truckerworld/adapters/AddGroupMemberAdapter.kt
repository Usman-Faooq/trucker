package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.ChatActivity
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ItemChatLayoutBinding
import com.buzzware.truckerworld.databinding.ItemDesignAddGroupMemberLayoutBinding
import com.buzzware.truckerworld.model.User

class AddGroupMemberAdapter(val context: Context, val list: ArrayList<User?>?) :
    RecyclerView.Adapter<AddGroupMemberAdapter.ViewHolder>() {

    //private var addedUsersMap = hashMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignAddGroupMemberLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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

        holder.binding.addBtn.setOnClickListener {
            val userId = model?.userId
            if (userId != null) {
                if (Constants.userMap.containsKey(userId)) {
                    // User is already added, remove them
                    Constants.userMap.remove(userId)
                    Toast.makeText(context, "Member Removed", Toast.LENGTH_SHORT).show()
                } else {
                    // User is not added, add them
                    Constants.userMap[userId] = "requested"
                    Toast.makeText(context, "Member Added", Toast.LENGTH_SHORT).show()
                }
            }


        }

    }

    inner class ViewHolder(val binding: ItemDesignAddGroupMemberLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
