package com.buzzware.truckerworld.model

data class ConversationModel(
    var content: String = "",
    var fromId: String = "",
    var isRead: Boolean = false,
    var messageId: String = "",
    var timeStamp: Long = 0,
    var toId: String = "",
    var type: String = ""
)