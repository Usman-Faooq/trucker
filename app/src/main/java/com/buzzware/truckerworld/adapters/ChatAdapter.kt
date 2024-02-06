package com.buzzware.truckerworld.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.ChatActivity
import com.buzzware.truckerworld.R
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ItemChatLayoutBinding
import com.buzzware.truckerworld.model.ChatModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class ChatAdapter(val context: Context, val list: ArrayList<ChatModel?>?) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    //private var name = ""
    //private var imageUrl = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChatLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model = list?.get(position)

        val otherUserId = getOtherUserId(model!!)
        FirebaseFirestore.getInstance().collection("Users")
            .document(otherUserId)
            .get().addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                var fName = documentSnapshot.getString("firstName") ?: ""
                var lName = documentSnapshot.getString("lastName") ?: ""
                var name = "$fName $lName"
                var imageUrl = documentSnapshot.getString("imageUrl") ?: ""

                Glide.with(context).load(imageUrl)
                    .placeholder(R.drawable.profile_dummy)
                    .into(holder.binding.imageView9)
                holder.binding.userNameTV.text = name

                holder.binding.root.setOnClickListener {
                    Constants.otherImage = imageUrl
                    Constants.otherName = name
                    var intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra("userId", otherUserId)
                    intent.putExtra("chatId", model.chatId)
                    context.startActivity(intent)
                }
            })

        holder.binding.contentTV.text = model.lastMessage?.get("content").toString()




    }

    inner class ViewHolder(val binding: ItemChatLayoutBinding) : RecyclerView.ViewHolder(binding.root)


    private fun getOtherUserId(chatModel: ChatModel): String {
        val currentUserId = Constants.currentUser.userId ?: ""
        val participants = chatModel.participants

        for (userId in participants?.keys!!) {
            if (userId != currentUserId) {
                return userId
            }
        }

        return ""
    }
}
