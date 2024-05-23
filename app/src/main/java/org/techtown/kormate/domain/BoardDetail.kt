package org.techtown.kormate.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BoardDetail(

    @SerializedName("postId")
    var postId : String = "",

    @SerializedName("userId")
    var userId : Long = 0,

    @SerializedName("userName")
    var userName : String = "",

    @SerializedName("userImg")
    var userImg : String = "",

    @SerializedName("post")
    var post : String = "",

    @SerializedName("img")
    var img: MutableList<String> = mutableListOf(),

    @SerializedName("dateTime")
    var dateTime: String = ""

): Parcelable