package com.bhavesh.ticktask.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bhavesh.ticktask.data.repository.RoutineRepository

class RoutineViewModelFactory(private val routineRepository: RoutineRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoutineViewModel(routineRepository) as T
    }

}