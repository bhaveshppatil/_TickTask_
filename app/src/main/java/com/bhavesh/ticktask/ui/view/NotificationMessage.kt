package com.bhavesh.ticktask.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bhavesh.ticktask.R
import com.bhavesh.ticktask.databinding.ActivityNotificationMessageBinding

class NotificationMessage : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_message)

        val bundle = intent.extras
        binding.tvMessage.text = bundle!!.getString("message")
    }
}