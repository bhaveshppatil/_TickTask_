package com.bhavesh.ticktask.ui.viewholder

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bhavesh.ticktask.R
import com.bhavesh.ticktask.data.model.RoutineModel
import com.bhavesh.ticktask.ui.clickListener.OnTaskItemClicked

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val title: TextView = itemView.findViewById(R.id.tvRoutineTitle)
    val decs: TextView = itemView.findViewById(R.id.tvRoutineDecs)
    val date: TextView = itemView.findViewById(R.id.tvRoutineDate)
    val time: TextView = itemView.findViewById(R.id.tvRoutineTime)
    val ivRoutine: ImageView = itemView.findViewById(R.id.ivTaskPoster)
    val ivPriority: ImageView = itemView.findViewById(R.id.ivPriority)
    val menuBar: TextView = itemView.findViewById(R.id.tvMenu)

    fun setData(routineModel: RoutineModel, clickListener: OnTaskItemClicked, context: Context) {
        title.text = routineModel.title

        when {
            routineModel.title.contains("workout") -> {
                ivRoutine.setImageResource(R.drawable.icons8_man_lifting_weights_48)
            }
            routineModel.title.contains("assignment") -> {
                ivRoutine.setImageResource(R.drawable.icons8_task_50)
            }
            routineModel.title.contains("meeting") -> {
                ivRoutine.setImageResource(R.drawable.icons8_meeting_room_50)
            }
        }

        decs.text = routineModel.decs
        date.text = routineModel.date
        time.text = routineModel.time

        if (routineModel.priority == "High") {
            ivPriority.setImageResource(R.drawable.high_priority)
        } else {
            ivPriority.setImageResource(R.drawable.low_priority)
        }

        menuBar.setOnClickListener {
            val popupMenu = PopupMenu(context, menuBar)
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