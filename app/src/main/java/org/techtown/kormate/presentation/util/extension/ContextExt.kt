package org.techtown.kormate.presentation.util.extension

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private var toast: Toast? = null

fun Context.showToast(text: String) {
    if (toast != null) toast!!.cancel()
    toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
    toast!!.show()
}

fun Context.showToast(@StringRes textResId: Int) {
    showToast(getString(textResId))
}

fun Context.getCurrentTime(): String = SimpleDateFormat("MM/dd  HH:mm", Locale.getDefault()).format(Date())

fun Context.getPostTime() : String = SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault()).format(Date())