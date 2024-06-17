package org.techtown.kormate.domain.model

data class Report(

    var user : String = "",
    //신고한 유저
    var reason : List<String> = mutableListOf(),
    //신고 사유
    var reportUser : String = "",
    //신고 당한 유저
    var reportId : String = ""
    //신고 당한 게시판 id

)
