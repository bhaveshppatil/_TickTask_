package com.masai.myjournalapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.masai.myjournalapp.Model.RoutineModel
import com.masai.myjournalapp.R


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
        holder.decs.text = routineModel.decs
        holder.date.text = routineModel.date

        holder.menuBar.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.menuBar)
            popupMenu.inflate(com.masai.myjournalapp.R.menu.query_menu_list)

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
        val menuBar: TextView = itemView.findViewById(R.id.tvMenu)

    }
}