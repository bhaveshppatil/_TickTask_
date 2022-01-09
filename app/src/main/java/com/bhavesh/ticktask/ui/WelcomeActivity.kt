package com.bhavesh.ticktask.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.bhavesh.ticktask.ui.adapter.RoutineAdapter
import com.bhavesh.ticktask.ui.clickListener.OnTaskItemClicked
import com.bhavesh.ticktask.utils.SwipeToDelete
import com.bhavesh.ticktask.viewModel.RoutineViewModel
import com.bhavesh.ticktask.viewModel.RoutineViewModelFactory
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.welcome_activity.*

class WelcomeActivity : AppCompatActivity(), OnTaskItemClicked {

    private val routineList = mutableListOf<RoutineModel>()
    lateinit var routineAdapter: RoutineAdapter
    private lateinit var routineRoomDB: RoutineRoomDB
    private lateinit var routineDAO: RoutineDAO
    private lateinit var routineViewModel: RoutineViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        supportActionBar?.hide()
        setSupportActionBar(toolbar)

        setUpRecyclerView()
        initialize()

        btnFab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java);
            startActivity(intent)
        }

        routineViewModel.getRoutines().observe(this, Observer {
            routineList.clear()
            routineList.addAll(it)
            updateUI(routineList)
            routineAdapter.notifyDataSetChanged()
        })
    }

    private fun initialize(){
        routineDAO = RoutineRoomDB.getDatabaseObject(this).getRoutineDAO()
        val routineRepository = RoutineRepository(routineDAO)
        val routineViewModelFactory = RoutineViewModelFactory(routineRepository)
        routineViewModel =
            ViewModelProviders.of(this, routineViewModelFactory).get(RoutineViewModel::class.java)
    }



    private fun updateUI(routineModelList: List<RoutineModel>) {

        if (routineModelList.isEmpty()) {
            recyclerView.visibility = View.GONE
            layoutEmptyList.visibility = View.VISIBLE
        } else {
            layoutEmptyList.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView() {
        routineAdapter = RoutineAdapter(this, routineList, this)

        recyclerView.apply {
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
        val intent = Intent(this, AddTaskActivity::class.java)
        intent.putExtra("Edit", "editTask")
        startActivity(intent)
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

    private fun deleteAllRoutines() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Do you want to remove all routine??")
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
