package com.buzzware.truckerworld.model

import android.os.Parcel
import android.os.Parcelable

data class GroupModel(

    var docId :String= "",
    var createdDate: Long = 0,
    var groupCategory: String = "",
    var groupName: String = "",
    var status: String = "",
    var userId: String = "",
    var userIamges: String = "",
    var groupLimit:  Long = 0,
    var members: MutableMap<String, String>? = null,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        mutableMapOf<String, String>().apply {
            val size = parcel.readInt()
            repeat(size) {
                val key = parcel.readString() ?: ""
                val value = parcel.readString() ?: ""
                put(key, value)
            }
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(docId)
        parcel.writeLong(createdDate)
        parcel.writeString(groupCategory)
        parcel.writeString(groupName)
        parcel.writeString(status)
        parcel.writeString(userId)
        parcel.writeString(userIamges)
        parcel.writeLong(groupLimit)
        parcel.writeInt(members?.size ?: -1) // Write the size of the map
        members?.let {
            for ((key, value) in it) {
                parcel.writeString(key)
                parcel.writeString(value)
            }
        }
        //parcel.writeSerializable(commentUsers)
    }
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<GroupModel> {
        override fun createFromParcel(parcel: Parcel): GroupModel {
            return GroupModel(parcel)
        }

        override fun newArray(size: Int): Array<GroupModel?> {
            return arrayOfNulls(size)
        }
    }
}