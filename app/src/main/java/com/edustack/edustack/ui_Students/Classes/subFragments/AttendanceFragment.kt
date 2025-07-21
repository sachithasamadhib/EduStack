package com.edustack.edustack.ui_Students.Classes.subFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.edustack.edustack.adapters.AttendanceAdapter
import com.edustack.edustack.databinding.FragmentClassAttendanceBinding
import com.edustack.edustack.model.AttendanceItem

class AttendanceFragment : Fragment() {

    private var _binding: FragmentClassAttendanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var courseId: String
    private lateinit var courseName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get arguments passed from CourseDetailFragment
        courseId = arguments?.getString("courseId") ?: ""
        courseName = arguments?.getString("courseName") ?: "Course"

        // Set the course title
        binding.courseTitle.text = "$courseName - Attendance"

        // Load attendance data
        loadAttendanceData()
    }

    private fun loadAttendanceData() {
        // TODO: Replace with Firebase data
        val dummyAttendanceData = getDummyAttendanceData()

        // Create and set adapter
        val adapter = AttendanceAdapter(dummyAttendanceData)
        binding.attendanceRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.attendanceRecyclerView.adapter = adapter
    }

    private fun getDummyAttendanceData(): List<AttendanceItem> {
        return listOf(
            AttendanceItem(
                calendarId = "1",
                courseId = courseId,
                studentId = "1",
                date = "2024-01-15",
                status = "Present",
                timestamp = 1705276800000
            ),
            AttendanceItem(
                calendarId = "2",
                courseId = courseId,
                studentId = "1",
                date = "2024-01-16",
                status = "Absent",
                timestamp = 1705363200000
            ),
            AttendanceItem(
                calendarId = "3",
                courseId = courseId,
                studentId = "1",
                date = "2024-01-17",
                status = "Present",
                timestamp = 1705449600000
            ),
            AttendanceItem(
                calendarId = "4",
                courseId = courseId,
                studentId = "1",
                date = "2024-01-18",
                status = "Late",
                timestamp = 1705536000000
            ),
            AttendanceItem(
                calendarId = "5",
                courseId = courseId,
                studentId = "1",
                date = "2024-01-19",
                status = "Present",
                timestamp = 1705622400000
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}