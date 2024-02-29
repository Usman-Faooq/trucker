package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.truckerworld.ChatActivity
import com.buzzware.truckerworld.databinding.ItemChatLayoutBinding
import com.buzzware.truckerworld.databinding.ItemDesignPersonalRouteLayoutBinding
import com.buzzware.truckerworld.model.GroupModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class PersonalRouteAdapter(
    val context: Context,
    val list: ArrayList<GroupModel?>?,
    private val listener: OnItemClickListener,
    private val deleteItem : (item :GroupModel)->Unit
) :
    RecyclerView.Adapter<PersonalRouteAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDesignPersonalRouteLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list!!.get(position)
        holder.bind(item!!)
        holder.binding.apply {
            item?.let { item ->
                groupName.text = item.groupName
                root.setOnClickListener {
                    listener.onDateClick(item.groupName, item.docId)
                }
            }
            deleteIV.setOnClickListener {
                deleteItem.invoke(item!!)
            }
        }
    }

    inner class ViewHolder(val binding: ItemDesignPersonalRouteLayoutBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(groups : GroupModel){
                val childAdapter =  ChildPersonalRouteAdapter(emptyList(),context)
                binding.apply {
                   /* groupAdminRV.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL,false)
                    groupAdminRV.adapter = childAdapter*/
                }
            }
        }

    interface OnItemClickListener {
        fun onDateClick(itemName: String, type: String)
    }
}
