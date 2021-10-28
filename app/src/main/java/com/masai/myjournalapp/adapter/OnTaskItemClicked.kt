package com.masai.myjournalapp.adapter

import com.masai.myjournalapp.Model.RoutineModel


interface OnTaskItemClicked {

    fun onEditClicked(routineModel: RoutineModel)

    fun onDeleteClicked(routineModel: RoutineModel)

    fun onTaskCompleted(routineModel: RoutineModel)
}