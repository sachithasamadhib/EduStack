package com.edustack.edustack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.databinding.ItemAssignmentBinding
import com.edustack.edustack.model.Assignment
import java.text.SimpleDateFormat
import java.util.*

class AssignmentsAdapter(
    private val assignmentsList: List<Assignment>,
    private val onAssignmentAction: (Assignment, String) -> Unit
) : RecyclerView.Adapter<AssignmentsAdapter.AssignmentsViewHolder>() {

    class AssignmentsViewHolder(
        private val binding: ItemAssignmentBinding,
        private val onAssignmentAction: (Assignment, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(assignment: Assignment) {
            binding.assignmentTitle.text = assignment.title
            binding.assignmentDescription.text = assignment.description
            binding.handOutDate.text = "Hand Out: ${formatDate(assignment.handOutDate)}"
            binding.handInDate.text = "Due Date: ${formatDate(assignment.handInDate)}"

            // Check if assignment is overdue
            val currentTime = System.currentTimeMillis()
            val isOverdue = currentTime > assignment.handInDate

            if (isOverdue) {
                binding.statusText.text = "OVERDUE"
                binding.statusText.setTextColor(0xFFF44336.toInt()) // Red
                binding.submitButton.isEnabled = false
                binding.submitButton.text = "Overdue"
            } else {
                binding.statusText.text = "ACTIVE"
                binding.statusText.setTextColor(0xFF4CAF50.toInt()) // Green
                binding.submitButton.isEnabled = true
                binding.submitButton.text = "Submit"
            }

            // Set click listeners
            binding.downloadButton.setOnClickListener {
                onAssignmentAction(assignment, "download")
            }

            binding.submitButton.setOnClickListener {
                onAssignmentAction(assignment, "submit")
            }
        }

        private fun formatDate(timestamp: Long): String {
            return try {
                val date = Date(timestamp)
                val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                format.format(date)
            } catch (e: Exception) {
                "Unknown Date"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentsViewHolder {
        val binding = ItemAssignmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AssignmentsViewHolder(binding, onAssignmentAction)
    }

    override fun onBindViewHolder(holder: AssignmentsViewHolder, position: Int) {
        holder.bind(assignmentsList[position])
    }

    override fun getItemCount(): Int = assignmentsList.size
}