package com.buzzware.truckerworld.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.truckerworld.ChatActivity
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.databinding.ItemChatLayoutBinding
import com.buzzware.truckerworld.databinding.ItemDesignDateLayoutBinding
import com.buzzware.truckerworld.fragments.ScheduleFragment
import com.buzzware.truckerworld.model.DayName

class DateAdapter(
    val context: Context,
    val list: List<DayName>,
    val onItemClick: (position: Int) -> Unit
) :
    RecyclerView.Adapter<DateAdapter.ViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDesignDateLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val daysItem = list.get(position)
        holder.binding.apply {
            dayTV.text = daysItem.dayName
            dateTV.text = daysItem.dayNumber
        }
        Log.d("LOGGER", "fORMaTE: $daysItem.")
        if(daysItem.isSelected){
            holder.binding.root.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_color))
            holder.binding.dayTV.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.binding.dateTV.setTextColor(ContextCompat.getColor(context, R.color.white))
        }else{
            holder.binding.root.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            holder.binding.dayTV.setTextColor(ContextCompat.getColor(context, R.color.gray_color))
            holder.binding.dateTV.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        // Set click listener for the item
        holder.binding.mainLayout.setOnClickListener {
            onItemClick.invoke(position)
        }

    }

    inner class ViewHolder(val binding: ItemDesignDateLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}
