package com.example.expenseiq

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.expenseiq.databinding.ActivityMainBinding
import com.example.expenseiq.fragments.CategoriesFragment
import com.example.expenseiq.fragments.ExpensesFragment
import com.example.expenseiq.fragments.HomeFragment
import com.example.expenseiq.fragments.IncomesFragment
import com.example.expenseiq.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set initial fragment
        replaceFragment(HomeFragment())

        // Handle bottom nav item clicks
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeMn -> replaceFragment(HomeFragment())
                R.id.expensesMn -> replaceFragment(ExpensesFragment())
                R.id.incomesMn -> replaceFragment(IncomesFragment())
                R.id.categoriesMn -> replaceFragment(CategoriesFragment())
                R.id.settingsMn -> replaceFragment(SettingsFragment())
                else -> return@setOnItemSelectedListener false
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
