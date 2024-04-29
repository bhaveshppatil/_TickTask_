package com.bhavesh.ticktask.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bhavesh.ticktask.notification.NotificationBroadcast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object AlarmUtils {

    fun setAlarm(context: Context?, title: String, decs: String, date: String, time: String) {
        if (context == null) {
            Log.e("AlarmUtils", "Context is null. Cannot set alarm.")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        if (alarmManager == null) {
            Log.e("AlarmUtils", "Failed to obtain AlarmManager.")
            return
        }

        val intent = Intent(context, NotificationBroadcast::class.java).apply {
            putExtra("title", title)
            putExtra("decs", decs)
            putExtra("date", date)
            putExtra("time", time)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val datetimeString = "$date $time"
        val calendar = Calendar.getInstance()
        try {
            val formatter = SimpleDateFormat(DateConstant.DATE_FORMAT_YYYY, Locale.getDefault())
            val dateObj = formatter.parse(datetimeString)
            if (dateObj != null) {
                calendar.time = dateObj
                val alarmTimeMillis = calendar.timeInMillis
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent)
            } else {
                Log.e("AlarmUtils", "Failed to parse date: $datetimeString")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("AlarmUtils", "Error while setting alarm: ${e.message}")
        }
    }
}
