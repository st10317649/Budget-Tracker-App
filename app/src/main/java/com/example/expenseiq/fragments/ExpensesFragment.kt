package com.example.expenseiq.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expenseiq.CreateExpensesActivity
import com.example.expenseiq.DatabaseHelper
import com.example.expenseiq.ExpenseAdapter
import com.example.expenseiq.databinding.FragmentExpensesBinding
import java.util.Calendar

class ExpensesFragment : Fragment() {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ExpenseAdapter

    private var startDate: String = ""
    private var endDate: String = ""
    private var userId: Int? = null  // Moved to class level

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize dbHelper once at the beginning
        dbHelper = DatabaseHelper(requireContext())

        //Get current user session
        val sharedPreferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        dbHelper.logAllExpenses()

        if (username != null) {
            userId = dbHelper.getUserId(username)

            if (userId != null) {
                val expenseList = dbHelper.getExpensesForUser(userId!!)

                val totalAmount = expenseList.sumOf { it.amount }
                binding.totalAmtTv.text = "R %.2f".format(totalAmount)

                if (expenseList.isNotEmpty()) {
                    adapter = ExpenseAdapter(expenseList)
                    binding.expensesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.expensesRecyclerView.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "No expenses found.", Toast.LENGTH_SHORT).show()
                }

                binding.redirectButton.setOnClickListener {
                    startActivity(Intent(requireContext(), CreateExpensesActivity::class.java))
                }

            } else {
                Toast.makeText(requireContext(), "User not found. Please log in again.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Please log in again", Toast.LENGTH_SHORT).show()
        }

        // Start Date Picker
        binding.startDateButton.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                binding.startDateButton.text = "Start: $date"
            }
        }

        // End Date Picker
        binding.endDateButton.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                binding.endDateButton.text = "End: $date"
            }
        }

        //Filter Button
        binding.filterButton.setOnClickListener {
            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                userId?.let { id ->
                    val expenses = dbHelper.getExpensesForUserInRange(id, startDate, endDate)
                    val total = expenses.sumOf { it.amount }
                    binding.totalAmtTv.text = "R %.2f".format(total)

                    adapter = ExpenseAdapter(expenses)
                    binding.expensesRecyclerView.adapter = adapter
                } ?: Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Select both dates", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
