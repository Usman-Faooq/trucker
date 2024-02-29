package com.buzzware.truckerworld.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.adapters.DietPlanDetailsAdpater
import com.buzzware.truckerworld.adapters.GuideLineAdapter
import com.buzzware.truckerworld.databinding.FragmentGuidelinesBinding
import com.buzzware.truckerworld.model.GuideLineModel

class GuidelinesFragment : Fragment() {

    private lateinit var binding: FragmentGuidelinesBinding
    private lateinit var fragmentContext: Context
    private var guideLineList : ArrayList<GuideLineModel> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGuidelinesBinding.inflate(layoutInflater)

        guideLineList.apply {
            add(GuideLineModel(title ="Empathy", details = requireActivity().getString(R.string.empathy_text_guide_line) ))
            add(GuideLineModel(title ="Resources", details = requireActivity().getString(R.string.resources_text_guide_line) ))
            add(GuideLineModel(title ="Communicate respectfully", details = requireActivity().getString(R.string.communicate_respectfully_text_guide_line) ))
            add(GuideLineModel(title ="Be Supportive", details = requireActivity().getString(R.string.be_supportive_text_guide_line) ))
            add(GuideLineModel(title ="Keep an Open mind", details = requireActivity().getString(R.string.open_mind_text_guide_line) ))
        }

        binding.apply {
            guideLineDetails.layoutManager = LinearLayoutManager(fragmentContext,LinearLayoutManager.VERTICAL,false)
            guideLineDetails.adapter = GuideLineAdapter(guideLineList)

        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}