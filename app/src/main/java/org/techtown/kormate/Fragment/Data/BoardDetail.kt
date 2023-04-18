package org.techtown.kormate.Fragment.Data

import android.os.Parcel
import android.os.Parcelable


data class BoardDetail(
    var userName: String? = null,
    var userImg: String? = null,
    var post: String? = null,
    var img: String? = null,
    var date: String? = null,
    var time: String? = null,
    var comments: MutableList<Comment> = mutableListOf()
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        mutableListOf<Comment>().apply {
            parcel.readList(this, Comment::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userName)
        parcel.writeString(userImg)
        parcel.writeString(post)
        parcel.writeString(img)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeList(comments)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BoardDetail> {
        override fun createFromParcel(parcel: Parcel): BoardDetail {
            return BoardDetail(parcel)
        }

        override fun newArray(size: Int): Array<BoardDetail?> {
            return arrayOfNulls(size)
        }
    }
}



