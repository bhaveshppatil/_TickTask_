package com.masai.TickTask.Views

import android.app.*
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.masai.TickTask.ClickListener.OnTaskItemClicked
import com.masai.TickTask.R
import com.masai.TickTask.ViewModel.RoutineViewModel
import com.masai.TickTask.ViewModel.RoutineViewModelFactory
import com.masai.TickTask.adapter.RoutineAdapter
import com.masai.TickTask.data.Model.RoutineModel
import com.masai.TickTask.data.Repository.RoutineRepository
import com.masai.TickTask.data.RoomDB.RoutineDAO
import com.masai.TickTask.data.RoomDB.RoutineRoomDB
import com.masai.TickTask.fragment.BottomDialogFragment
import com.masai.TickTask.notification.NotificationBroadcast
import com.masai.TickTask.utils.SwipeToDelete
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.add_new_routine.*
import kotlinx.android.synthetic.main.welcome_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class WelcomeActivity : AppCompatActivity(), OnTaskItemClicked {

    private val routineList = mutableListOf<RoutineModel>()
    lateinit var routineAdapter: RoutineAdapter
    private lateinit var routineRoomDB: RoutineRoomDB
    private lateinit var routineDAO: RoutineDAO
    private lateinit var routineViewModel: RoutineViewModel
    var timeNotify: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        supportActionBar?.hide()

        ivEditUsername.setOnClickListener {
            showEditTextDialog()
        }

        setUpRecyclerView()
        routineDAO = RoutineRoomDB.getDatabaseObject(this).getRoutineDAO()
        val routineRepository = RoutineRepository(routineDAO)
        val routineViewModelFactory = RoutineViewModelFactory(routineRepository)
        routineViewModel =
            ViewModelProviders.of(this, routineViewModelFactory).get(RoutineViewModel::class.java)

        btnFab.setOnClickListener {
            onFabClick()
        }

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = routineAdapter

        routineViewModel.getRoutines().observe(this, Observer {
            routineList.clear()
            routineList.addAll(it)
            updateUI(routineList)
            routineAdapter.notifyDataSetChanged()
        })

        bottomAppBar.setNavigationOnClickListener {
            val bottomSheet = BottomDialogFragment()
            bottomSheet.show(supportFragmentManager, "TAG")
        }

    }

    private fun setUpRecyclerView() {
        routineAdapter = RoutineAdapter(this, routineList, this)

        recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = routineAdapter

            itemAnimator = SlideInUpAnimator().apply {
                addDuration = 300
            }
            swipeToDelete(this)
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeCallback = object : SwipeToDelete(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemClicked = routineAdapter.routineList[viewHolder.adapterPosition]
                routineViewModel.deleteRoutineData(itemClicked)
                restoreData(viewHolder.itemView, itemClicked)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreData(
        view: View,
        routineModel: RoutineModel
    ) {
        Snackbar.make(view, "Deleted ${routineModel.title}", Snackbar.LENGTH_LONG).also {
            it.apply {
                setAction("Undo") {
                    routineViewModel.addRoutineData(routineModel)
                }
                setBackgroundTint(Color.RED)
                show()
            }
        }
    }

    private fun updateUI(routineModelList: List<RoutineModel>) {

        if (routineModelList.isEmpty()) {
            recyclerview.visibility = View.GONE
            crdNoRoutines.visibility = View.VISIBLE
        } else {
            crdNoRoutines.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
        }
    }


    fun deleteAllRoutines() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Do you want to remove this routine??")
            setPositiveButton("Yes") { _, _ ->
                routineViewModel.deleteAllRoutines()
                showToast("Routine deleted successfully")
            }
            setNegativeButton("No") { _, _ -> }
            create()
            show()
        }
    }

    override fun onEditClicked(routineModel: RoutineModel) {
        onEditClick(routineModel)
    }

    override fun onDeleteClicked(routineModel: RoutineModel) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Do you want to remove this routine??")
            setPositiveButton("Yes") { _, _ ->
                routineViewModel.deleteRoutineData(routineModel)
                showToast("Routine deleted successfully")
            }
            setNegativeButton("No") { _, _ -> }
            create()
            show()
        }
    }

    private fun setAlarm(title: String, decs: String, date: String, time: String) {
        val am = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(applicationContext, NotificationBroadcast::class.java)
        intent.putExtra("title", title)
        intent.putExtra("decs", decs)
        intent.putExtra("date", date)
        intent.putExtra("time", time)
        val pendingIntent =
            PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val datetime: String = "$date  $timeNotify"
        val formatter: DateFormat = SimpleDateFormat("d-M-yyyy hh:mm")
        try {
            val date1 = formatter.parse(datetime)
            am.set(AlarmManager.RTC_WAKEUP, date1.time, pendingIntent)
            showToast("Alarm")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun onEditClick(routineModel: RoutineModel) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_new_routine)
        dialog.setTitle("Add new Routine")

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.ivCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.ivSelectTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            val timePickerDialog = TimePickerDialog(
                this,
                { timePicker, i, i1 ->
                    timeNotify = "$i:$i1"
                    dialog.etTime.text = FormatTime(i, i1).toString()
                }, hour, minute, false
            )
            timePickerDialog.show()
        }

        dialog.ivSelectDate.setOnClickListener(View.OnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this,
                { datePicker, year, month, day ->
                    dialog.etDate.text = day.toString() + "-" + (month + 1) + "-" + year
                }, year, month, day
            )
            datePickerDialog.show()
        })

        dialog.btnAddRoutine.setOnClickListener {

            routineModel.decs = dialog.etDecs.text.toString()
            routineModel.title = dialog.etRoutine.text.toString()
            routineModel.date = dialog.etDate.text.toString()
            routineModel.time = dialog.etTime.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                routineViewModel.updateRoutineData(routineModel)
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun FormatTime(hour: Int, minute: Int): String {
        var time: String = ""
        val formattedMinute: String = if (minute / 10 == 0) {
            "0$minute"
        } else {
            "" + minute
        }
        time = if (hour == 0) {
            "12:$formattedMinute AM"
        } else if (hour < 12) {
            "$hour:$formattedMinute AM"
        } else if (hour == 12) {
            "12:$formattedMinute PM"
        } else {
            val temp = hour - 12
            "$temp:$formattedMinute PM"
        }
        return time
    }

    private fun onFabClick() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_new_routine)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.ivCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.ivSelectDate.setOnClickListener(View.OnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this,
                { datePicker, year, month, day ->
                    dialog.etDate.text = day.toString() + "-" + (month + 1) + "-" + year
                }, year, month, day
            )
            datePickerDialog.show()
        })

        dialog.ivSelectTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]
            val timePickerDialog = TimePickerDialog(
                this,
                { timePicker, i, i1 ->
                    timeNotify = "$i:$i1"
                    dialog.etTime.text = FormatTime(i, i1).toString()
                }, hour, minute, false
            )
            timePickerDialog.show()
        }

        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)
        val high = dialog.findViewById<RadioButton>(R.id.high)
        val low = dialog.findViewById<RadioButton>(R.id.low)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: Int = radioGroup.checkedRadioButtonId

            if (high.id == radioButton) {
                showToast("High Priority Task")
            }
            if (low.id == radioButton) {
                showToast("Low Priority Task")
            }
        }

        dialog.btnAddRoutine.setOnClickListener {

            val title = dialog.etRoutine.text.toString()
            val decs = dialog.etDecs.text.toString()
            val date = dialog.etDate.text.toString()
            val time = dialog.etTime.text.toString()
            if (high.isChecked) {
                val routineModel = RoutineModel(title, decs, date, time, "High")
                routineViewModel.addRoutineData(routineModel)
            } else {
                val routineModel = RoutineModel(title, decs, date, time, "Low")
                routineViewModel.addRoutineData(routineModel)
            }
            setAlarm(title, decs, date, time)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showEditTextDialog() {
        val alert = AlertDialog.Builder(this)
        val edittext = EditText(this)
        edittext.hint = "username"
        edittext.maxLines = 1

        val layout = FrameLayout(this)

        layout.setPaddingRelative(45, 15, 45, 0)
        alert.setTitle("Enter Username")
        layout.addView(edittext)
        alert.setView(layout)
        alert.setPositiveButton(
            getString(R.string.label_save),
            DialogInterface.OnClickListener { dialog, which ->
                run {
                    val qName = edittext.text.toString()
                    tvUserName.text = "Hello, $qName \nWelcome Back"
                }
            })
        alert.setNegativeButton(
            getString(R.string.label_cancel),
            DialogInterface.OnClickListener { dialog, which ->
                run {
                    dialog.dismiss()
                }
            })

        alert.show()
    }
}