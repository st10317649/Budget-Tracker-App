package com.example.expenseiq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IncomeAdapter(private val incomes: List<IncomeItem>) : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.valueIncomeName)
        val category: TextView = itemView.findViewById(R.id.valueIncomeCategory)
        val amount: TextView = itemView.findViewById(R.id.valueIncomeAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.income_item, parent, false)
        return IncomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val income = incomes[position]
        holder.name.text = income.incomeName
        holder.category.text = income.incomeCategory
        holder.amount.text = "R ${income.incomeAmount}"
    }

    override fun getItemCount(): Int = incomes.size
}