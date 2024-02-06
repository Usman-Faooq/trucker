package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.truckerworld.ChatActivity
import com.buzzware.truckerworld.databinding.ItemChatLayoutBinding
import com.buzzware.truckerworld.databinding.ItemDesignDietLayoutBinding

class DietAdapter(val context: Context, val list: List<String>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<DietAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignDietLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return 6//list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.dietTV.text = "Diet Plan ${position + 1}"

        holder.binding.root.setOnClickListener {
            listener.onItemClick("Item Name", "${position + 1}")
        }

    }

    inner class ViewHolder(val binding: ItemDesignDietLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onItemClick(itemName: String, type: String)
    }
}
