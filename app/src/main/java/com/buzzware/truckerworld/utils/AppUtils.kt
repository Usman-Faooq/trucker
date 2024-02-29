package com.buzzware.truckerworld.utils

import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.buzzware.truckerworld.fragments.ImagePreviewFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

fun convertSecondsToTimeFormat(seconds: Long): String {
    when {
        seconds >= 3600 -> {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60

            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("GMT")
            val timeString = formatter.format(0)  // Initialize with 0 to start from midnight
            val timeArray = timeString.split(":").toTypedArray()

            timeArray[0] = String.format("%02d", hours + timeArray[0].toInt())
            timeArray[1] = String.format("%02d", minutes + timeArray[1].toInt())

            return "${timeArray[0]}:${timeArray[1]} hrs"
        }

        seconds <= 59 -> {
            return if (seconds <= 9) {
                "00:0$seconds sec"
            } else {
                "00:$seconds sec"
            }
        }

        else -> {
            val minutes = seconds / 60
            val seconds = seconds % 60

            val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val timeString = formatter.format(0)  // Initialize with 0 to start from midnight
            val timeArray = timeString.split(":").toTypedArray()


            Log.d(
                "convertMilliseconds",
                "convertMillisecondsToTimeFormat: ${timeArray[0]} : ${timeArray[1]}"
            )
            timeArray[0] = String.format("%02d", minutes + timeArray[0].toInt())
            timeArray[1] = String.format("%02d", seconds + timeArray[1].toInt())

            return "${timeArray[0]}:${timeArray[1]} min"
        }
    }
}

fun formatTime(milliseconds: Long): String {

    val date = Date(milliseconds)
   return if (abs(milliseconds) >= 3600000) {
        val formatter = SimpleDateFormat("HH:mm 'hrs'", Locale.ENGLISH)
        formatter.format(date)
    } else {
        val formatter = SimpleDateFormat("mm 'min'",  Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val timeString = formatter.format(0)  // Initialize with 0 to start from midnight
        val timeArray = timeString.split(":").toTypedArray()
       "${timeArray[0]} min"
    }
}

fun formatDate(milliseconds: Long): String {
    val date = Date(milliseconds)
    val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.ENGLISH)
    return dateFormat.format(date)
}

private fun showDialog(context: Context, msg: String) {
    val dialogs = ProgressDialog(context)
    dialogs.apply {
        setMessage(msg)
        setCancelable(true)
        show()
    }
}
fun Date.formatTo(outPut : String) : String {
    val format = SimpleDateFormat(outPut, Locale.US)
    return format.format(this)
}
fun Date.getDayNameByDate() = formatTo("EEE")
fun Fragment.getColor(@ColorRes color: Int) = ContextCompat.getColor(requireContext(), color)
fun ImagePreviewFragment.show(manager: FragmentManager) {
    this.show(manager, "ImagePreviewFragment")
}


fun View.hideKeyboard() {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(windowToken, 0)
}

fun View.setKeyboardHideOnClickListener() {
    setOnClickListener {
        it.hideKeyboard()
    }
}

fun View?.gone() {
    this?.let { it.visibility = View.GONE }
}

fun View?.visible() {
    this?.let { it.visibility = View.VISIBLE }
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

fun View.isVisible(flag: Boolean) {
    visibility = if (flag) View.VISIBLE else View.GONE
}

fun goneViews(vararg views: View) {
    views.forEach { it.gone() }
}

fun visibleViews(vararg views: View) {
    views.forEach { it.visible() }
}

fun invisibleViews(vararg views: View) {
    views.forEach { it.inVisible() }
}
fun formatTimestampToCustomFormat(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val date = Date(timestamp)
    return dateFormat.format(date)
}