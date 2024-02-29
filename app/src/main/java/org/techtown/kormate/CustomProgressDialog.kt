package org.techtown.kormate

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View

class CustomProgressDialog(context: Context) : Dialog(context) {

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view: View = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null)
        setContentView(view)
    }
}