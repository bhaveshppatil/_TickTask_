package com.masai.myjournalapp.Views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.masai.myjournalapp.R
import com.masai.myjournalapp.RoomDatabase.RoutineDAO
import com.masai.myjournalapp.RoomDatabase.RoutineRoomDB
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var routineRoomDB: RoutineRoomDB
    private lateinit var routineDAO: RoutineDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        routineRoomDB = RoutineRoomDB.getDatabaseObject(this)
        routineDAO = routineRoomDB.getRoutineDAO()

        val btnLoginUp = findViewById<Button>(R.id.btnLoginUp)
        val tvNewUser = findViewById<TextView>(R.id.tvNewUser)

        tvSkip.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }

        btnLoginUp.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                val loginEmail = etEmail.text.toString()
                val loginPasswd = etPasswd.text.toString()
                val user = routineDAO.getUserData(loginEmail, loginPasswd)

                CoroutineScope(Dispatchers.Main).launch {
                    if (user != null) {
                        if (user.email.isNotEmpty()) {
                            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        tvNewUser.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}