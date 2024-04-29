package com.bhavesh.ticktask.ui.viewholder

import android.content.Context
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bhavesh.ticktask.R
import com.bhavesh.ticktask.data.model.RoutineModel
import com.bhavesh.ticktask.databinding.RoutineItemRowBinding
import com.bhavesh.ticktask.ui.clickListener.OnTaskItemClicked

class TaskViewHolder(private val binding: RoutineItemRowBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun setData(routineModel: RoutineModel, clickListener: OnTaskItemClicked, context: Context) {
        binding.tvRoutineTitle.text = routineModel.title
        when {
            routineModel.title.contains("workout") -> {
                binding.ivTaskPoster.setImageResource(R.drawable.icons8_man_lifting_weights_48)
            }

            routineModel.title.contains("assignment") -> {
                binding.ivTaskPoster.setImageResource(R.drawable.icons8_task_50)
            }

            routineModel.title.contains("meeting") -> {
                binding.ivTaskPoster.setImageResource(R.drawable.icons8_meeting_room_50)
            }
        }
        binding.tvRoutineDecs.text = routineModel.decs
        binding.tvRoutineDate.text = routineModel.date
        binding.tvRoutineDecs.text = routineModel.time

        binding.cardTask.setOnClickListener {
            clickListener.onTaskClick(routineModel)
        }

        binding.tvMenu.setOnClickListener {
            val popupMenu = PopupMenu(context, binding.tvMenu)
            popupMenu.inflate(R.menu.query_menu_list)

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.update -> {
                        clickListener.onEditClicked(routineModel)
                    }

                    R.id.delete -> {
                        clickListener.onDeleteClicked(routineModel)
                    }
                }
                false
            })
            popupMenu.show()
        }
    }
}