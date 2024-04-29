package com.bhavesh.ticktask.ui.loginScreen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bhavesh.ticktask.R
import com.bhavesh.ticktask.data.model.UserModel
import com.bhavesh.ticktask.data.repository.RoutineRepository
import com.bhavesh.ticktask.data.roomDB.RoutineDAO
import com.bhavesh.ticktask.data.roomDB.RoutineRoomDB
import com.bhavesh.ticktask.databinding.ActivityRegisterBinding
import com.bhavesh.ticktask.ui.view.WelcomeActivity
import com.bhavesh.ticktask.viewModel.RoutineViewModel
import com.bhavesh.ticktask.viewModel.RoutineViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    private lateinit var routineRoomDB: RoutineRoomDB
    private lateinit var routineDAO: RoutineDAO
    private lateinit var routineViewModel: RoutineViewModel
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        routineDAO = RoutineRoomDB.getDatabaseObject(this).getRoutineDAO()
        val routineRepository = RoutineRepository(routineDAO)
        val routineViewModelFactory = RoutineViewModelFactory(routineRepository)
        routineViewModel =
            ViewModelProviders.of(this, routineViewModelFactory).get(RoutineViewModel::class.java)


        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val name = binding.etNameUp.text.toString()
            val email = binding.etEmailSignUp.text.toString()
            val number = binding.etNumberUp.text.toString()
            val passwd = binding.etPasswdUp.text.toString()

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

        binding.tvHaveAnAccountUp.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}