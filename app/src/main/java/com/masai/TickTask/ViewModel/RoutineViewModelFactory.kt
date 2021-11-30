package com.masai.TickTask.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.masai.TickTask.Repository.RoutineRepository

class RoutineViewModelFactory(private val routineRepository: RoutineRepository)  : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoutineViewModel(routineRepository) as T
    }

}