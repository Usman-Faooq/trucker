package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.OtherUserProfileActivity
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.databinding.ItemDesignAddGroupMemberLayoutBinding
import com.buzzware.truckerworld.model.User
import java.util.ArrayList

class OnlineMemberAdapter(val context: Context, val list: ArrayList<User?>?) :
    RecyclerView.Adapter<OnlineMemberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignAddGroupMemberLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.addBtn.visibility = View.INVISIBLE

        var model = list?.get(position)

        Glide.with(context).load(model?.image)
            .error(R.drawable.profile_dummy)
            .placeholder(R.drawable.profile_dummy)
            .into(holder.binding.profileIV)
        holder.binding.userNameTV.setText("${model?.firstName} ${model?.lastName}")


        holder.binding.root.setOnClickListener {
            val intent = Intent(context, OtherUserProfileActivity::class.java)
            intent.putExtra("userId", model?.userId)
            context.startActivity(intent)
        }


    }

    inner class ViewHolder(val binding: ItemDesignAddGroupMemberLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
