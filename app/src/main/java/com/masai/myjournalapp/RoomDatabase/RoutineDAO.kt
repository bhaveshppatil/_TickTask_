package com.masai.myjournalapp.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.masai.myjournalapp.Model.RoutineModel
import com.masai.myjournalapp.Model.UserModel

@Dao
interface RoutineDAO {

    //Insert the data into database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRoutine(routineModel: RoutineModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserData(userModel: UserModel)

    @Query("select * from user_data where email = :email and passwd = :passwd")
    fun getUserData(email: String, passwd: String): UserModel


    //Inside we need to pass the query.
    @Query("select * from routine_manager")
    fun getRoutineData(): LiveData<List<RoutineModel>>

    //Update the data into database
    @Update
    fun updateRoutine(routineModel: RoutineModel)

    //Delete the record from Database
    @Delete
    fun deleteRoutine(routineModel: RoutineModel)
}