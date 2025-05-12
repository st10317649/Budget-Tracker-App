package com.example.expenseiq

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.expenseiq.databinding.ActivityLoginBinding
import com.example.expenseiq.databinding.ActivityRegisterBinding
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityLoginBinding

    private  lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        binding.loginButton.setOnClickListener {
            val username = binding.loginUsernameEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()

            loginToDatabase(username,password)

        }
        binding.registerRedirectText.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun loginToDatabase(username: String, password: String) {
        if (databaseHelper.readUserByUsername(username, password)) {
            val userId = databaseHelper.getUserId(username)
            if (userId != null) {
                val categoryCount = databaseHelper.getCategoryCountForUser(userId)
                Toast.makeText(this, "Login Successful!!", Toast.LENGTH_SHORT).show()

                val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                sharedPreferences.edit() {
                    putString("username", username) // Save username
                }

                val intent = if (categoryCount >= 3) {
                    Intent(this, MainActivity::class.java)
                } else {
                    Intent(this, CreateCategoriesActivity::class.java)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Login error: user not found!", Toast.LENGTH_SHORT).show()
            }
        } else if (databaseHelper.checkUserExists(username)) {
            Toast.makeText(this, "Incorrect password!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "User does not exist!", Toast.LENGTH_SHORT).show()
        }
    }
}