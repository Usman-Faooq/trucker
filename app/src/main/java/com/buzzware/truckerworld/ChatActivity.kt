package com.buzzware.truckerworld

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzware.truckerworld.adapters.ConversationAdapter
import com.buzzware.truckerworld.classes.Constants
import com.buzzware.truckerworld.databinding.ActivityChatBinding
import com.buzzware.truckerworld.model.ConversationModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class ChatActivity : AppCompatActivity() {

    lateinit var binding : ActivityChatBinding
    lateinit var messageAdapter: ConversationAdapter

    var userId : String = ""
    var chatId : String = ""

    private var conversationModelList: ArrayList<ConversationModel?>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId")!!
        chatId = intent.getStringExtra("chatId")!!


        if (chatId.isEmpty()) {
            FirebaseFirestore.getInstance().collection("Chat")
                .get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    for (document in queryDocumentSnapshots) {
                        val participantMap = document.get("participants") as Map<String, Any>?

                        val hasCurrentUser = participantMap?.containsKey(Constants.currentUser.userId) == true
                        val hasOpponent = participantMap?.containsKey(userId) == true

                        if (hasCurrentUser && hasOpponent) {
                            val documentId = document.id
                            chatId = documentId
                            Constants.chatId = chatId
                            Log.d("LOGGER", "ID: $documentId")
                            break // Exit loop after finding the document
                        }
                    }
                    addRealtimeListener()
                }
                .addOnFailureListener { }
        } else {
            addRealtimeListener()
        }

        setView()
        setListener()

    }

    private fun addRealtimeListener() {
        if (!chatId.isNullOrEmpty()) {
            FirebaseFirestore.getInstance().collection("Chat")
                .document(chatId).collection("Conversation")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { queryDocumentSnapshots, e ->
                    if (e != null) {
                        // Handle the error
                        return@addSnapshotListener
                    }

                    conversationModelList?.clear()
                    for (documentSnapshot in queryDocumentSnapshots ?: emptyList()) {
                        val model = documentSnapshot.toObject(ConversationModel::class.java)
                        conversationModelList?.add(model)
                    }
                    setConversationAdapter()
                }
        }
    }

    private fun setConversationAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        binding.messagesList.layoutManager = layoutManager
        messageAdapter = ConversationAdapter(this, conversationModelList)
        binding.messagesList.adapter = messageAdapter

    }

    private fun setView() {
        binding.userNamwTV.text = Constants.otherName
        Glide.with(this).load(Constants.otherImage)
            .error(R.drawable.profile_dummy)
            .placeholder(R.drawable.profile_dummy)
            .into(binding.profileIV)

    }

    private fun setListener() {

        binding.backIV.setOnClickListener {
            finish()
        }

        binding.sendIV.setOnClickListener {
            if (!binding.messageET.text.isEmpty()){
                sendMessage(binding.messageET.text.toString())
            }
        }

    }

    private fun sendMessage(message: String) {
        val messageId = UUID.randomUUID().toString()
        val timeStamp = System.currentTimeMillis()
        if (chatId.isEmpty()) {
            chatId = UUID.randomUUID().toString()
        }

        val messageMap = hashMapOf(
            "content" to message,
            "fromId" to Constants.currentUser.userId,
            "toId" to userId,
            "messageId" to messageId,
            "read" to false,
            "timeStamp" to timeStamp,
            "type" to "text"
        )

        val participents = hashMapOf(
            Constants.currentUser.userId to true,
            userId to true
        )


        val lastMessageMap = hashMapOf(
            "lastMessage" to messageMap,
            "participants" to participents,
            "chatType" to "one"
        )

        FirebaseFirestore.getInstance().collection("Chat").document(chatId).set(lastMessageMap)
        FirebaseFirestore.getInstance().collection("Chat").document(chatId)
            .collection("Conversation").document(messageId).set(messageMap)
        binding.messageET.setText("")

    }
}