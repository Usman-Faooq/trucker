package com.buzzware.truckerworld.model

data class GroupModel(
    var groupId: String = "",
    var adminId: String = "",
    var groupName: String = "",
    var friends: MutableMap<String, String>? = null,
    var membersCount: Long = 0
)