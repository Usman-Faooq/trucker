package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.truckerworld.ChatActivity
import com.buzzware.truckerworld.databinding.ItemChatLayoutBinding
import com.buzzware.truckerworld.databinding.ItemDesignPersonalRouteLayoutBinding

class PersonalRouteAdapter(val context: Context, val list: List<String>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<PersonalRouteAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignPersonalRouteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return 3//list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.root.setOnClickListener {
            listener.onDateClick("Item Name", "Item Type")
        }

    }

    inner class ViewHolder(val binding: ItemDesignPersonalRouteLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onDateClick(itemName: String, type: String)
    }
}
