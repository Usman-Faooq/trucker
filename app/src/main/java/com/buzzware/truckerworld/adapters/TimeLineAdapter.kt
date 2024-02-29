package com.buzzware.truckerworld.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.truckerworld.databinding.ItemDesignDateTimelineLayoutBinding
import com.buzzware.truckerworld.model.ScheduleModel
import com.buzzware.truckerworld.utils.formatDate
import com.buzzware.truckerworld.utils.formatTime
import java.util.Calendar
import java.util.Date

class TimeLineAdapter(val context: Context, val list: ArrayList<ScheduleModel?>?) :
    RecyclerView.Adapter<TimeLineAdapter.ViewHolder>() {

    // Add a filtered list to hold schedules filtered by date
    private var filteredList: ArrayList<ScheduleModel?>? = null

    init {
        filteredList = ArrayList(list!!)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDesignDateTimelineLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return filteredList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = filteredList!![position]
        holder.binding.apply {
            workoutNameTV.text = model!!.name
            detailTV.text = model.category
            dateTV.text = formatDate(model.date)
            clockTV.text = formatTime(model.time)
        }

        if(position == filteredList!!.size-1){
            holder.binding.lineView.visibility = View.INVISIBLE
        }

    }
    fun filterByDate(selectedDate: Date) {
        filteredList?.clear()

        for (schedule in list ?: arrayListOf()) {
            val scheduleDate = Date(schedule?.date ?: 0)
            if (isSameDate(scheduleDate, selectedDate)) {
                filteredList?.add(schedule)
            }
        }

        notifyDataSetChanged()
    }
    private fun isSameDate(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
    inner class ViewHolder(val binding: ItemDesignDateTimelineLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}
