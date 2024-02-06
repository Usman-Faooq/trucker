package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.truckerworld.ChatActivity
import com.buzzware.truckerworld.databinding.ItemChatLayoutBinding
import com.buzzware.truckerworld.databinding.ItemDesignAddGroupMemberLayoutBinding
import com.buzzware.truckerworld.databinding.ItemDesignGroupRequestLayoutBinding
import com.buzzware.truckerworld.model.GroupModel

class GroupRequestAdapter(val context: Context, val list: List<GroupModel?>?, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<GroupRequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignGroupRequestLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var model = list?.get(position)

        holder.binding.userNameTV.setText(model!!.groupName)

        holder.binding.AcceptTV.setOnClickListener {
            listener.onGroupClicked(position, "Accept")
        }

        holder.binding.ignoreTV.setOnClickListener {
            listener.onGroupClicked(position, "Ignore")
        }

    }

    inner class ViewHolder(val binding: ItemDesignGroupRequestLayoutBinding) : RecyclerView.ViewHolder(binding.root)


    interface OnItemClickListener {
        fun onGroupClicked(position: Int, check: String)
    }

}
