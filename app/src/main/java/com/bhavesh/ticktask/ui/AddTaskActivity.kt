package com.bhavesh.ticktask.ui

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bhavesh.ticktask.R
import com.bhavesh.ticktask.data.model.RoutineModel
import com.bhavesh.ticktask.data.repository.RoutineRepository
import com.bhavesh.ticktask.data.roomDB.RoutineDAO
import com.bhavesh.ticktask.data.roomDB.RoutineRoomDB
import com.bhavesh.ticktask.notification.NotificationBroadcast
import com.bhavesh.ticktask.viewModel.RoutineViewModel
import com.bhavesh.ticktask.viewModel.RoutineViewModelFactory
import kotlinx.android.synthetic.main.fragment_add_task.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {
    private lateinit var routineRoomDB: RoutineRoomDB
    private lateinit var routineDAO: RoutineDAO
    private lateinit var routineViewModel: RoutineViewModel
    var timeNotify: String = ""
    var message: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        var intent = Intent()
        intent = getIntent()
        message = intent.getStringExtra("Edit").toString()

        routineDAO = this.let { RoutineRoomDB.getDatabaseObject(it).getRoutineDAO() }
        val routineRepository = RoutineRepository(routineDAO)
        val routineViewModelFactory = RoutineViewModelFactory(routineRepository)
        routineViewModel =
            ViewModelProviders.of(this, routineViewModelFactory).get(RoutineViewModel::class.java)

        ivSelectTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            val timePickerDialog = TimePickerDialog(
                this,
                { timePicker, i, i1 ->
                    timeNotify = "$i:$i1"
                    tvTime.text = FormatTime(i, i1).toString()
                }, hour, minute, false
            )
            timePickerDialog.show()
        }

        ivSelectDate.setOnClickListener(View.OnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this,
                { datePicker, year, month, day ->
                    tvDate.text = day.toString() + "-" + (month + 1) + "-" + year
                }, year, month, day
            )
            datePickerDialog.show()
        })

        btnTaskDone.setOnClickListener {

            val title = etRoutine.text.toString()
            val decs = etDecs.text.toString()
            val date = tvDate.text.toString()
            val time = tvTime.text.toString()

            if (title.isNotEmpty() && decs.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                val routineModel = RoutineModel(title, decs, date, time, "High")
                routineViewModel.addRoutineData(routineModel)
            } else {
                showToast("check all fields")
            }

            setAlarm(title, decs, date, time)
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setAlarm(title: String, decs: String, date: String, time: String) {
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, NotificationBroadcast::class.java)
        intent.putExtra("title", title)
        intent.putExtra("decs", decs)
        intent.putExtra("date", date)
        intent.putExtra("time", time)

        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val datetime: String = "$date  $timeNotify"
        val formatter: DateFormat = SimpleDateFormat("d-M-yyyy hh:mm")

        try {
            val date1 = formatter.parse(datetime)
            alarmManager.set(AlarmManager.RTC_WAKEUP, date1.time, pendingIntent)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun FormatTime(hour: Int, minute: Int): String {
        var time: String = ""
        val formattedMinute: String = if (minute / 10 == 0) {
            "0$minute"
        } else {
            "" + minute
        }
        time = when {
            hour == 0 -> {
                "12:$formattedMinute AM"
            }
            hour < 12 -> {
                "$hour:$formattedMinute AM"
            }
            hour == 12 -> {
                "12:$formattedMinute PM"
            }
            else -> {
                val temp = hour - 12
                "$temp:$formattedMinute PM"
            }
        }
        return time
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}