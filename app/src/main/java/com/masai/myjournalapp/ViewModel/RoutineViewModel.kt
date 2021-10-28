package com.masai.myjournalapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.masai.myjournalapp.Model.RoutineModel
import com.masai.myjournalapp.Model.UserModel
import com.masai.myjournalapp.Repository.RoutineRepository

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