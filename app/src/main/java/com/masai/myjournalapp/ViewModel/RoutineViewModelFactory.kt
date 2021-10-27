package com.masai.myjournalapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.masai.myjournalapp.Repository.RoutineRepository

class RoutineViewModelFactory(private val routineRepository: RoutineRepository)  : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RoutineViewModel(routineRepository) as T
    }

}