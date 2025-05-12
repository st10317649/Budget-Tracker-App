package com.example.expenseiq

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.expenseiq.databinding.ActivityCreateExpensesBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Locale

class CreateExpensesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateExpensesBinding
    private lateinit var dbHelper: DatabaseHelper
    private var selectedImageUri: Uri? = null
    private lateinit var username: String

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult

            // Persist URI permission (optional but good practice)
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            // Define the base directory for expense images
            val expenseImagesDir = File(filesDir, "expense_images")

            // Check if the directory exists; if not, create it
            if (!expenseImagesDir.exists()) {
                val isCreated = expenseImagesDir.mkdirs() // Create directory
                if (isCreated) {
                    Log.d("Directory", "Directory created successfully.")
                } else {
                    Log.e("Directory", "Failed to create directory.")
                }
            }

            // Create the image file inside the directory
            val imageFile = File(expenseImagesDir, "receipt_${System.currentTimeMillis()}.jpg")

            try {
                // Open input stream from the URI
                val inputStream = contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(imageFile)

                // Copy data from input stream to output stream (save image)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                // Update the image URI for display
                selectedImageUri = Uri.fromFile(imageFile)
                binding.expenseImageView.setImageURI(selectedImageUri)

                Log.d("ImagePath", "Image saved at: ${imageFile.absolutePath}")
            } catch (e: IOException) {
                Log.e("ImageError", "Error saving image", e)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

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
            finish()
        }

        // Load categories
        val categories = dbHelper.getCategoryNamesForUser(userId)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter

        // Date picker
        binding.dateEt.setOnClickListener {
            showDatePicker()
        }

        // Time pickers
        binding.startTimeEt.setOnClickListener {
            showTimePicker { time -> binding.startTimeEt.setText(time) }
        }

        binding.endTimeEt.setOnClickListener {
            showTimePicker { time -> binding.endTimeEt.setText(time) }
        }

        // Image picker button
        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            pickImageLauncher.launch(intent)
        }

        // Save expense
        binding.saveExpenseButton.setOnClickListener {
            val name = binding.expenseNameEt.text.toString().trim()
            val amountText = binding.amountEt.text.toString().trim()
            val description = binding.descriptionEt.text.toString().trim()
            val date = binding.dateEt.text.toString().trim()
            val startTime = binding.startTimeEt.text.toString().trim()
            val endTime = binding.endTimeEt.text.toString().trim()
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

            val success = dbHelper.insertExpense(
                userId = userId,
                amount = amount,
                categoryId = categoryId,
                date = date,
                description = description,
                expenseName = name,
                imageUri = selectedImageUri?.path // store local path
            )

            if (success > 0) {
                Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                val dateString = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                binding.dateEt.setText(dateString)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(this,
            { _, hourOfDay, minute ->
                val timeString = String.format("%02d:%02d", hourOfDay, minute)
                onTimeSelected(timeString)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }
}