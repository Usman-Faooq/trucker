package com.buzzware.truckerworld.model

import android.os.Parcel
import android.os.Parcelable

data class Comments(
    var commentId: String = "",
    var content: String = "",
    var fromId: String = "",
    var timeStamp: Long = 0,
    var toId: String = "",
    var type: String = "",
    var likedBy: ArrayList<String>? = null,
    var dislikedBy: ArrayList<String>? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList()?.let { ArrayList(it) },
        parcel.createStringArrayList()?.let { ArrayList(it) }
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(commentId)
        parcel.writeString(content)
        parcel.writeString(fromId)
        parcel.writeLong(timeStamp)
        parcel.writeString(toId)
        parcel.writeString(type)
        parcel.writeStringList(likedBy)
        parcel.writeStringList(dislikedBy)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comments> {
        override fun createFromParcel(parcel: Parcel): Comments {
            return Comments(parcel)
        }

        override fun newArray(size: Int): Array<Comments?> {
            return arrayOfNulls(size)
        }
    }

    fun addLike(userId: String) {
        if (likedBy == null) {
            likedBy = ArrayList()
        }
        likedBy?.add(userId)
    }

    // Remove a user from the likedby list
    fun removeLike(userId: String) {
        likedBy?.remove(userId)
    }

    fun addDislike(userId: String) {
        if (dislikedBy == null) {
            dislikedBy = ArrayList()
        }
        dislikedBy?.add(userId)
    }

    // Remove a user from the likedby list
    fun removeDislike(userId: String) {
        dislikedBy?.remove(userId)
    }

}