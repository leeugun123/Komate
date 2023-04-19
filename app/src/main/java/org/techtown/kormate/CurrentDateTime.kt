package org.techtown.kormate

import java.text.SimpleDateFormat
import java.util.*

 class CurrentDateTime {

     companion object{

         fun getCommentTime(): String {
             val dateFormat = SimpleDateFormat("MM/dd  HH:mm", Locale.getDefault())
             val date = Date()
             return dateFormat.format(date)
         }

         fun getPostTime(): String {
             val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
             val date = Date()
             return dateFormat.format(date)
         }

     }



}