package com.buzzware.truckerworld.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.adapters.ChatAdapter
import com.buzzware.truckerworld.adapters.PersonalRouteAdapter
import com.buzzware.truckerworld.databinding.ActivityDashBoardBinding
import com.buzzware.truckerworld.databinding.FragmentPersonalRouteBinding

class PersonalRouteFragment : Fragment(), PersonalRouteAdapter.OnItemClickListener{

    lateinit var binding : FragmentPersonalRouteBinding
    private lateinit var fragmentContext: Context

    interface OnDataChangeListener {
        fun onDataChanged(data: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentPersonalRouteBinding.inflate(layoutInflater)

        setView()
        setListener()
        setAdapter()

        return binding.root
    }


    private fun setView() {

    }

    private fun setListener() {

    }

    private fun setAdapter() {
        binding.recyclerView.setLayoutManager(LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false))
        binding.recyclerView.setAdapter(PersonalRouteAdapter(fragmentContext, emptyList(), this))
    }

    override fun onDateClick(itemName: String, type: String) {
        val activity = activity as? OnDataChangeListener
        activity?.onDataChanged("Group Name")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}