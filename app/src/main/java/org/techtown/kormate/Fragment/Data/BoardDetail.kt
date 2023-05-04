package org.techtown.kormate.Fragment.Data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BoardDetail(

    var postId: String? = null,
    var userId: Long? = null,
    var userName: String? = null,
    var userImg: String? = null,
    var post: String? = null,
    var img: MutableList<String> = mutableListOf(),
    var dateTime: String? = null,




): Parcelable {

    constructor(parcel: Parcel) : this(

        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()?.toMutableList() as MutableList<String>,
        parcel.readString(),



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

