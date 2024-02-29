package com.buzzware.truckerworld.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.databinding.ItemDesignDateTimelineLayoutBinding
import com.buzzware.truckerworld.databinding.ItemGuideLineTextBinding
import com.buzzware.truckerworld.model.GuideLineModel

class GuideLineAdapter(var list:ArrayList<GuideLineModel>): RecyclerView.Adapter<GuideLineAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemGuideLineTextBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGuideLineTextBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list.get(position)
        holder.binding.apply {
            titleTV.text = model.title
            detailstV.text = model.details

            if(position == list.size-1){
                lineView.visibility = View.INVISIBLE
            }
        }
    }
}