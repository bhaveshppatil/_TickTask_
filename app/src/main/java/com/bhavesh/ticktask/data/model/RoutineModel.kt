package com.bhavesh.ticktask.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "routine_manager")
data class RoutineModel(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "decs") var decs: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "time") var time: String,
    @ColumnInfo(name = "priority") var priority: String,

    ) : Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null
}