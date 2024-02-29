package com.buzzware.truckerworld.model

import android.os.Parcel
import android.os.Parcelable

data class ScheduleModel(
    val category: String = "",
    val createdAt: Long = 0,
    val date: Long = 0,
    val time: Long = 0,
    val name: String = "",
    val userId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeLong(createdAt)
        parcel.writeLong(date)
        parcel.writeLong(time)
        parcel.writeString(name)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduleModel> {
        override fun createFromParcel(parcel: Parcel): ScheduleModel {
            return ScheduleModel(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleModel?> {
            return arrayOfNulls(size)
        }
    }
}
