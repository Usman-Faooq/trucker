package com.buzzware.truckerworld.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.adapters.DietAdapter
import com.buzzware.truckerworld.adapters.DietPlanDetailsAdpater
import com.buzzware.truckerworld.databinding.FragmentDietPlanDetailBinding
import com.buzzware.truckerworld.model.DietPlanModel
import com.google.firebase.firestore.FirebaseFirestore


class DietPlanDetailFragment(var type : String, var dietPlan:DietPlanModel) : Fragment() {

    lateinit var binding : FragmentDietPlanDetailBinding
    private lateinit var fragmentContext: Context
    private var dietPlanList : ArrayList<String> = ArrayList()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDietPlanDetailBinding.inflate(layoutInflater)

        dietPlan.plan.forEach {
            dietPlanList.add(it)
            setAdatper()
        }
        return binding.root
    }

    private fun setAdatper() {
        binding.dietPlanRV.layoutManager = LinearLayoutManager(fragmentContext)
        binding.dietPlanRV.adapter = DietPlanDetailsAdpater(dietPlanList!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}