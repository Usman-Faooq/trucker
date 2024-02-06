package com.buzzware.truckerworld.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.databinding.FragmentCreateScheduleBinding
import java.util.*

class CreateScheduleFragment : Fragment() {

    lateinit var binding : FragmentCreateScheduleBinding
    private lateinit var fragmentContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateScheduleBinding.inflate(layoutInflater)

        setView()
        setListener()


        return binding.root
    }


    private fun setView() {

        val type = arrayOf("Value 01", "Value 02", "Value 03", "Value 04", "Value 05")
        val array = ArrayAdapter<String>(fragmentContext, R.layout.dropdown_layout, type)
        binding.selectSportPicker.setAdapter(array)


    }

    private fun setListener() {

        binding.datePickerET.setOnClickListener { view ->
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePickerDialog =
                DatePickerDialog(requireContext(), { view, year, month, dayOfMonth ->
                        binding.datePickerET.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year))
                    }, year, month, day)
            datePickerDialog.show()
        }

        //Time Picker
        binding.timePickerLayout.setOnClickListener { view ->
            val calendar = Calendar.getInstance()
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            val timePickerDialog = TimePickerDialog(requireContext(),
                OnTimeSetListener { view, hourOfDay, minute ->
                    var selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    binding.timePickerET.setText(selectedTime)
                }, hour, minute, true
            )
            timePickerDialog.show()
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}