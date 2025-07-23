package com.edustack.edustack.ui.Teacher.Attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.edustack.edustack.databinding.FragmentTeacherAttendanceBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.edustack.edustack.adapters.AttendanceAdapter
import com.edustack.edustack.model.AttendanceItem
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.app.AlertDialog
import com.edustack.edustack.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.edustack.edustack.model.StudentAccount
import com.edustack.edustack.model.StudentInfo
import android.widget.EditText
import android.widget.Button
import android.widget.LinearLayout
import com.edustack.edustack.R
import android.widget.ImageButton
import android.app.DatePickerDialog
import java.util.Calendar

class AttendanceFragment : Fragment() {

    private var _binding: FragmentTeacherAttendanceBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val studentRepository = StudentRepository()
    private var allAttendance: List<AttendanceItem> = emptyList()
    private var currentFilterDate: String = ""
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFilterBar()
        loadAttendanceFromFirestore()
    }

    private fun setupFilterBar() {
        val dateText = binding.root.findViewById<TextView>(R.id.dateFilterTextView)
        val calendarBtn = binding.root.findViewById<ImageButton>(R.id.dateFilterCalendarButton)
        val filterBtn = binding.root.findViewById<Button>(R.id.dateFilterButton)
        val clearBtn = binding.root.findViewById<Button>(R.id.dateClearFilterButton)

        calendarBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val dialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val m = (month + 1).toString().padStart(2, '0')
                val d = dayOfMonth.toString().padStart(2, '0')
                selectedDate = "$year-$m-$d"
                dateText.text = selectedDate
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            dialog.show()
        }
        filterBtn.setOnClickListener {
            currentFilterDate = selectedDate
            updateRecyclerViewWithFilter()
        }
        clearBtn.setOnClickListener {
            selectedDate = ""
            currentFilterDate = ""
            dateText.text = "Select date"
            updateRecyclerViewWithFilter()
        }
    }

    // Commented out dummy data usage
    // private fun setupAttendanceList() {
    //     val dummyAttendance = listOf(
    //         AttendanceItem(
    //             calendarId = "1",
    //             courseId = "COURSE001",
    //             studentId = "STU001",
    //             date = "2024-01-15",
    //             status = "Present",
    //             timestamp = 1705276800000
    //         ),
    //         AttendanceItem(
    //             calendarId = "2",
    //             courseId = "COURSE001",
    //             studentId = "STU002",
    //             date = "2024-01-15",
    //             status = "Absent",
    //             timestamp = 1705276800000
    //         ),
    //         AttendanceItem(
    //             calendarId = "3",
    //             courseId = "COURSE001",
    //             studentId = "STU003",
    //             date = "2024-01-15",
    //             status = "Late",
    //             timestamp = 1705276800000
    //         )
    //     )
    //     val adapter = AttendanceAdapter(dummyAttendance)
    //     binding.attendanceRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    //     binding.attendanceRecyclerView.adapter = adapter
    // }

    private fun loadAttendanceFromFirestore() {
        // val adapter = AttendanceAdapter(emptyList())
        // binding.attendanceRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // binding.attendanceRecyclerView.adapter = adapter

        lifecycleScope.launch {
            try {
                val snapshot = firestore.collection("Attendance").get().await()
                // Commented out old mapping
                // val attendanceList = snapshot.documents.mapNotNull { doc ->
                //     val timestamp = doc.getLong("timestamp") ?: 0L
                //     AttendanceItem(
                //         calendarId = doc.getString("calenderID") ?: "",
                //         courseId = doc.getString("courseID") ?: "",
                //         studentId = doc.getString("studentID") ?: "",
                //         date = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date(timestamp)),
                //         status = doc.getString("status") ?: "",
                //         timestamp = timestamp
                //     )
                // }
                val courseMap = mutableMapOf<String, String>()
                val studentMap = mutableMapOf<String, String>()
                // Preload all courses
                val courses = firestore.collection("Cources").get().await()
                for (c in courses.documents) {
                    courseMap[c.id] = c.getString("Name") ?: ""
                }
                // Preload all students
                val students = firestore.collection("StudentInfo").get().await()
                for (s in students.documents) {
                    studentMap[s.id] = (s.getString("Fname") ?: "") + " " + (s.getString("Lname") ?: "")
                }
                val studentAccMap = mutableMapOf<String, String>()
                val studentAccs = firestore.collection("StudentAcc").get().await()
                for (acc in studentAccs.documents) {
                    studentAccMap[acc.id] = acc.getString("StudentInfoID") ?: ""
                }
                val attendanceList = snapshot.documents.mapNotNull { doc ->
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    val studentId = doc.getString("studentID") ?: ""
                    val courseId = doc.getString("courseID") ?: ""
                    val studentInfoId = studentAccMap[studentId] ?: ""
                    AttendanceItem(
                        calendarId = doc.getString("calenderID") ?: "",
                        courseId = courseId,
                        studentId = studentId,
                        date = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date(timestamp)),
                        status = doc.getString("status") ?: "",
                        timestamp = timestamp,
                        studentName = studentMap[studentInfoId] ?: "",
                        courseName = courseMap[courseId] ?: ""
                    )
                }
                allAttendance = attendanceList
                updateRecyclerViewWithFilter()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateRecyclerViewWithFilter() {
        val filtered = if (currentFilterDate.isNotEmpty()) {
            allAttendance.filter { it.date == currentFilterDate }
        } else {
            allAttendance
        }
        val adapter = AttendanceAdapter(filtered) { attendanceItem ->
            showStudentInfoDialog(attendanceItem.studentId)
        }
        binding.attendanceRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.attendanceRecyclerView.adapter = adapter
    }

    private fun showStudentInfoDialog(studentId: String) {
        lifecycleScope.launch {
            val studentAccount = withContext(Dispatchers.IO) {
                val doc = firestore.collection("StudentAcc").document(studentId).get().await()
                if (doc.exists()) {
                    val statusValue = doc.get("Status")
                    val statusString = when (statusValue) {
                        is String -> statusValue
                        is Boolean -> statusValue.toString()
                        else -> ""
                    }
                    StudentAccount(
                        password = doc.getString("Password") ?: "",
                        status = statusString,
                        studentID = doc.id,
                        studentInfoID = doc.getString("StudentInfoID") ?: ""
                    )
                } else null
            }
            val studentInfo = studentAccount?.studentInfoID?.let { infoId ->
                withContext(Dispatchers.IO) {
                    val doc = firestore.collection("StudentInfo").document(infoId).get().await()
                    if (doc.exists()) {
                        StudentInfo(
                            address = doc.getString("Address") ?: "",
                            city = doc.getString("City") ?: "",
                            contactNumber = doc.getString("ContactNumber") ?: "",
                            dob = doc.getTimestamp("DOB")?.seconds?.times(1000) ?: 0L,
                            email = doc.getString("Email") ?: "",
                            fname = doc.getString("Fname") ?: "",
                            gender = doc.getString("Gender") ?: "",
                            joinedDate = doc.getTimestamp("JoinedDate")?.seconds?.times(1000) ?: 0L,
                            lname = doc.getString("Lname") ?: "",
                            school = doc.getString("School") ?: ""
                        )
                    } else null
                }
            }
            if (studentAccount != null && studentInfo != null) {
                val info = studentInfo
                val profileText = """
                    Full Name: ${info.fname} ${info.lname}
                    Email: ${info.email}
                    Phone: ${info.contactNumber}
                    Address: ${info.address}
                    City: ${info.city}
                    School: ${info.school}
                    Gender: ${info.gender}
                    Date of Birth: ${formatDate(info.dob)}
                    Joined Date: ${formatDate(info.joinedDate)}
                    Student ID: ${studentAccount.studentID}
                    Status: ${studentAccount.status}
                """.trimIndent()
                AlertDialog.Builder(requireContext())
                    .setTitle("Student Information")
                    .setMessage(profileText)
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Student Information")
                    .setMessage("Student info not found.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }

    private fun formatDate(millis: Long): String {
        return if (millis > 0) java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(millis)) else "-"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}