package com.buzzware.truckerworld.model

import android.os.Parcel
import android.os.Parcelable

data class DietPlanModel(
    val createdDate: String = "",
    val id: Long = 0,
    val name: String = "",
    val time: Long = 0,
    val plan : List<String> = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?: "",
        parcel.readLong(),
        parcel.readString()?: "",
        parcel.readLong(),
        parcel.createStringArrayList() ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdDate)
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeLong(time)
        parcel.writeStringList(plan)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DietPlanModel> {
        override fun createFromParcel(parcel: Parcel): DietPlanModel {
            return DietPlanModel(parcel)
        }

        override fun newArray(size: Int): Array<DietPlanModel?> {
            return arrayOfNulls(size)
        }
    }

}
