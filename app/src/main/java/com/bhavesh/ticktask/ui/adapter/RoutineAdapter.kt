package com.bhavesh.ticktask.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bhavesh.ticktask.data.model.RoutineModel
import com.bhavesh.ticktask.databinding.RoutineItemRowBinding
import com.bhavesh.ticktask.ui.clickListener.OnTaskItemClicked
import com.bhavesh.ticktask.ui.viewholder.TaskViewHolder


class RoutineAdapter(
    val context: Context,
    val routineList: MutableList<RoutineModel>,
    val listener: OnTaskItemClicked
) : RecyclerView.Adapter<TaskViewHolder>() {

    private lateinit var binding: RoutineItemRowBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        binding = RoutineItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val routineModel = routineList[position]
        holder.setData(routineModel, listener, context)
    }

    override fun getItemCount(): Int {
        return routineList.size
    }
}