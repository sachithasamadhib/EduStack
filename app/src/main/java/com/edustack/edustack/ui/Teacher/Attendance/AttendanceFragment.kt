package com.edustack.edustack.ui.Teacher.Attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.R
import com.edustack.edustack.databinding.FragmentTeacherAttendanceBinding

class AttendanceFragment : Fragment() {

    private var _binding: FragmentTeacherAttendanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var attendanceViewModel: AttendanceViewModel
    private lateinit var studentAttendanceAdapter: StudentAttendanceAdapter
    private lateinit var courseSpinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var dateText: TextView
    private lateinit var courseText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        attendanceViewModel = ViewModelProvider(this)[AttendanceViewModel::class.java]

        // Use the appropriate layout file name
        _binding = FragmentTeacherAttendanceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initViews()
        setupRecyclerView()
        setupSpinner()
        observeViewModel()

        return root
    }

    private fun initViews() {
        courseSpinner = binding.spinnerCourse
        recyclerView = binding.recyclerViewAttendance
        progressBar = binding.progressBar
        emptyView = binding.tvEmptyView
        dateText = binding.tvDate
        courseText = binding.tvSelectedCourse

        dateText.text = attendanceViewModel.getTodayDateString()
    }

    private fun setupRecyclerView() {
        studentAttendanceAdapter = StudentAttendanceAdapter(emptyList()) { record, position ->
            showStatusDialog(record)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = studentAttendanceAdapter
        }
    }

    private fun setupSpinner() {
        attendanceViewModel.courses.observe(viewLifecycleOwner) { courses ->
            val courseNames = listOf("Select Course") + courses.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                courseNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            courseSpinner.adapter = adapter

            courseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position > 0) {
                        val selectedCourse = courses[position - 1]
                        attendanceViewModel.selectCourse(selectedCourse)
                        courseText.text = "Course: ${selectedCourse.name}"
                    } else {
                        courseText.text = "No course selected"
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun observeViewModel() {
        attendanceViewModel.attendanceList.observe(viewLifecycleOwner) { attendanceList ->
            if (attendanceList.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                emptyView.text = if (attendanceViewModel.selectedCourse.value != null) {
                    "No students enrolled in this course or no class scheduled for today"
                } else {
                    "Please select a course to view attendance"
                }
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                studentAttendanceAdapter.updateAttendance(attendanceList)
            }
        }

        attendanceViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        attendanceViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showStatusDialog(record: AttendanceRecord) {
        val options = arrayOf("Present", "Absent", "Late")
        val currentStatus = record.status.lowercase()
        val checkedItem = when (currentStatus) {
            "present" -> 0
            "absent" -> 1
            "late" -> 2
            else -> 1
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Update Attendance for ${record.studentName}")
            .setSingleChoiceItems(options, checkedItem) { dialog, which ->
                val newStatus = options[which].lowercase()
                attendanceViewModel.updateAttendanceStatus(record, newStatus)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
