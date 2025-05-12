package com.example.expenseiq.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expenseiq.CreateIncomeActivity
import com.example.expenseiq.DatabaseHelper
import com.example.expenseiq.IncomeAdapter
import com.example.expenseiq.R
import com.example.expenseiq.databinding.FragmentIncomesBinding


class IncomesFragment : Fragment() {
    private var _binding: FragmentIncomesBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: IncomeAdapter // Adapter for IncomeItem list

    private var userId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        // Get username from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        // Log all incomes (Optional Debug)
        dbHelper.logAllIncomes()

        if (username != null) {
            userId = dbHelper.getUserId(username)

            if (userId != null) {
                // Fetch incomes for the user
                val incomeList = dbHelper.getIncomesForUser(userId!!)

                // Log the list size
                Log.d("DB_DEBUG", "Income list size: ${incomeList.size}")

                // Set the total amount text
                val totalAmount = incomeList.sumOf { it.incomeAmount }
                binding.totalAmtTv.text = "R %.2f".format(totalAmount)

                // Set up RecyclerView and Adapter
                if (incomeList.isNotEmpty()) {
                    adapter = IncomeAdapter(incomeList)
                    binding.incomesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.incomesRecyclerView.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "No incomes found.", Toast.LENGTH_SHORT).show()
                }

                // Set click listener for the redirect button
                binding.redirectButton.setOnClickListener {
                    startActivity(Intent(requireContext(), CreateIncomeActivity::class.java))
                }
            } else {
                Toast.makeText(requireContext(), "User not found. Please log in again.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Please log in again", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}