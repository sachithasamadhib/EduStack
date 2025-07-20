package com.edustack.edustack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.databinding.ItemResultBinding
import com.edustack.edustack.model.ResultsItem
import java.text.SimpleDateFormat
import java.util.Locale

class ResultsAdapter(
    private val resultsList: List<ResultsItem>
) : RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>() {

    class ResultsViewHolder(private val binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: ResultsItem) {
            binding.assignmentIdText.text = "Assignment: ${result.assignmentID}"
            binding.dateText.text = formatDate(result.date)
            binding.marksText.text = "${result.marks}/100"

            // Set marks color based on performance
            val marks = result.marks.toIntOrNull() ?: 0
            when {
                marks >= 90 -> binding.marksText.setTextColor(0xFF4CAF50.toInt()) // Green for A
                marks >= 80 -> binding.marksText.setTextColor(0xFF2196F3.toInt()) // Blue for B
                marks >= 70 -> binding.marksText.setTextColor(0xFFFF9800.toInt()) // Orange for C
                else -> binding.marksText.setTextColor(0xFFF44336.toInt()) // Red for D/F
            }

            // Set grade based on marks
            binding.gradeText.text = getGrade(marks)
            binding.gradeText.setTextColor(binding.marksText.currentTextColor)
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                dateString
            }
        }

        private fun getGrade(marks: Int): String {
            return when {
                marks >= 90 -> "A"
                marks >= 80 -> "B"
                marks >= 70 -> "C"
                marks >= 60 -> "D"
                else -> "F"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val binding = ItemResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ResultsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        holder.bind(resultsList[position])
    }

    override fun getItemCount(): Int = resultsList.size
}