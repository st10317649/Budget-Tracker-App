package com.example.expenseiq

import android.R
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.expenseiq.databinding.ActivityCreateIncomeBinding
import java.util.Calendar
import java.util.Locale

class CreateIncomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateIncomeBinding
    private lateinit var dbHelper: DatabaseHelper
    private var selectedImageUri: Uri? = null
    private lateinit var username: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // Get username from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username", null) ?: run {
            Toast.makeText(this, "Please log in again.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userId = dbHelper.getUserId(username) ?: run {
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        binding.backButton.setOnClickListener {
            finish() // closes this activity and returns to previous screen
        }


        // Populate category spinner
        val categories = dbHelper.getCategoryNamesForUser(userId)
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter

        // Date picker
        binding.dateEt.setOnClickListener {
            showDatePicker()
        }



        // Save expense
        binding.saveIncomeButton.setOnClickListener {
            val name = binding.incomeNameEt.text.toString().trim()
            val amountText = binding.incomeAmountEt.text.toString().trim()
            val description = binding.incomeDescriptionEt.text.toString().trim()
            val date = binding.dateEt.text.toString().trim()
            val categoryName = binding.categorySpinner.selectedItem.toString()

            if (name.isBlank() || amountText.isBlank() || date.isBlank()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoryId = dbHelper.getCategoryIdForName(categoryName, userId)
            if (categoryId == null) {
                Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateNow = date

            val success = dbHelper.insertIncome(
                userId = userId,
                amount = amount,
                categoryId = categoryId,
                date = dateNow,
                description = description,
                incomeName = name

            )

            if (success > 0) {
                Toast.makeText(this, "Income saved!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            // Format date to yyyy-MM-dd
            val dateStr = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
            binding.dateEt.setText(dateStr)
        }, year, month, day).show()
    }

}