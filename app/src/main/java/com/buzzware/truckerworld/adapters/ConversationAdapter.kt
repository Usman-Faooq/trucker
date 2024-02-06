package com.buzzware.truckerworld.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ItemDesignConversationLayoutBinding
import com.buzzware.truckerworld.model.ConversationModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ConversationAdapter(val context: Context, val list: ArrayList<ConversationModel?>?) :
    RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignConversationLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model = list?.get(position)



        if (model?.fromId.equals(Constants.currentUser.userId)){

            holder.binding.leftMessageView.root.visibility = View.GONE
            holder.binding.rightMessageView.contentTV.text = model!!.content
            holder.binding.rightMessageView.timeTV.text = convertFormat(model?.timeStamp.toString())
            holder.binding.rightMessageView.userNameTV.setText("${Constants.currentUser.firstName} ${Constants.currentUser.lastName}")
            Glide.with(context)
                .load(Constants.currentUser.image)
                .placeholder(R.drawable.profile_dummy)
                .error(R.drawable.profile_dummy)
                .into(holder.binding.rightMessageView.imageView10)


        }else{

            holder.binding.rightMessageView.root.visibility = View.GONE
            holder.binding.leftMessageView.contentTV.text = model!!.content
            holder.binding.leftMessageView.timeTV.text = convertFormat(model?.timeStamp.toString())
            holder.binding.leftMessageView.userNameTV.text = Constants.otherName
            Glide.with(context)
                .load(Constants.otherImage)
                .placeholder(R.drawable.profile_dummy)
                .error(R.drawable.profile_dummy)
                .into(holder.binding.leftMessageView.imageView10)

        }

    }

    inner class ViewHolder(val binding: ItemDesignConversationLayoutBinding) : RecyclerView.ViewHolder(binding.root)


    private fun convertFormat(inputDate: String): String {
        val date = Date(inputDate.toLong())
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(date)
    }

    private fun calculateEndTime(deliveryTime: String): Long {
        val parts = deliveryTime.split(" ")
        val value = parts[0].toInt()
        val unit = parts[1].toLowerCase(Locale.getDefault())

        var timeInMillis: Long = 0

        when {
            unit == "day" || unit == "days" -> timeInMillis = value * 24 * 60 * 60 * 1000L
            unit == "hour" || unit == "hours" -> timeInMillis = value * 60 * 60 * 1000L
            unit == "minute" || unit == "minutes" -> timeInMillis = value * 60 * 1000L
        }

        val currentTime = System.currentTimeMillis()
        return currentTime + timeInMillis
    }

}
