package com.bhavesh.ticktask.ui.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.SearchView
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bhavesh.ticktask.R
import com.bhavesh.ticktask.data.model.RoutineModel
import com.bhavesh.ticktask.data.repository.RoutineRepository
import com.bhavesh.ticktask.data.roomDB.RoutineDAO
import com.bhavesh.ticktask.data.roomDB.RoutineRoomDB
import com.bhavesh.ticktask.databinding.WelcomeActivityBinding
import com.bhavesh.ticktask.ui.adapter.RoutineAdapter
import com.bhavesh.ticktask.ui.clickListener.OnTaskItemClicked
import com.bhavesh.ticktask.utils.AlarmUtils.setAlarm
import com.bhavesh.ticktask.utils.DateTimeUtils
import com.bhavesh.ticktask.utils.SwipeToDelete
import com.bhavesh.ticktask.utils.showToast
import com.bhavesh.ticktask.viewModel.RoutineViewModel
import com.bhavesh.ticktask.viewModel.RoutineViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity(), OnTaskItemClicked {

    private val routineList = mutableListOf<RoutineModel>()
    lateinit var routineAdapter: RoutineAdapter
    private lateinit var routineDAO: RoutineDAO
    private lateinit var routineViewModel: RoutineViewModel
    private lateinit var binding: WelcomeActivityBinding
    var timeNotify: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.welcome_activity)
        supportActionBar?.hide()
        setSupportActionBar(binding.toolbar)

        setUpRecyclerView()
        initialize()

        binding.btnFab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        routineViewModel.getRoutines().observe(this, Observer {
            routineList.clear()
            routineList.addAll(it)
            updateUI(routineList)
            routineAdapter.notifyDataSetChanged()
        })
    }

    private fun initialize() {
        routineDAO = RoutineRoomDB.getDatabaseObject(this).getRoutineDAO()
        val routineRepository = RoutineRepository(routineDAO)
        val routineViewModelFactory = RoutineViewModelFactory(routineRepository)
        routineViewModel =
            ViewModelProviders.of(this, routineViewModelFactory).get(RoutineViewModel::class.java)
    }

    private fun updateUI(routineModelList: List<RoutineModel>) {

        if (routineModelList.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.layoutEmptyList.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyList.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView() {
        routineAdapter = RoutineAdapter(this, routineList, this)

        binding.recyclerView.apply {
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
                setActionTextColor(Color.parseColor("#1C1F27"))
                setAction("Undo") {
                    routineViewModel.addRoutineData(routineModel)
                }
                setBackgroundTint(Color.parseColor("#11CFC5"))
                show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        val searchView = menu!!.findItem(R.id.search_bar)
        val searchManager = searchView.actionView as SearchView
        searchManager.queryHint = "search task here"
        searchManager.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    routineViewModel.getSearchRoutine(query)
                        .observe(this@WelcomeActivity, Observer {
                            routineList.clear()
                            routineList.addAll(it)
                            updateUI(routineList)
                            routineAdapter.notifyDataSetChanged()
                        })
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAll -> {
                deleteAllRoutines()
            }

            R.id.theme -> {
                showToast("currently not available")
            }

            R.id.setting -> {
                showToast("currently not available")
            }

            R.id.search_bar -> {
                showToast("currently not available")
            }
        }
        return true
    }

    override fun onEditClicked(routineModel: RoutineModel) {
        binding.layoutEdit.visibility = View.VISIBLE
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutEdit).apply {
            peekHeight = 200
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.ivSelectTimeEdit.setOnClickListener {
            DateTimeUtils.showTimePicker(this) { _: TimePicker, hourOfDay: Int, minute: Int ->
                timeNotify = "$hourOfDay:$minute"
                binding.tvTimeEdit.text = DateTimeUtils.formatTime(hourOfDay, minute).toString()
            }
        }

        binding.ivSelectDateEdit.setOnClickListener {
            DateTimeUtils.showDatePicker(this) { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                binding.tvDateEdit.text = "$dayOfMonth-${month + 1}-$year"
            }
        }

        binding.btnEditDone.setOnClickListener {
            val newTitle = binding.etRoutineEdit.text.toString().trim()
            val newDecs = binding.etDecsEdit.text.toString().trim()
            val newDate = binding.tvDateEdit.text.toString().trim()
            val newTime = binding.tvTimeEdit.text.toString().trim()

            if (newTitle.isNotEmpty() && newDecs.isNotEmpty() && newDate.isNotEmpty() && newTime.isNotEmpty()) {
                routineModel.apply {
                    title = newTitle
                    decs = newDecs
                    date = newDate
                    time = newTime
                    priority = "High"
                }

                CoroutineScope(Dispatchers.IO).launch {
                    routineViewModel.updateRoutineData(routineModel)
                }

                setAlarm(this, newTitle, newDecs, newDate, newTime)

                bottomSheetBehavior.isHideable = true
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                showToast(getString(R.string.please_fill_in_all_fields))
            }
        }
    }

    override fun onDeleteClicked(routineModel: RoutineModel) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Do you want to remove this task??")
            setPositiveButton("Yes") { _, _ ->
                routineViewModel.deleteRoutineData(routineModel)
                showToast("Task deleted successfully")
            }
            setNegativeButton("No") { _, _ -> }
            create()
            show()
        }
    }

    override fun onTaskClick(routineModel: RoutineModel) {
        showToast("${routineModel.title} clicked")
    }

    private fun deleteAllRoutines() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Do you want to remove all task??")
            setPositiveButton("Yes") { _, _ ->
                if (routineList.size == 0) {
                    showToast("List is already empty")
                } else {
                    routineViewModel.deleteAllRoutines()
                    showToast("Task Deleted Successfully")
                }
            }
            setNegativeButton("No") { _, _ -> }
            create()
            show()
        }
    }
}
