package com.buzzware.truckerworld.model

import android.os.Parcel
import android.os.Parcelable

data class Posts(
    var postId: String = "",
    var description: String = "",
    var thumbnailImage: String = "",
    var userId: String = "",
    var videoLink: String = "",
    var publishDate: Long = 0,
    var duration: Long = 0,
    var likedBy: HashMap<String, Boolean> = hashMapOf(), // Changed value type to Boolean
    var commentUsers: HashMap<String, Boolean> = hashMapOf(), // Changed value type to Boolean
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readLong(),
        // Corrected the readHashMap calls to readSerializable and casting
        parcel.readSerializable() as HashMap<String, Boolean>,
        parcel.readSerializable() as HashMap<String, Boolean>,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(postId)
        parcel.writeString(description)
        parcel.writeString(thumbnailImage)
        parcel.writeString(userId)
        parcel.writeString(videoLink)
        parcel.writeLong(publishDate)
        parcel.writeLong(publishDate)
        parcel.writeSerializable(likedBy)
        parcel.writeSerializable(commentUsers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Posts> {
        override fun createFromParcel(parcel: Parcel): Posts {
            return Posts(parcel)
        }

        override fun newArray(size: Int): Array<Posts?> {
            return arrayOfNulls(size)
        }
    }

    fun addLike(userId: String) {
        likedBy[userId] = true
    }

    // Remove a user from the likedby list
    fun removeLike(userId: String) {
        likedBy.remove(userId)
    }
}