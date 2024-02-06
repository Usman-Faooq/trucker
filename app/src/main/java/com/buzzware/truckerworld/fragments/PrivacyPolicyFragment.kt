package com.buzzware.truckerworld.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.databinding.FragmentPrivacyPolicyBinding

class PrivacyPolicyFragment : Fragment() {

    lateinit var binding : FragmentPrivacyPolicyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrivacyPolicyBinding.inflate(layoutInflater)

        return binding.root
    }



}