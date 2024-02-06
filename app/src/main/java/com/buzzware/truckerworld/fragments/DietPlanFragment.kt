package com.buzzware.truckerworld.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.buzzware.truckerworld.adapters.DietAdapter
import com.buzzware.truckerworld.databinding.FragmentDietPlanBinding


class DietPlanFragment : Fragment(), DietAdapter.OnItemClickListener {

    lateinit var binding : FragmentDietPlanBinding
    private lateinit var fragmentContext: Context

    interface OnDataChangeListener {
        fun onItemDataChanged(data: String, type: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDietPlanBinding.inflate(layoutInflater)

        setAdatper()

        return binding.root
    }

    private fun setAdatper() {
        binding.recyclerView.layoutManager = GridLayoutManager(fragmentContext, 2)
        binding.recyclerView.adapter = DietAdapter(fragmentContext, emptyList(), this)
    }

    override fun onItemClick(itemName: String, type: String) {

        val activity = activity as? OnDataChangeListener
        activity?.onItemDataChanged("Diet Plan $type", type)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}