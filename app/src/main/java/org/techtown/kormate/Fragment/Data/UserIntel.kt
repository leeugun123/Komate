package org.techtown.kormate.Fragment.Data

import android.os.Parcel
import android.os.Parcelable

data class UserIntel(

    var nation: String? = null,

    var major: String? = null,

    var selfIntro: String? = null,

    var gender: String? = null

) : Parcelable {

    constructor(parcel: Parcel) : this(

        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {

        parcel.writeString(nation)
        parcel.writeString(major)
        parcel.writeString(selfIntro)
        parcel.writeString(gender)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserIntel> {

        override fun createFromParcel(parcel: Parcel): UserIntel {
            return UserIntel(parcel)
        }

        override fun newArray(size: Int): Array<UserIntel?> {
            return arrayOfNulls(size)
        }

    }


}