package com.buzzware.truckerworld.model


data class NotificationModel(
    val content: String="=",
    val fromId: String="",
    val notificationTime: Long =0,
    val toId: String="",
    val type: String="",
    var extradata: MutableMap<String, Any>? = hashMapOf(),
){

}