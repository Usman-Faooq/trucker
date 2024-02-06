package com.buzzware.truckerworld.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.adapters.MainLayoutAdapter
import com.buzzware.truckerworld.databinding.FragmentSearchhBinding

class SearchhFragment : Fragment() {

    lateinit var binding : FragmentSearchhBinding
    lateinit var adapter : MainLayoutAdapter
    private lateinit var fragmentContext: Context


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSearchhBinding.inflate(layoutInflater)

        setView()
        setListener()
        setAdapter()

        return binding.root

    }

    private fun setAdapter() {
        val dataList = listOf("Arm Muscles Workout", "Body Workout", "Arm Workout", "Arm Muscles Workout", "Arm Muscles Workout", "Body Workout", "Arm Muscles Workout")
        adapter = MainLayoutAdapter(fragmentContext, dataList)
        binding.recyclerView.setLayoutManager(LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false))
        binding.recyclerView.setAdapter(adapter)
    }

    private fun setView() {

        binding.searchTV.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //adapter.filterItems(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

    }

    private fun setListener() {

        binding.postSendIV.setOnClickListener {
            performSearch()
        }
        binding.searchTV.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

    }

    private fun performSearch() {
        val query = binding.searchTV.text.toString().trim()
        adapter.filterItems(query)
        // Hide the keyboard after performing the search
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.searchTV.windowToken, 0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}