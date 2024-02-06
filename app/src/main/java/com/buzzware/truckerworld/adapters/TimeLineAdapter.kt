package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.truckerworld.ChatActivity
import com.buzzware.truckerworld.databinding.ItemChatLayoutBinding
import com.buzzware.truckerworld.databinding.ItemDesignDateTimelineLayoutBinding

class TimeLineAdapter(val context: Context, val list: List<String>) :
    RecyclerView.Adapter<TimeLineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignDateTimelineLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return 10//list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



    }

    inner class ViewHolder(val binding: ItemDesignDateTimelineLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
