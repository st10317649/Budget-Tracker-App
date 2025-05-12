package com.example.expenseiq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val categoryTotals: List<CategoryTotal>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.category_name)
        val totalSpent: TextView = itemView.findViewById(R.id.total_spent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryTotal = categoryTotals[position]
        holder.categoryName.text = categoryTotal.categoryName
        holder.totalSpent.text = "R ${categoryTotal.totalSpent}"
    }

    override fun getItemCount(): Int = categoryTotals.size
}