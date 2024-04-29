package com.bhavesh.ticktask.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.Calendar

object DateTimeUtils {
    fun showDatePicker(context: Context, listener: DatePickerDialog.OnDateSetListener) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            context,
            listener,
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    fun showTimePicker(context: Context, listener: TimePickerDialog.OnTimeSetListener) {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(
            context,
            listener,
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    fun formatTime(hour: Int, minute: Int): String {
        val formattedMinute = if (minute < 10) {
            "0$minute"
        } else {
            "$minute"
        }

        val period = if (hour < 12) "AM" else "PM"
        val formattedHour = when {
            hour == 0 -> 12
            hour <= 12 -> hour
            else -> hour - 12
        }

        return "$formattedHour:$formattedMinute $period"
    }
}
