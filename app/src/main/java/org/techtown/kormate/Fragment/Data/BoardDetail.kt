package org.techtown.kormate.Fragment.Data

import android.net.Uri
import java.io.Serializable

data class BoardDetail(

    var userName: String? = null,
    var userImg: String? = null,
    var post: String? = null,
    var img: String? = null,
    var date: String? = null,
    var time: String? = null,
    var comments: MutableList<Comment> = mutableListOf()

                      ) : Serializable


