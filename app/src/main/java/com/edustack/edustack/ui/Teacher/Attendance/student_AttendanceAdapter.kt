package com.edustack.edustack.ui.Teacher.Attendance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.R

data class AttendanceRecord(
    val studentName: String,
    val studentId: String,
    val status: String,
    val courseId: String,
    val calendarId: String,
    val timestamp: Long
)

class StudentAttendanceAdapter(
    private var attendanceList: List<AttendanceRecord>,
    private val onStatusClick: (AttendanceRecord, Int) -> Unit
) : RecyclerView.Adapter<StudentAttendanceAdapter.AttendanceViewHolder>() {

    class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.tv_student_name)
        val studentId: TextView = itemView.findViewById(R.id.tv_student_id)
        val statusButton: TextView = itemView.findViewById(R.id.btn_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_item_attendance, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val attendance = attendanceList[position]

        holder.studentName.text = attendance.studentName
        holder.studentId.text = "ID: ${attendance.studentId}"
        holder.statusButton.text = attendance.status.uppercase()

        // Set background and text color based on status
        val context = holder.itemView.context
        when (attendance.status.lowercase()) {
            "present" -> {
                try {
                    holder.statusButton.setBackgroundResource(R.drawable.bg_present)
                } catch (e: Exception) {
                    holder.statusButton.setBackgroundColor(0xFF4CAF50.toInt()) // Green
                }
                holder.statusButton.setTextColor(0xFFFFFFFF.toInt()) // White
            }
            "absent" -> {
                try {
                    holder.statusButton.setBackgroundResource(R.drawable.bg_absent)
                } catch (e: Exception) {
                    holder.statusButton.setBackgroundColor(0xFFF44336.toInt()) // Red
                }
                holder.statusButton.setTextColor(0xFFFFFFFF.toInt()) // White
            }
            "late" -> {
                try {
                    holder.statusButton.setBackgroundResource(R.drawable.bg_late)
                } catch (e: Exception) {
                    holder.statusButton.setBackgroundColor(0xFFFF9800.toInt()) // Orange
                }
                holder.statusButton.setTextColor(0xFFFFFFFF.toInt()) // White
            }
            else -> {
                try {
                    holder.statusButton.setBackgroundResource(R.drawable.bg_default)
                } catch (e: Exception) {
                    holder.statusButton.setBackgroundColor(0xFF9E9E9E.toInt()) // Gray
                }
                holder.statusButton.setTextColor(0xFFFFFFFF.toInt()) // White
            }
        }

        holder.statusButton.setOnClickListener {
            onStatusClick(attendance, position)
        }

        // Add click listener to the entire item
        holder.itemView.setOnClickListener {
            onStatusClick(attendance, position)
        }
    }

    override fun getItemCount(): Int = attendanceList.size

    fun updateAttendance(newList: List<AttendanceRecord>) {
        attendanceList = newList
        notifyDataSetChanged()
    }
}
