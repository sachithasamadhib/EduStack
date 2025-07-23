package com.edustack.edustack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.databinding.ItemAttendanceBinding
import com.edustack.edustack.model.AttendanceItem
import java.text.SimpleDateFormat
import java.util.Locale

class AttendanceAdapter(
    private var attendanceList: List<AttendanceItem>,
    private val onItemClick: ((AttendanceItem) -> Unit)? = null // New callback
) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    class AttendanceViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(attendance: AttendanceItem, onItemClick: ((AttendanceItem) -> Unit)?) {
            binding.studentNameText.text = attendance.studentName
            binding.courseNameText.text = attendance.courseName
            binding.dateText.text = formatDate(attendance.date)
            binding.statusText.text = attendance.status

            // Set status color based on attendance status
            when (attendance.status.lowercase()) {
                "present" -> binding.statusText.setTextColor(0xFF4CAF50.toInt()) // Green
                "absent" -> binding.statusText.setTextColor(0xFFF44336.toInt()) // Red
                "late" -> binding.statusText.setTextColor(0xFFFF9800.toInt()) // Orange
            }

            binding.root.setOnClickListener {
                onItemClick?.invoke(attendance)
            }
        }

        // Old bind method for reference (commented out)
        // fun bind(attendance: AttendanceItem) {
        //     binding.dateText.text = formatDate(attendance.date)
        //     binding.statusText.text = attendance.status
        //     // Set status color based on attendance status
        //     when (attendance.status.lowercase()) {
        //         "present" -> binding.statusText.setTextColor(0xFF4CAF50.toInt()) // Green
        //         "absent" -> binding.statusText.setTextColor(0xFFF44336.toInt()) // Red
        //         "late" -> binding.statusText.setTextColor(0xFFFF9800.toInt()) // Orange
        //     }
        // }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                dateString
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemAttendanceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(attendanceList[position], onItemClick)
    }

    override fun getItemCount(): Int = attendanceList.size

    fun updateData(newList: List<AttendanceItem>) {
        attendanceList = newList
        notifyDataSetChanged()
    }

    // Old constructor for reference (commented out)
    // class AttendanceAdapter(private var attendanceList: List<AttendanceItem>) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() { ... }
}