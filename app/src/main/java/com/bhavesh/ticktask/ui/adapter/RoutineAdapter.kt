package com.bhavesh.ticktask.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bhavesh.ticktask.ui.clickListener.OnTaskItemClicked
import com.bhavesh.ticktask.data.model.RoutineModel
import com.bhavesh.ticktask.R


class RoutineAdapter(
    val context: Context,
    val routineList: MutableList<RoutineModel>,
    val listener: OnTaskItemClicked
) : RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val inflater = LayoutInflater.from(context)
        val view1: View = inflater.inflate(R.layout.routine_item_row, parent, false)
        return RoutineViewHolder(view1)

    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {

        val routineModel = routineList[position]

        holder.title.text = routineModel.title

        when {
            routineModel.title.contains("workout") -> {
                holder.ivRoutine.setImageResource(R.drawable.icons8_man_lifting_weights_48)
            }
            routineModel.title.contains("assignment") -> {
                holder.ivRoutine.setImageResource(R.drawable.icons8_task_50)
            }
            routineModel.title.contains("meeting") -> {
                holder.ivRoutine.setImageResource(R.drawable.icons8_meeting_room_50)
            }
        }

        holder.decs.text = routineModel.decs
        holder.date.text = routineModel.date
        holder.time.text = routineModel.time

        if (routineModel.priority == "High") {
            holder.ivPriority.setImageResource(R.drawable.high_priority)
        } else {
            holder.ivPriority.setImageResource(R.drawable.low_priority)
        }

        holder.menuBar.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.menuBar)
            popupMenu.inflate(R.menu.query_menu_list)

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.update -> {
                        listener.onEditClicked(routineModel)
                    }
                    R.id.delete -> {
                        listener.onDeleteClicked(routineModel)
                    }
                }
                false
            })
            popupMenu.show()
        }

    }

    override fun getItemCount(): Int {
        return routineList.size
    }

    class RoutineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.tvRoutineTitle)
        val decs: TextView = itemView.findViewById(R.id.tvRoutineDecs)
        val date: TextView = itemView.findViewById(R.id.tvRoutineDate)
        val time: TextView = itemView.findViewById(R.id.tvRoutineTime)
        val ivRoutine: ImageView = itemView.findViewById(R.id.ivTaskPoster)
        val ivPriority: ImageView = itemView.findViewById(R.id.ivPriority)

        val menuBar: TextView = itemView.findViewById(R.id.tvMenu)

    }
}