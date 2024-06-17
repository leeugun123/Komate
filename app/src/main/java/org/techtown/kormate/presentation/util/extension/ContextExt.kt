package org.techtown.kormate.presentation.util.extension

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
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

fun Context.showAlertDialog(
    title: String,
    positiveButtonText: String,
    positiveButtonAction: () -> Unit,
    negativeButtonText: String,
    negativeButtonAction: (() -> Unit)? = null
) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setPositiveButton(positiveButtonText) { _, _ -> positiveButtonAction() }
        .setNegativeButton(negativeButtonText) { _, _ -> negativeButtonAction?.invoke() }
        .create()
        .show()
}

fun Context.showMultiChoiceDialog(
    title: String,
    items: Array<String>,
    checkedItems: BooleanArray,
    positiveButtonText: String,
    positiveButtonAction: (selectedItems: List<String>) -> Unit,
    negativeButtonText: String,
    negativeButtonAction: (() -> Unit)? = null
) {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
            checkedItems[which] = isChecked
        }
        setPositiveButton(positiveButtonText) { _, _ ->
            val selectedItems = items.indices
                .filter { checkedItems[it] }
                .map { items[it] }

            positiveButtonAction(selectedItems)
        }
        setNegativeButton(negativeButtonText) { dialog, _ ->
            negativeButtonAction?.invoke()
            dialog.dismiss()
        }
    }.create().show()
}