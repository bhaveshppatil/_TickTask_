package com.masai.myjournalapp.Views

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.masai.myjournalapp.Model.RoutineModel
import com.masai.myjournalapp.R
import com.masai.myjournalapp.Repository.RoutineRepository
import com.masai.myjournalapp.RoomDatabase.RoutineDAO
import com.masai.myjournalapp.RoomDatabase.RoutineRoomDB
import com.masai.myjournalapp.ViewModel.RoutineViewModel
import com.masai.myjournalapp.ViewModel.RoutineViewModelFactory
import com.masai.myjournalapp.adapter.OnTaskItemClicked
import com.masai.myjournalapp.adapter.RoutineAdapter
import kotlinx.android.synthetic.main.add_new_routine.*
import kotlinx.android.synthetic.main.welcome_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WelcomeActivity : AppCompatActivity(), OnTaskItemClicked {

    private val routineList = mutableListOf<RoutineModel>()
    lateinit var routineAdapter: RoutineAdapter

    private lateinit var routineRoomDB: RoutineRoomDB
    private lateinit var routineDAO: RoutineDAO
    private lateinit var routineViewModel: RoutineViewModel

    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        supportActionBar?.hide()

        routineDAO = RoutineRoomDB.getDatabaseObject(this).getRoutineDAO()
        val routineRepository = RoutineRepository(routineDAO)
        val routineViewModelFactory = RoutineViewModelFactory(routineRepository)
        routineViewModel =
            ViewModelProviders.of(this, routineViewModelFactory).get(RoutineViewModel::class.java)

        btnFab.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.add_new_routine)

            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            dialog.ivCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.high -> Toast.makeText(
                        this,
                        "High Priority Task",
                        Toast.LENGTH_SHORT
                    ).show()
                    R.id.low -> Toast.makeText(
                        this,
                        "Low Priority Task",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            dialog.ivSelectDate.setOnClickListener(View.OnClickListener {

                // Get Current Date
                val c = Calendar.getInstance()
                mYear = c[Calendar.YEAR]
                mMonth = c[Calendar.MONTH]
                mDay = c[Calendar.DAY_OF_WEEK]

                val datePickerDialog = DatePickerDialog(
                    this,
                    { view, year, monthOfYear, dayOfWeek ->

                        dialog.etDate.text = ("$dayOfWeek - ${monthOfYear + 1} - $year").toString()

                    }, mYear, mMonth, mDay
                )
                datePickerDialog.show()

            })

            dialog.ivSelectTime.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        dialog.etTime.text = SimpleDateFormat("HH:mm").format(cal.time)
                    }
                TimePickerDialog(
                    this,
                    timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                ).show()
            }

            dialog.btnAddRoutine.setOnClickListener {

                val title = dialog.etRoutine.text.toString()
                val decs = dialog.etDecs.text.toString()
                val date = dialog.etDate.text.toString()
                val time = dialog.etTime.text.toString()

                val routineModel = RoutineModel(title, decs, date, time)
                routineViewModel.addRoutineData(routineModel)
                dialog.dismiss()
            }
            dialog.show()
        }

        routineAdapter = RoutineAdapter(this, routineList, this)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = routineAdapter

        routineViewModel.getRoutines().observe(this, Observer {
            routineList.clear()
            routineList.addAll(it)
            routineAdapter.notifyDataSetChanged()
        })

    }

    override fun onEditClicked(routineModel: RoutineModel) {

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
            val cal = Calendar.getInstance()
            val timeSetListener =
                TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    dialog.etTime.text = SimpleDateFormat("HH:mm").format(cal.time)
                }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        dialog.ivSelectDate.setOnClickListener(View.OnClickListener {

            // Get Current Date
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_WEEK]

            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfWeek ->

                    dialog.etDate.text = ("$dayOfWeek - ${monthOfYear + 1} - $year").toString()

                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()

        })

        dialog.btnAddRoutine.setOnClickListener {

            val newTitle = dialog.etRoutine.text.toString()
            val newDecs = dialog.etDecs.text.toString()
            val newDate = dialog.etDate.text.toString()
            val newTime = dialog.etTime.toString()

            routineModel.decs = newDecs
            routineModel.title = newTitle
            routineModel.date = newDate
            routineModel.time = newTime

            CoroutineScope(Dispatchers.IO).launch {
                routineViewModel.updateRoutineData(routineModel)

            }
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDeleteClicked(routineModel: RoutineModel) {
        CoroutineScope(Dispatchers.IO).launch {
            routineViewModel.deleteRoutineData(routineModel)
        }
    }

    /*private fun updateData() {
        val updatedData = dbHandler.getRoutineData()
        routineList.clear()
        routineList.addAll(updatedData)
        routineAdapter.notifyDataSetChanged()
    }*/
}