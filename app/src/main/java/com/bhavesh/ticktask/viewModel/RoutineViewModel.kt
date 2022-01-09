package com.bhavesh.ticktask.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bhavesh.ticktask.data.model.RoutineModel
import com.bhavesh.ticktask.data.model.UserModel
import com.bhavesh.ticktask.data.repository.RoutineRepository

class RoutineViewModel(private val routineRepository: RoutineRepository) : ViewModel() {

    fun addRoutineData(routineModel: RoutineModel) {
        routineRepository.addRoutineToRoom(routineModel)

    }

    fun getRoutines(): LiveData<List<RoutineModel>> {
        return routineRepository.getAllRoutines()
    }

    fun getSearchRoutine(search: String): LiveData<List<RoutineModel>> {
        return routineRepository.getSearchData(search)
    }

    fun updateRoutineData(routineModel: RoutineModel) {
        routineRepository.updateRoutineData(routineModel)
    }

    fun deleteRoutineData(routineModel: RoutineModel) {
        routineRepository.deleteRoutine(routineModel)
    }

    fun checkUserData(email: String, passwd: String) {
        routineRepository.checkUserData(email, passwd)
    }

    fun addNewUserData(userModel: UserModel) {
        routineRepository.addNewUser(userModel)

    }

    fun deleteAllRoutines(){
        routineRepository.deleteAllRoutine()
    }
}