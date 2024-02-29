package com.buzzware.truckerworld.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.truckerworld.databinding.ItemDietPlanDetailsBinding

class DietPlanDetailsAdpater(var list:ArrayList<String>): RecyclerView.Adapter<DietPlanDetailsAdpater.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDietPlanDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemDietPlanDetailsBinding.inflate(
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
        holder.binding.dietDay1.text = model

    }

}