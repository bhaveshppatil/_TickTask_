package com.masai.TickTask.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.masai.TickTask.Model.RoutineModel
import com.masai.TickTask.Model.UserModel
import com.masai.TickTask.Repository.RoutineRepository

class RoutineViewModel(private val routineRepository: RoutineRepository) : ViewModel() {

    fun addRoutineData(routineModel: RoutineModel) {
        routineRepository.addRoutineToRoom(routineModel)

    }

    fun getRoutines(): LiveData<List<RoutineModel>> {
        return routineRepository.getAllRoutines()
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