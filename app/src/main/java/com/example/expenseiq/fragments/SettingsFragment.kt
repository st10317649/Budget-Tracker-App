package com.example.expenseiq.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.expenseiq.DatabaseHelper
import com.example.expenseiq.R
import com.example.expenseiq.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        dbHelper = DatabaseHelper(requireContext())
        sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener {
            val minGoal = binding.minGoalEditText.text.toString().toDoubleOrNull()
            val maxGoal = binding.maxGoalEditText.text.toString().toDoubleOrNull()

            if (minGoal == null || maxGoal == null) {
                Toast.makeText(requireContext(), "Please enter valid goals", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val username = sharedPreferences.getString("username", null)
            if (username != null) {
                val userId = dbHelper.getUserId(username)
                if (userId != null) {
                    dbHelper.saveSpendingGoals(userId, minGoal, maxGoal)
                    Toast.makeText(requireContext(), "Goals saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
