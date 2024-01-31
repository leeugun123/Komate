package org.techtown.kormate.Util

import java.text.SimpleDateFormat
import java.util.*

 class CurrentDateTime {

     companion object{
         fun getCommentTime() = SimpleDateFormat("MM/dd  HH:mm", Locale.getDefault()).format(Date())!!
         fun getPostTime() = SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault()).format(Date())!!

     }



}