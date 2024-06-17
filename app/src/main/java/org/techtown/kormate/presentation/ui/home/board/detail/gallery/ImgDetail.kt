package org.techtown.kormate.presentation.ui.home.board.detail.gallery

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImgDetail(

    @SerializedName("entirePage")
    var entirePage: Int = 0,

    @SerializedName("curPage")
    var curPage: Int = 0,

    @SerializedName("imgUri")
    var imgUri: String = "",

    ) : Parcelable