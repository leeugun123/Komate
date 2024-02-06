package org.techtown.kormate.Model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BoardDetail(

    var postId : String = "",
    var userId : Long = 0,
    var userName : String = "",
    var userImg : String = "",
    var post : String = "",
    var img: MutableList<String> = mutableListOf(),
    var dateTime: String = ""

): Parcelable {

    constructor(parcel: Parcel) : this(

        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.createStringArrayList()?.toMutableList() as MutableList<String>,
        parcel.readString().toString(),

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {

        parcel.writeString(postId)
        userId?.let { parcel.writeLong(it) }
        parcel.writeString(userName)
        parcel.writeString(userImg)
        parcel.writeString(post)
        parcel.writeStringList(img)
        parcel.writeString(dateTime)

    }

    override fun describeContents() = 0
    companion object CREATOR : Parcelable.Creator<BoardDetail> {
        override fun createFromParcel(parcel: Parcel): BoardDetail {
            return BoardDetail(parcel)
        }

        override fun newArray(size: Int): Array<BoardDetail?> {
            return arrayOfNulls(size)
        }

    }

}

