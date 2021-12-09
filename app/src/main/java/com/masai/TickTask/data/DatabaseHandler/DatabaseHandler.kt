package com.masai.TickTask.data.DatabaseHandler

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.masai.TickTask.data.Model.RoutineModel

class DatabaseHandler(val context: Context) :
    SQLiteOpenHelper(context, "journal.db", null, 1) {

    companion object {
        val TABLE_NAME = "JournalTable"
        val ID = "id"
        val TITLE = "title"
        val DECS = "decs"
        val DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            "CREATE TABLE " +
                    "$TABLE_NAME(" +
                    "$ID INTEGER PRIMARY KEY, " +
                    "$TITLE TEXT, " +
                    "$DECS TEXT, " +
                    "$DATE TEXT)"
        db?.execSQL(query)
    }

    fun insertDataToDB(title: String, decs: String, date: String) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(TITLE, title)
        values.put(DECS, decs)
        values.put(DATE, date)
        val id = db.insert(TABLE_NAME, null, values)

        if (id.toInt() == -1) {
            Toast.makeText(context, "Failed to insert data", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Data Inserted Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    fun getRoutineData(): MutableList<RoutineModel> {

        val routineList = mutableListOf<RoutineModel>()
        val db = readableDatabase
        val query = "select * from $TABLE_NAME"

        val cursor = db?.rawQuery(query, null)

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()

            do {
                val id = cursor.getInt(cursor.getColumnIndex(ID))
                val title = cursor.getString(cursor.getColumnIndex(TITLE))
                val decs = cursor.getString(cursor.getColumnIndex(DECS))
                val date = cursor.getString(cursor.getColumnIndex(DATE))

                val routineModel = RoutineModel(title, decs, date, id.toString(), "High")
                routineList.add(routineModel)

            } while (cursor.moveToNext())
        }
        return routineList
    }

    fun editRoutineData(routineModel: RoutineModel) {
        val db = writableDatabase
        val contentValues = ContentValues()

        contentValues.put(TITLE, routineModel.title)
        contentValues.put(DECS, routineModel.decs)
        contentValues.put(DATE, routineModel.date)

        val res = db.update(TABLE_NAME, contentValues, "id=${routineModel.id}", null)

        if (res == 1) {
            Toast.makeText(context, "Task Completed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteRoutine(routineModel: RoutineModel) {
        val db = writableDatabase
        val deleteRow = db.delete(TABLE_NAME, "id = ${routineModel.id}", null)

        if (deleteRow == 1) {
            Toast.makeText(context, "Task Deleted Successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}