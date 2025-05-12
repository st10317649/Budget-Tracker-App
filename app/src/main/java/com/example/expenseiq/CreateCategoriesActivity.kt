package com.example.expenseiq

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.expenseiq.databinding.ActivityCreateCategoriesBinding


class CreateCategoriesActivity : AppCompatActivity() {

    //Declare variables
    private lateinit var databaseHelper: DatabaseHelper
    private val expenseCategories = mutableListOf<String>()
    private val budgetCategories = mutableListOf<String>()
    private lateinit var binding : ActivityCreateCategoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialise databaseHelper
        databaseHelper = DatabaseHelper(this)

        //Hide button next
        binding.nextButton.visibility = View.GONE

        //Handle button  add expense click event
        binding.addExpenseButton.setOnClickListener {
            val text = binding.expenseCategoriesEt.text.toString().trim()
            if (text.isNotEmpty()) {
                expenseCategories.add(text)
                binding.expenseCategoriesEt.text.clear()
                checkCategories(binding.nextButton)
            }
        }

        //Handle button add budget click event
        binding.addBudgetButton.setOnClickListener {
            val text = binding.budgetCategoriesEt.text.toString().trim()
            if (text.isNotEmpty()) {
                budgetCategories.add(text)
                binding.budgetCategoriesEt.text.clear()
                checkCategories(binding.nextButton)
            }
        }

        //Handle next button click event
        binding.nextButton.setOnClickListener {
            val userId = getCurrentUserId() // You must implement this
            expenseCategories.forEach {
                databaseHelper.insertCategory(userId, it, "expense")
            }
            budgetCategories.forEach {
                databaseHelper.insertCategory(userId, it, "budget")
            }

            // Proceed to next screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    //Check if the database has more than 3 categories of each ,if true ,show bytton next
    private fun checkCategories(nextButton: Button) {
        nextButton.visibility =
            if (expenseCategories.size >= 3 && budgetCategories.size >= 3) View.VISIBLE else View.GONE
    }

    private fun getCurrentUserId(): Int {
        // Retrieve logged-in user ID from SharedPreferences or session
        return 1 // Placeholder
    }
}
