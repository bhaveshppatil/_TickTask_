package com.masai.myjournalapp.Views

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.masai.myjournalapp.Model.RoutineModel
import com.masai.myjournalapp.R
import com.masai.myjournalapp.RoomDatabase.RoutineDAO
import com.masai.myjournalapp.RoomDatabase.RoutineRoomDB
import com.masai.myjournalapp.adapter.OnTaskItemClicked
import com.masai.myjournalapp.adapter.RoutineAdapter
import kotlinx.android.synthetic.main.welcome_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class WelcomeActivity : AppCompatActivity(), OnTaskItemClicked {

    private val routineList = mutableListOf<RoutineModel>()
    lateinit var routineAdapter: RoutineAdapter

    private lateinit var routineRoomDB: RoutineRoomDB
    private lateinit var routineDAO: RoutineDAO

    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        supportActionBar?.hide()

        val intent = Intent()
        val username = intent.getStringExtra("name")
        tvUserName.text = "Hello $username"

        routineRoomDB = RoutineRoomDB.getDatabaseObject(this)
        routineDAO = routineRoomDB.getRoutineDAO()

        btnFab.setOnClickListener {

            crdRoutine.visibility = View.VISIBLE

            ivCancel.setOnClickListener {
                crdRoutine.visibility = View.GONE
            }

            ivSelectDate.setOnClickListener(View.OnClickListener {
                // Get Current Date
                val c = Calendar.getInstance()
                mYear = c[Calendar.YEAR]
                mMonth = c[Calendar.MONTH]
                mDay = c[Calendar.DAY_OF_WEEK]

                val datePickerDialog = DatePickerDialog(this,
                    { view, year, monthOfYear, dayOfWeek ->

                        etDate.text = ("$dayOfWeek - ${monthOfYear + 1} - $year").toString()

                    }, mYear, mMonth, mDay
                )
                datePickerDialog.show()
            })

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
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

            btnAddRoutine.setOnClickListener {

                val title = etRoutine.text.toString()
                val decs = etDecs.text.toString()
                val date = etDate.text.toString()
                val routineModel = RoutineModel(title, decs, date)
                CoroutineScope(Dispatchers.IO).launch {
                    routineDAO.addRoutine(routineModel)
                }
                crdRoutine.visibility = View.GONE
            }
        }

        routineAdapter = RoutineAdapter(this, routineList, this)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = routineAdapter

        routineDAO.getRoutineData().observe(this, Observer {
            val routineModel = it
            routineList.clear()
            routineList.addAll(routineModel)
            routineAdapter.notifyDataSetChanged()
        })
    }

    override fun onEditClicked(routineModel: RoutineModel) {

        crdRoutine.visibility = View.VISIBLE

        btnAddRoutine.setOnClickListener {

            val newTitle = etRoutine.text.toString()
            val newDecs = etDecs.text.toString()
            val newDate = etDate.text.toString()

            routineModel.decs = newDecs
            routineModel.title = newTitle
            routineModel.date = newDate

            CoroutineScope(Dispatchers.IO).launch {
                routineDAO.updateRoutine(routineModel)
            }
            crdRoutine.visibility = View.GONE
        }
    }

    override fun onDeleteClicked(routineModel: RoutineModel) {
        CoroutineScope(Dispatchers.IO).launch {
            routineDAO.deleteRoutine(routineModel)
        }
    }

    /*private fun updateData() {
        val updatedData = dbHandler.getRoutineData()
        routineList.clear()
        routineList.addAll(updatedData)
        routineAdapter.notifyDataSetChanged()
    }*/
}