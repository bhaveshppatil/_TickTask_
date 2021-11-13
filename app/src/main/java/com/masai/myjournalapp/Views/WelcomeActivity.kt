package com.masai.myjournalapp.Views

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

        ivEditUsername.setOnClickListener {

            val alert =  AlertDialog.Builder(this)
            val edittext = EditText(this)
            edittext.hint = "username"
            edittext.maxLines = 1

            val layout = FrameLayout(this)

            layout.setPaddingRelative(45,15,45,0)

            alert.setTitle("Enter Username")

            layout.addView(edittext)

            alert.setView(layout)

            alert.setPositiveButton(getString(R.string.label_save), DialogInterface.OnClickListener {

                    dialog, which ->
                run {

                    val qName = edittext.text.toString()
                    tvUserName.text = "Hello, $qName \nWelcome Back"
                }

            })
            alert.setNegativeButton(getString(R.string.label_cancel), DialogInterface.OnClickListener {

                    dialog, which ->
                run {
                    dialog.dismiss()
                }
            })

            alert.show()
        }

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

            val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)
            val high = dialog.findViewById<RadioButton>(R.id.high)
            val low = dialog.findViewById<RadioButton>(R.id.low)

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                val radioButton: Int = radioGroup.checkedRadioButtonId

                if (high.id == radioButton) {
                    Toast.makeText(
                        this,
                        "High Priority Task",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (low.id == radioButton) {
                    Toast.makeText(
                        this,
                        "Low Priority Task",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            dialog.btnAddRoutine.setOnClickListener {

                val title = dialog.etRoutine.text.toString()
                val decs = dialog.etDecs.text.toString()
                val date = dialog.etDate.text.toString()
                val time = dialog.etTime.text.toString()

                val routineModel = RoutineModel(title, decs, date, time, "Not Completed")
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
            updateUI(routineList)
            routineAdapter.notifyDataSetChanged()
        })

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

    private fun deleteAllRoutines() {
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
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
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
            val newTime = dialog.etTime.text.toString()
            routineModel.decs = newDecs
            routineModel.title = newTitle
            routineModel.date = newDate
            routineModel.time = newTime
            routineModel.status = "Not Completed"

            CoroutineScope(Dispatchers.IO).launch {
                routineViewModel.updateRoutineData(routineModel)
            }
            dialog.dismiss()
        }
        dialog.show()
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

    override fun onTaskCompleted(routineModel: RoutineModel) {
        routineModel.status = "Completed"
        CoroutineScope(Dispatchers.IO).launch {
            routineViewModel.updateRoutineData(routineModel)
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}