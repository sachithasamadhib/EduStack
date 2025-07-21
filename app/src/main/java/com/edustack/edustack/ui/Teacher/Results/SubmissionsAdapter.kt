package com.edustack.edustack.ui.Teacher.Results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.R
import java.text.SimpleDateFormat
import java.util.*

class SubmissionsAdapter(
    private val onItemClick: (SubmissionData) -> Unit
) : RecyclerView.Adapter<SubmissionsAdapter.SubmissionViewHolder>() {

    private var submissions = listOf<SubmissionData>()

    fun updateSubmissions(newSubmissions: List<SubmissionData>) {
        submissions = newSubmissions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmissionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_submission, parent, false)
        return SubmissionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubmissionViewHolder, position: Int) {
        holder.bind(submissions[position])
    }

    override fun getItemCount() = submissions.size

    inner class SubmissionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textAssignmentTitle: TextView = itemView.findViewById(R.id.textAssignmentTitle)
        private val textStudentName: TextView = itemView.findViewById(R.id.textStudentName)
        private val textMarks: TextView = itemView.findViewById(R.id.textMarks)
        private val textSubmissionDate: TextView = itemView.findViewById(R.id.textSubmissionDate)
        private val textCourseName: TextView? = itemView.findViewById(R.id.textCourseName) // Optional if you add this to layout

        fun bind(submission: SubmissionData) {
            textAssignmentTitle.text = "${submission.assignmentTitle} (${submission.courseName})"
            textStudentName.text = submission.studentName
            textMarks.text = "Marks: ${submission.marks}"

            textCourseName?.text = "Course: ${submission.courseName}"

            submission.date?.let { timestamp ->
                val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                textSubmissionDate.text = sdf.format(timestamp.toDate())
            } ?: run {
                textSubmissionDate.text = "No date"
            }

            itemView.setOnClickListener {
                onItemClick(submission)
            }
        }
    }
}
