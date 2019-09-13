package com.trendyol.medusalib.navigator.data

import android.os.Parcel
import android.os.Parcelable

data class StackItem(val fragmentTag: String, val groupName: String = "") : Parcelable {
    constructor(parcel: Parcel) : this(
        requireNotNull(parcel.readString()),
        requireNotNull(parcel.readString())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fragmentTag)
        parcel.writeString(groupName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StackItem> {
        override fun createFromParcel(parcel: Parcel): StackItem {
            return StackItem(parcel)
        }

        override fun newArray(size: Int): Array<StackItem?> {
            return arrayOfNulls(size)
        }
    }

}