package com.bhavesh.ticktask.ui.clickListener

import com.bhavesh.ticktask.data.model.RoutineModel


interface OnTaskItemClicked {

    fun onEditClicked(routineModel: RoutineModel)

    fun onDeleteClicked(routineModel: RoutineModel)
}