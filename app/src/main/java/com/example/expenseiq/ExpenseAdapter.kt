package com.example.expenseiq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import com.bumptech.glide.Glide

class ExpenseAdapter(private val expenses: List<ExpenseItem>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val expenseName: TextView = itemView.findViewById(R.id.valueExpenseName)
        val category: TextView = itemView.findViewById(R.id.valueCategory)
        val amount: TextView = itemView.findViewById(R.id.valueAmount)
        val image: ImageView = itemView.findViewById(R.id.expenseImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.expenseName.text = expense.expenseName
        holder.category.text = expense.category
        holder.amount.text = "R ${expense.amount}"

        if (!expense.imageUri.isNullOrEmpty()) {
            val imageFile = File(expense.imageUri)
            Glide.with(holder.itemView.context)
                .load(imageFile)
                .into(holder.image) // Make sure your ViewHolder has this ImageView
        } else {
            holder.image.setImageResource(R.drawable.baseline_person_24)
        }
    }

    override fun getItemCount(): Int = expenses.size
}
