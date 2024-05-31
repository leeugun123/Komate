package org.techtown.kormate.presentation.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes


private var toast: Toast? = null

fun Context.showToast(text: String) {
    if (toast != null) toast!!.cancel()
    toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
    toast!!.show()
}

fun Context.showToast(@StringRes textResId: Int) {
    showToast(getString(textResId))
}