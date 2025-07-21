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

class AttendanceAdapter(
    private var attendanceList: List<AttendanceRecord>,
    private val onStatusClick: (AttendanceRecord, Int) -> Unit
) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.tv_student_name)
        val studentId: TextView = itemView.findViewById(R.id.tv_student_id)
        val statusButton: TextView = itemView.findViewById(R.id.btn_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val attendance = attendanceList[position]

        holder.studentName.text = attendance.studentName
        holder.studentId.text = attendance.studentId
        holder.statusButton.text = attendance.status.uppercase()

        when (attendance.status.lowercase()) {
            "present" -> holder.statusButton.setBackgroundResource(R.drawable.bg_present)
            "absent" -> holder.statusButton.setBackgroundResource(R.drawable.bg_absent)
            else -> holder.statusButton.setBackgroundResource(R.drawable.bg_default)
        }

        holder.statusButton.setOnClickListener {
            onStatusClick(attendance, position)
        }
    }

    override fun getItemCount(): Int = attendanceList.size

    fun updateAttendance(newList: List<AttendanceRecord>) {
        attendanceList = newList
        notifyDataSetChanged()
    }
}
