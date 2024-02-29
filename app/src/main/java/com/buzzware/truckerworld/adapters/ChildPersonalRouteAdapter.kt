package com.buzzware.truckerworld.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.databinding.ItemChildPersonalRouteBinding
import com.buzzware.truckerworld.model.User

class ChildPersonalRouteAdapter(private var memberList: List<User>, val context: Context) :
    RecyclerView.Adapter<ChildPersonalRouteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChildPersonalRouteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return 6
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       /* val model = memberList[position]
        holder.bind(model)*/
    }

    inner class ViewHolder(val binding: ItemChildPersonalRouteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                Glide.with(context).load(user.image).into(childItemIV)
            }
        }

    }


}