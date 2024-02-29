package com.buzzware.truckerworld.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.buzzware.truckerworld.adapters.DietAdapter
import com.buzzware.truckerworld.databinding.FragmentDietPlanBinding
import com.buzzware.truckerworld.model.DietPlanModel
import com.buzzware.truckerworld.utils.show
import com.google.firebase.firestore.FirebaseFirestore


class DietPlanFragment : Fragment(), DietAdapter.OnItemClickListener {

    lateinit var binding: FragmentDietPlanBinding
    private lateinit var fragmentContext: Context
    private lateinit var mDialog: ProgressDialog
    private lateinit var previewDialog: ImagePreviewFragment
    private var dietPlanList: ArrayList<DietPlanModel?>? = ArrayList()

    interface OnDataChangeListener {
        fun onItemDataChanged(data: String, type: String, model: DietPlanModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDietPlanBinding.inflate(layoutInflater)
        mDialog = ProgressDialog(requireContext())
        setAdatper()
        getDietPlans()
        binding.apply {
            liveFitThreeIV.setOnClickListener {
                previewDialog = ImagePreviewFragment("third")
                previewDialog.show(childFragmentManager)
            }
        }
        return binding.root
    }

    private fun getDietPlans() {
        showDialog()
        FirebaseFirestore.getInstance().collection("dietplan")
            .addSnapshotListener { value, error ->
                dietPlanList!!.clear()
                if (value != null && !value.isEmpty) {
                    value.forEach {
                        val model = it.toObject(DietPlanModel::class.java)
                        dietPlanList!!.add(model)
                    }
                    hideDialog()
                    Log.d("dietPlanList", "getDietPlans: ${dietPlanList!!.size}")
                    setAdatper()
                }
            }
    }

    private fun setAdatper() {
        binding.recyclerView.layoutManager = GridLayoutManager(fragmentContext, 2)
        binding.recyclerView.adapter = DietAdapter(fragmentContext, dietPlanList!!, this)
    }

    override fun onItemClick(itemName: String, type: String, model: DietPlanModel) {

        val activity = activity as? OnDataChangeListener
        activity?.onItemDataChanged("Diet Plan $type", type, model)

    }


    private fun showDialog(msg: String = "Please wait...") {
        mDialog.apply {
            setMessage(msg)
            setCancelable(true)
            show()
        }
    }

    private fun hideDialog() {
        mDialog.dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}