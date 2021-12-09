package com.masai.TickTask.Views.LoginScreen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.masai.TickTask.data.Model.UserModel
import com.masai.TickTask.R
import com.masai.TickTask.data.Repository.RoutineRepository
import com.masai.TickTask.data.RoomDB.RoutineDAO
import com.masai.TickTask.data.RoomDB.RoutineRoomDB
import com.masai.TickTask.ViewModel.RoutineViewModel
import com.masai.TickTask.ViewModel.RoutineViewModelFactory
import com.masai.TickTask.Views.WelcomeActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    private lateinit var routineRoomDB: RoutineRoomDB
    private lateinit var routineDAO: RoutineDAO
    private lateinit var routineViewModel: RoutineViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        routineDAO = RoutineRoomDB.getDatabaseObject(this).getRoutineDAO()
        val routineRepository = RoutineRepository(routineDAO)
        val routineViewModelFactory = RoutineViewModelFactory(routineRepository)
        routineViewModel =
            ViewModelProviders.of(this, routineViewModelFactory).get(RoutineViewModel::class.java)


        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val name = etNameUp.text.toString()
            val email = etEmailSignUp.text.toString()
            val number = etNumberUp.text.toString()
            val passwd = etPasswdUp.text.toString()

            if (name.length > 3 && Pattern.compile(emailPattern).matcher(email)
                    .matches() && number.length == 10 && passwd.length > 6
            ) {
                val userModel = UserModel(name, email, number, passwd)

                CoroutineScope(Dispatchers.IO).launch {
                    routineViewModel.addNewUserData(userModel)
                }

                val intent = Intent(this, WelcomeActivity::class.java)
                intent.putExtra("name", name)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "make sure you entered correct information",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        tvHaveAnAccountUp.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}