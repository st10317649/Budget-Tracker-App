package com.example.expenseiq

import android.database.Cursor

data class CategoryTotal(val categoryName: String, val totalSpent: Double) {
    companion object {
        fun fromCursor(cursor: Cursor): CategoryTotal {
            val category = cursor.getString(cursor.getColumnIndexOrThrow("category_name"))
            val totalSpent = cursor.getDouble(cursor.getColumnIndexOrThrow("total_spent"))
            return CategoryTotal(category, totalSpent)
        }
    }
}