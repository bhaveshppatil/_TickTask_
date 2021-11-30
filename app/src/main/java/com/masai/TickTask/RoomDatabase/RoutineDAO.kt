package com.masai.TickTask.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.masai.TickTask.Model.RoutineModel
import com.masai.TickTask.Model.UserModel

@Dao
interface RoutineDAO {

    //Insert the data into database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRoutine(routineModel: RoutineModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserData(userModel: UserModel)

    @Query("select * from user_data where email = :email and passwd = :passwd")
    fun getUserData(email: String, passwd: String): UserModel

    //It will return the list of task in desc order
    @Query("select * from routine_manager order by id desc ")
    fun getRoutineData(): LiveData<List<RoutineModel>>

    @Query("delete from routine_manager")
    fun deleteAllRoutine()

    //Update the data into database
    @Update
    fun updateRoutine(routineModel: RoutineModel)

    //Delete the record from Database
    @Delete
    fun deleteRoutine(routineModel: RoutineModel)
}