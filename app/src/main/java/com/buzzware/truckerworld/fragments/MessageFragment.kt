package com.buzzware.truckerworld.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.truckerworld.adapters.ChatAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.FragmentMessageBinding
import com.buzzware.truckerworld.model.ChatModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class MessageFragment : Fragment() {

    lateinit var binding : FragmentMessageBinding
    private lateinit var fragmentContext: Context
    private var chatList: ArrayList<ChatModel?>? = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageBinding.inflate(layoutInflater)


        getSingleChat()
        setView()
        setListener()


        return binding.root
    }


    private fun getSingleChat() {
        FirebaseFirestore.getInstance().collection("Chat")
            .whereEqualTo("chatType", "one")
            .whereEqualTo("participants.${Constants.currentUser.userId}", true)
            .addSnapshotListener { queryDocumentSnapshots, e ->
                if (e != null) {
                    // Handle the failure here
                    return@addSnapshotListener
                }

                chatList?.clear()

                for (documentSnapshot in queryDocumentSnapshots!!) {
                    val chatId = documentSnapshot.id
                    val model = documentSnapshot.toObject(ChatModel::class.java)
                    model.chatId = chatId
                    chatList?.add(model)
                }

                setAdapter()
            }
    }

    private fun setView() {


    }

    private fun setListener() {

    }

    private fun setAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = ChatAdapter(fragmentContext, chatList)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}