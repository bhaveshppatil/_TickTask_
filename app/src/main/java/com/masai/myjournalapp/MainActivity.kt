package com.masai.myjournalapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.masai.myjournalapp.DatabaseHandler.DatabaseHandler
import com.masai.myjournalapp.Model.RoutineModel
import com.masai.myjournalapp.adapter.OnTaskItemClicked
import com.masai.myjournalapp.adapter.RoutineAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), OnTaskItemClicked {

    private val routineList = mutableListOf<RoutineModel>()
    lateinit var routineAdapter: RoutineAdapter
    private val dbHandler = DatabaseHandler(this)

    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        btnFab.setOnClickListener {

            ivCancel.setOnClickListener {
                crdRoutine.visibility = View.GONE
            }

            ivSelectDate.setOnClickListener(View.OnClickListener {
                // Get Current Date
                val c = Calendar.getInstance()
                mYear = c[Calendar.YEAR]
                mMonth = c[Calendar.MONTH]
                mDay = c[Calendar.DAY_OF_WEEK]

                val datePickerDialog = DatePickerDialog(
                    this,
                    { view, year, monthOfYear, dayOfWeek ->

                        etDate.text = ("$dayOfWeek - ${monthOfYear + 1} - $year").toString()

                    }, mYear, mMonth, mDay
                )
                datePickerDialog.show()
            })

            crdRoutine.visibility = View.VISIBLE
            btnAddRoutine.setOnClickListener {

                val title = etRoutine.text.toString()
                val decs = etDecs.text.toString()
                val date = etDate.text.toString()

                dbHandler.insertDataToDB(title, decs, date)
                updateData()
                crdRoutine.visibility = View.GONE
            }
        }

        routineList.addAll(dbHandler.getAllTask())
        routineAdapter = RoutineAdapter(this, routineList, this)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = routineAdapter
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

            dbHandler.editRoutineData(routineModel)
            updateData()
            crdRoutine.visibility = View.GONE
        }
    }

    override fun onDeleteClicked(routineModel: RoutineModel) {
        dbHandler.deleteRoutine(routineModel)
        updateData()

    }

    private fun updateData() {
        val updatedData = dbHandler.getAllTask()
        routineList.clear()
        routineList.addAll(updatedData)
        routineAdapter.notifyDataSetChanged()
    }

}