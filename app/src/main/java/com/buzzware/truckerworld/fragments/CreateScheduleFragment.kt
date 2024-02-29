package com.buzzware.truckerworld.fragments

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentCreateScheduleBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CreateScheduleFragment : Fragment() {

    lateinit var binding: FragmentCreateScheduleBinding
    private lateinit var fragmentContext: Context
    private lateinit var dialog: ProgressDialog
    private var timeInMillis: Long = 0
    private var dateInMillis: Long = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateScheduleBinding.inflate(layoutInflater)

        dialog = ProgressDialog(requireContext())
        setView()
        setListener()


        return binding.root
    }


    private fun setView() {


        val type = arrayOf("Cardio", "Full Body")
        val array = ArrayAdapter(fragmentContext, R.layout.dropdown_layout, type)
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
                    binding.datePickerET.setText(
                        String.format(
                            "%02d/%02d/%04d",
                            dayOfMonth,
                            month + 1,
                            year
                        )
                    )

                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        set(Calendar.MONTH, month+1)
                        set(Calendar.YEAR, year)
                    }

                    dateInMillis = calendar.timeInMillis
                }, year, month, day)
            datePickerDialog.show()
        }

        //Time Picker
        binding.timePickerET.setOnClickListener { view ->
            val calendar = Calendar.getInstance()
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    timeInMillis = calendar.timeInMillis
                    Log.d("timeInMillis", "setListener: $timeInMillis")
                    var selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    binding.timePickerET.setText(selectedTime)
                }, hour, minute, true
            )
            timePickerDialog.show()
        }

        binding.apply {
            saveTV.setOnClickListener {
                showDialog("please wait...")
                createSchedule()
            }
        }

    }
    private fun createSchedule() {

        binding.apply {

            val schedule = hashMapOf(
                "category" to selectSportPicker.text.toString(),
                "createdAt" to System.currentTimeMillis(),
                "date" to dateInMillis,
                "name" to sceduleNameTV.text.toString(),
                "userId" to Constants.currentUser.userId,
                "time" to timeInMillis
            )
            FirebaseFirestore.getInstance().collection("Schedules")
                .document(UUID.randomUUID().toString())
                .set(schedule)
                .addOnSuccessListener {
                    hideDialog()
                    Toast.makeText(requireContext(), "Successfully created", Toast.LENGTH_SHORT)
                        .show()
                }.addOnFailureListener { error ->
                    Toast.makeText(requireContext(), error.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun showDialog(msg: String) {
        dialog.apply {
            setMessage(msg)
            setCancelable(true)
            show()
        }
    }

    private fun hideDialog() {
        dialog.dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}