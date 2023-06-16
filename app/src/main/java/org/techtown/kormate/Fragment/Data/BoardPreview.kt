package org.techtown.kormate.Fragment.Data

import android.os.Parcel
import android.os.Parcelable

data class BoardPreview(

    var postId: String? = null,
    var post: String? = null,
    var dateTime: String? = null

): Parcelable {

    constructor(parcel: Parcel) : this(

        parcel.readString(),
        parcel.readString(),
        parcel.readString(),

        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {

        parcel.writeString(postId)
        parcel.writeString(post)
        parcel.writeString(dateTime)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BoardPreview> {
        override fun createFromParcel(parcel: Parcel): BoardPreview {
            return BoardPreview(parcel)
        }

        override fun newArray(size: Int): Array<BoardPreview?> {
            return arrayOfNulls(size)
        }
    }
}

