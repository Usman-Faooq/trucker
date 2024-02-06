package com.buzzware.truckerworld.model

data class ChatModel(
    var chatId: String = "",
    var chatType: String = "",
    var lastMessage: MutableMap<String, Any>? = null,
    var participants: MutableMap<String, Any>? = null
)