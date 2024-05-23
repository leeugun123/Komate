package org.techtown.kormate.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Comment(

    @SerializedName("id")
    var id : String = "",

    @SerializedName("userId")
    var userId : Long = 0,

    @SerializedName("userName")
    var userName: String = "",

    @SerializedName("userImg")
    var userImg: String = "",

    @SerializedName("text")
    var text: String = "",

    @SerializedName("createdTime")
    var createdTime : String = ""


) : Parcelable



