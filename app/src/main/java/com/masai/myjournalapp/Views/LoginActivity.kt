package com.masai.myjournalapp.Views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.masai.myjournalapp.R
import com.masai.myjournalapp.Repository.RoutineRepository
import com.masai.myjournalapp.RoomDatabase.RoutineDAO
import com.masai.myjournalapp.RoomDatabase.RoutineRoomDB
import com.masai.myjournalapp.ViewModel.RoutineViewModel
import com.masai.myjournalapp.ViewModel.RoutineViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var routineRoomDB: RoutineRoomDB
    private lateinit var routineDAO: RoutineDAO
    private lateinit var routineViewModel: RoutineViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        routineDAO = RoutineRoomDB.getDatabaseObject(this).getRoutineDAO()
        val routineRepository = RoutineRepository(routineDAO)
        val routineViewModelFactory = RoutineViewModelFactory(routineRepository)
        routineViewModel =
            ViewModelProviders.of(this, routineViewModelFactory).get(RoutineViewModel::class.java)

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
                routineViewModel.checkUserData(loginEmail, loginPasswd)

                CoroutineScope(Dispatchers.Main).launch {
                    if (loginEmail.isNotEmpty()) {
                        val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_SHORT)
                            .show()
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