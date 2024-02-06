package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.truckerworld.ChatActivity
import com.buzzware.truckerworld.databinding.ItemChatLayoutBinding
import com.buzzware.truckerworld.databinding.ItemDesignMainLayoutBinding

class MainLayoutAdapter(val context: Context, var list: List<String>) :
    RecyclerView.Adapter<MainLayoutAdapter.ViewHolder>() {

    private var filteredList: List<String> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignMainLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.titleTV.text = filteredList[position]

    }

    fun filterItems(query: String) {
        filteredList = list.filter { it.contains(query, ignoreCase = true) }
        notifyDataSetChanged()
    }


    inner class ViewHolder(val binding: ItemDesignMainLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
