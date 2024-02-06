package com.buzzware.truckerworld.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.truckerworld.ChatActivity
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.databinding.ItemChatLayoutBinding
import com.buzzware.truckerworld.databinding.ItemDesignDateLayoutBinding

class DateAdapter(val context: Context, val list: List<String>) :
    RecyclerView.Adapter<DateAdapter.ViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignDateLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return 10//list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        // Check if the item is selected and update the background color accordingly
        if (selectedPosition == position) {
            holder.binding.root.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_color))
            holder.binding.dayTV.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.binding.dateTV.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.binding.root.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            holder.binding.dayTV.setTextColor(ContextCompat.getColor(context, R.color.gray_color))
            holder.binding.dateTV.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        // Set click listener for the item
        holder.binding.mainLayout.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
        }

    }

    inner class ViewHolder(val binding: ItemDesignDateLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
