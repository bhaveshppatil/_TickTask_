package com.masai.TickTask.data.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "routine_manager")
data class RoutineModel(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "decs") var decs: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "time") var time: String,
    @ColumnInfo(name = "priority") var priority: String,

    ) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null

}