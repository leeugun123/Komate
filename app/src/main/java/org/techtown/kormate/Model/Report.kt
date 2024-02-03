package org.techtown.kormate.Model

data class Report(

    var user : Long  = 0,
    //신고한 유저
    var reason : MutableList<String> = mutableListOf(),
    //신고 사유
    var reportUser : Long = 0,
    //신고 당한 유저
    var reportId : String = ""
    //신고 당한 게시판 id

)
