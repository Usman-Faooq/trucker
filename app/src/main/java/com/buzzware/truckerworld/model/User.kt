package com.buzzware.truckerworld.model

data class User(
    var userId: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var username: String = "",
    var image: String = "",
    var email: String = "",
    var password: String = "",
    var phoneNumber: String = "",
    var userRole: String = "",
    var address: String = "",
    var isOnline: Boolean = false,
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var friendsList: MutableMap<String, String>? = null,
    var groups: MutableMap<String, String>? = null
){
    fun addFriendRequest(friendUserId: String) {
        if (friendsList == null) {
            friendsList = mutableMapOf()
        }
        friendsList?.put(friendUserId, "requested")
    }

}