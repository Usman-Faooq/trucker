package com.buzzware.truckerworld.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.adapters.AddGroupMemberAdapter
import com.buzzware.truckerworld.adapters.DateAdapter
import com.buzzware.truckerworld.adapters.TimeLineAdapter
import com.buzzware.truckerworld.databinding.FragmentScheduleBinding

class ScheduleFragment : Fragment() {

    lateinit var binding : FragmentScheduleBinding
    private lateinit var fragmentContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =FragmentScheduleBinding.inflate(layoutInflater)

        setDateAdapter()
        setTimeLineAdapter()

        return binding.root
    }

    private fun setDateAdapter() {
        binding.monthRV.setLayoutManager(LinearLayoutManager(fragmentContext, LinearLayoutManager.HORIZONTAL, false))
        binding.monthRV.setAdapter(DateAdapter(fragmentContext, emptyList()))
    }

    private fun setTimeLineAdapter() {
        binding.timeLineRV.setLayoutManager(LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false))
        binding.timeLineRV.setAdapter(TimeLineAdapter(fragmentContext, emptyList()))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }


}