package com.masai.myjournalapp.Repository

import androidx.lifecycle.LiveData
import com.masai.myjournalapp.Model.RoutineModel
import com.masai.myjournalapp.Model.UserModel
import com.masai.myjournalapp.RoomDatabase.RoutineDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoutineRepository(val routineDAO: RoutineDAO) {

    fun addRoutineToRoom(routineModel: RoutineModel) {
        CoroutineScope(Dispatchers.IO).launch {
            routineDAO.addRoutine(routineModel)
        }
    }

    fun getAllRoutines(): LiveData<List<RoutineModel>> {
        return routineDAO.getRoutineData()
    }

    fun updateRoutineData(routineModel: RoutineModel) {
        CoroutineScope(Dispatchers.IO).launch {
            routineDAO.updateRoutine(routineModel)
        }
    }

    fun deleteRoutine(routineModel: RoutineModel) {
        CoroutineScope(Dispatchers.IO).launch {
            routineDAO.deleteRoutine(routineModel)
        }
    }

    fun checkUserData(email: String, passwd: String) {
        CoroutineScope(Dispatchers.IO).launch {
            routineDAO.getUserData(email, passwd)
        }
    }

    fun deleteAllRoutine(){
        CoroutineScope(Dispatchers.IO).launch {
            routineDAO.deleteAllRoutine()
        }
    }

    fun addNewUser(userModel: UserModel){
        CoroutineScope(Dispatchers.IO).launch {
            routineDAO.insertUserData(userModel)
        }
    }
}