package org.techtown.kormate.Model

import android.os.Parcel
import android.os.Parcelable

data class Comment(

    var id : String = "",

    var userId: Long  = 0,

    var userName: String = "",

    var userImg: String = "",

    var text: String = "",

    var createdTime : String = ""


): Parcelable {

    constructor(parcel: Parcel) : this(

        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()

    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {


        parcel.writeString(id)
        userId?.let { parcel.writeLong(it) }
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



