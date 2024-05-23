package org.techtown.kormate.presentation.util

import java.text.SimpleDateFormat
import java.util.*

 class CurrentDateTime {

     companion object{
         fun getCommentTime(): String = SimpleDateFormat("MM/dd  HH:mm", Locale.getDefault()).format(Date())
         fun getPostTime() : String= SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault()).format(Date())

     }



}