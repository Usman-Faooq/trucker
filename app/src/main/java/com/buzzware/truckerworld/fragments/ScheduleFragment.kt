package com.buzzware.truckerworld.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.truckerworld.adapters.DateAdapter
import com.buzzware.truckerworld.adapters.TimeLineAdapter
import com.buzzware.truckerworld.databinding.FragmentScheduleBinding
import com.buzzware.truckerworld.model.DayName
import com.buzzware.truckerworld.model.ScheduleModel
import com.buzzware.truckerworld.utils.getDayNameByDate
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.days

class ScheduleFragment : Fragment() {

    lateinit var binding: FragmentScheduleBinding
    private lateinit var fragmentContext: Context
    private lateinit var dialog: ProgressDialog
    private var scheduleList: ArrayList<ScheduleModel?>? = ArrayList()
    private var dayPosition = 0
    private lateinit var timeLineAdapter: TimeLineAdapter
    private val updatesDayNameList = ArrayList<DayName>()
    private val daysNameList = ArrayList<DayName>()
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleBinding.inflate(layoutInflater)

        dialog = ProgressDialog(requireContext())
        setTimeLineAdapter()
        getSchedules()
        getDaysWithNames()

        layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.HORIZONTAL, false)
        binding.apply {
            val date = Date(System.currentTimeMillis())
            val dateFormat = SimpleDateFormat(" MMM, yyyy", Locale.ENGLISH)
            monthTV.text = dateFormat.format(date)
            monthRV.layoutManager = layoutManager
        }
        return binding.root
    }

    private fun getDaysWithNames() {
        val calender: Calendar = Calendar.getInstance().clone() as Calendar
        val currentDay = calender.get(Calendar.DAY_OF_MONTH)
        val maxDays = calender.getActualMaximum(Calendar.DAY_OF_MONTH)
        val mDay = calender.get(Calendar.DAY_OF_MONTH)

        for (day in 1..maxDays) {
            calender.set(Calendar.DAY_OF_MONTH, day)
            daysNameList.add(
                DayName(
                    dayName = calender.time.getDayNameByDate(),
                    dayNumber = day.toString()
                )
            )
        }
        calender.set(Calendar.DAY_OF_MONTH, mDay)

        if (currentDay > 28) {
            daysNameList[0].isSelected = true
            dayPosition = 0
        } else {
            for (i in 1..daysNameList.size) {
                if (i == currentDay) {
                    daysNameList[i - 1].isSelected = true
                    dayPosition = i - 1
                }
            }
        }
        setDateAdapter(daysNameList)
    }

    private fun onDayNameClicked(position: Int) {
        dayPosition = position
        updatesDayNameList.clear()
        daysNameList.forEach {
            updatesDayNameList.add(
                DayName(
                    dayName = it.dayName,
                    dayNumber = it.dayNumber,
                    id = it.id
                )
            )
        }

        updatesDayNameList.apply {
            forEach { it.isSelected = false }
            get(position).isSelected = true
        }
        val selectedDayNumber = daysNameList[position].dayNumber.toInt()
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        val selectedDate = Calendar.getInstance().apply {
            set(currentYear, currentMonth, selectedDayNumber)
        }.time

        timeLineAdapter.filterByDate(selectedDate)
        setDateAdapter(updatesDayNameList)
    }

    private fun getSchedules() {
        showDialog()
        FirebaseFirestore.getInstance().collection("Schedules")
            .addSnapshotListener { value, error ->
                value?.forEach {
                    val schedule = it.toObject(ScheduleModel::class.java)
                    scheduleList!!.add(schedule)
                }
                setTimeLineAdapter()
                hideDialog()
            }
    }

    private fun setDateAdapter(daysList: ArrayList<DayName>) {
        binding.monthRV.adapter = DateAdapter(fragmentContext, daysList) {
            onDayNameClicked(it)
        }
     /*   binding.apply {
            monthRV.apply {
                val layoutManager = layoutManager as LinearLayoutManager
                val totalVisibleItems =
                    layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition()
                val centeredItemPosition = totalVisibleItems / 2
                smoothScrollToPosition(daysList.indexOfFirst { it.isSelected })
                scrollY = centeredItemPosition
            }
        }*/
    }

    private fun showDialog(msg: String = "Please wait...") {
        dialog.apply {
            setMessage(msg)
            setCancelable(true)
            show()
        }
    }

    private fun hideDialog() {
        dialog.dismiss()
    }

    private fun setTimeLineAdapter() {
        binding.timeLineRV.apply {
            layoutManager =
                LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
        }

        timeLineAdapter = TimeLineAdapter(fragmentContext, scheduleList)
        binding.timeLineRV.adapter = timeLineAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }


}