package com.bhavesh.ticktask.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bhavesh.ticktask.R
import com.bhavesh.ticktask.data.model.RoutineModel
import com.bhavesh.ticktask.data.repository.RoutineRepository
import com.bhavesh.ticktask.data.roomDB.RoutineDAO
import com.bhavesh.ticktask.data.roomDB.RoutineRoomDB
import com.bhavesh.ticktask.databinding.ActivityAddTaskBinding
import com.bhavesh.ticktask.utils.AlarmUtils.setAlarm
import com.bhavesh.ticktask.utils.DateTimeUtils
import com.bhavesh.ticktask.utils.DateTimeUtils.formatTime
import com.bhavesh.ticktask.utils.showToast
import com.bhavesh.ticktask.viewModel.RoutineViewModel
import com.bhavesh.ticktask.viewModel.RoutineViewModelFactory

class AddTaskActivity : AppCompatActivity() {
    private lateinit var routineDAO: RoutineDAO
    private lateinit var routineViewModel: RoutineViewModel
    private lateinit var binding: ActivityAddTaskBinding
    private var timeNotify: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_task)

        routineDAO = this.let { RoutineRoomDB.getDatabaseObject(it).getRoutineDAO() }
        val routineRepository = RoutineRepository(routineDAO)
        val routineViewModelFactory = RoutineViewModelFactory(routineRepository)
        routineViewModel =
            ViewModelProviders.of(this, routineViewModelFactory).get(RoutineViewModel::class.java)

        binding.ivSelectTime.setOnClickListener {
            DateTimeUtils.showTimePicker(this) { _: TimePicker, hourOfDay: Int, minute: Int ->
                timeNotify = "$hourOfDay:$minute"
                binding.tvTime.text = formatTime(hourOfDay, minute).toString()
            }
        }

        binding.ivSelectDate.setOnClickListener {
            DateTimeUtils.showDatePicker(this) { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                binding.tvDate.text = "$dayOfMonth-${month + 1}-$year"
            }
        }


        binding.ivPriority.setOnClickListener {
            val title = binding.etRoutine.text.toString()
            if (title.isNotEmpty()) {
                showToast(getString(R.string.added_to_high_priority, title))
            } else {
                showToast(getString(R.string.added_to_high_priority_1))
            }
        }
        binding.btnTaskDone.setOnClickListener {
            val title = binding.etRoutine.text.toString().trim()
            val decs = binding.etDecs.text.toString().trim()
            val date = binding.tvDate.text.toString().trim()
            val time = binding.tvTime.text.toString().trim()

            if (title.isNotEmpty() && decs.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                val routineModel = RoutineModel(title, decs, date, time, "High")
                routineViewModel.addRoutineData(routineModel)

                setAlarm(
                    context = this,
                    title = title,
                    decs = decs,
                    date, time
                )

                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish() // Finish current activity to prevent going back to it with the back button
            } else {
                showToast(getString(R.string.please_fill_in_all_fields))
            }
        }
    }
}