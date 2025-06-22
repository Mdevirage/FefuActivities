package ru.fefu.activitiesfefu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.fefu.activitiesfefu.databinding.ActivityLoginBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            val email = binding.etLogin.text.toString()
            val password = binding.etPass.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = (application as ActivityApplication).database
            lifecycleScope.launch {
                val user = db.userDao().getUserByEmail(email)
                if (user == null || user.password != password) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Неверный email или пароль",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Сохраняем userId в SharedPreferences
                    getSharedPreferences("app_prefs", MODE_PRIVATE)
                        .edit().putInt("user_id", user.id).apply()
                    // Переход на главный экран
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}
