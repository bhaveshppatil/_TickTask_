package com.masai.TickTask.ClickListener

import com.masai.TickTask.data.Model.RoutineModel


interface OnTaskItemClicked {

    fun onEditClicked(routineModel: RoutineModel)

    fun onDeleteClicked(routineModel: RoutineModel)
}