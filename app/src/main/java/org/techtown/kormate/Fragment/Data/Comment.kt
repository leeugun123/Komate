package org.techtown.kormate.Fragment.Data

import android.os.Parcel
import android.os.Parcelable

data class Comment(
    var userName: String? = null,
    var userImg: String? = null,
    var text: String? = null,
    var createdTime: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userName)
        parcel.writeString(userImg)
        parcel.writeString(text)
        parcel.writeString(createdTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}



