package com.masai.myjournalapp.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.masai.myjournalapp.Model.RoutineModel

@Dao
interface RoutineDAO {

    //Insert the data into database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRoutine(routineModel: RoutineModel)

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