package org.techtown.kormate.Model

data class Report(

    var user : Long? = null,
    //신고한 유저
    var reason : MutableList<String>? = null,
    //신고 사유
    var reportUser : Long? = null,
    //신고 당한 유저
    var reportId : String? = null
    //신고 당한 게시판 id


)
