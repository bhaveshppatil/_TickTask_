package com.bhavesh.ticktask.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bhavesh.ticktask.R
import com.bhavesh.ticktask.ui.view.NotificationMessage


class NotificationBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        val text = bundle?.getString("title")
        val date = bundle?.getString("date")
        val time = bundle?.getString("time")
        val decs = bundle?.getString("decs")

        //Click on Notification
        val intent1 = Intent(context, NotificationMessage::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent1.putExtra("message", text)

        //Notification Builder
        val pendingIntent =
            PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_ONE_SHOT)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, "TickTask_01")

        val remoteViews = RemoteViews(context.packageName, R.layout.notification_layout)

        remoteViews.apply {
            setImageViewResource(R.id.ivNotify, R.mipmap.ic_launcher)
            setTextViewText(R.id.tvNotifyTitle, text?.uppercase())
            setTextViewText(R.id.tvNotifyDate, date)
            setTextViewText(R.id.tvNotifyTime, time)
            setTextViewText(R.id.tvNotifyDecs, decs)
        }
        builder.apply {
            setSmallIcon(R.drawable.alaram)
            setAutoCancel(false)
            setOngoing(true)
            setAutoCancel(true)
            priority = Notification.PRIORITY_HIGH
            setOnlyAlertOnce(true)
            build().flags = Notification.FLAG_NO_CLEAR or Notification.PRIORITY_HIGH
            setContent(remoteViews)
            setContentIntent(pendingIntent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "TickTask_01"
            val notificationChannel =
                NotificationChannel(channelId, "TickTask", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
            builder.setChannelId(channelId)
        }
        val notification = builder.build()
        notificationManager.notify(1, notification)
    }
}