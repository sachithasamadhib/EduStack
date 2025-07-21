package com.edustack.edustack.ui.Teacher.Attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class AttendanceViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _attendanceList = MutableLiveData<List<AttendanceRecord>>()
    val attendanceList: LiveData<List<AttendanceRecord>> = _attendanceList

    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> = _courses

    private val _selectedCourse = MutableLiveData<Course?>()
    val selectedCourse: LiveData<Course?> = _selectedCourse

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    data class Course(
        val id: String,
        val name: String,
        val teacherId: String,
        val startTime: String,
        val endTime: String,
        val weekDay: String
    )

    init {
        loadTeacherCourses()
    }

    private fun loadTeacherCourses() {
        _loading.value = true

        val teacherId = "PBZSdZadxy0D9ovdcJ8x"

        db.collection("Courses")
            .whereEqualTo("TeacherID", teacherId)
            .whereEqualTo("Status", true)
            .get()
            .addOnSuccessListener { documents ->
                val courseList = mutableListOf<Course>()
                for (document in documents) {
                    val course = Course(
                        id = document.id,
                        name = document.getString("Name") ?: "",
                        teacherId = document.getString("TeacherID") ?: "",
                        startTime = document.getString("StartTime") ?: "",
                        endTime = document.getString("EndTime") ?: "",
                        weekDay = document.getString("WeekDay") ?: ""
                    )
                    courseList.add(course)
                }
                _courses.value = courseList
                _loading.value = false
            }
            .addOnFailureListener { exception ->
                _error.value = "Error loading courses: ${exception.message}"
                _loading.value = false
            }
    }

    fun selectCourse(course: Course) {
        _selectedCourse.value = course
        loadAttendanceForCourse(course.id)
    }

    private fun loadAttendanceForCourse(courseId: String) {
        _loading.value = true
        val today = Calendar.getInstance()
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        db.collection("Calendar")
            .whereEqualTo("CourseID", courseId)
            .whereGreaterThanOrEqualTo("Date", startOfDay.time)
            .whereLessThanOrEqualTo("Date", endOfDay.time)
            .get()
            .addOnSuccessListener { calendarDocs ->
                if (calendarDocs.isEmpty) {
                    _attendanceList.value = emptyList()
                    _loading.value = false
                    return@addOnSuccessListener
                }

                val calendarId = calendarDocs.documents[0].id
                loadAttendanceRecords(courseId, calendarId)
            }
            .addOnFailureListener { exception ->
                _error.value = "Error loading calendar: ${exception.message}"
                _loading.value = false
            }
    }

    private fun loadAttendanceRecords(courseId: String, calendarId: String) {
        db.collection("JoinedClass")
            .whereEqualTo("CourseID", courseId)
            .whereEqualTo("Status", true)
            .get()
            .addOnSuccessListener { joinedDocs ->
                val studentIds = joinedDocs.documents.map { it.getString("StudentID") ?: "" }

                if (studentIds.isEmpty()) {
                    _attendanceList.value = emptyList()
                    _loading.value = false
                    return@addOnSuccessListener
                }
                db.collection("Attendance")
                    .whereEqualTo("courseID", courseId)
                    .whereEqualTo("calenderID", calendarId)
                    .get()
                    .addOnSuccessListener { attendanceDocs ->
                        val existingAttendance = mutableMapOf<String, String>()
                        for (doc in attendanceDocs) {
                            val studentId = doc.getString("studentID") ?: ""
                            val status = doc.getString("status") ?: "absent"
                            existingAttendance[studentId] = status
                        }
                        loadStudentInfo(studentIds, existingAttendance, courseId, calendarId)
                    }
                    .addOnFailureListener { exception ->
                        _error.value = "Error loading attendance: ${exception.message}"
                        _loading.value = false
                    }
            }
            .addOnFailureListener { exception ->
                _error.value = "Error loading enrolled students: ${exception.message}"
                _loading.value = false
            }
    }

    private fun loadStudentInfo(
        studentIds: List<String>,
        existingAttendance: Map<String, String>,
        courseId: String,
        calendarId: String
    ) {
        val attendanceRecords = mutableListOf<AttendanceRecord>()
        var loadedCount = 0

        for (studentId in studentIds) {
            db.collection("StudentAcc")
                .document(studentId)
                .get()
                .addOnSuccessListener { studentDoc ->
                    val studentInfoId = studentDoc.getString("StudentInfoID") ?: ""

                    db.collection("StudentInfo")
                        .document(studentInfoId)
                        .get()
                        .addOnSuccessListener { infoDoc ->
                            val firstName = infoDoc.getString("Fname") ?: ""
                            val lastName = infoDoc.getString("Lname") ?: ""
                            val fullName = "$firstName $lastName"

                            val status = existingAttendance[studentId] ?: "absent"

                            val record = AttendanceRecord(
                                studentName = fullName,
                                studentId = studentId,
                                status = status,
                                courseId = courseId,
                                calendarId = calendarId,
                                timestamp = System.currentTimeMillis()
                            )

                            attendanceRecords.add(record)
                            loadedCount++

                            if (loadedCount == studentIds.size) {
                                _attendanceList.value = attendanceRecords.sortedBy { it.studentName }
                                _loading.value = false
                            }
                        }
                }
        }
    }

    fun updateAttendanceStatus(record: AttendanceRecord, newStatus: String) {
        val attendanceData = hashMapOf(
            "calenderID" to record.calendarId,
            "courseID" to record.courseId,
            "status" to newStatus,
            "studentID" to record.studentId,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("Attendance")
            .whereEqualTo("courseID", record.courseId)
            .whereEqualTo("calenderID", record.calendarId)
            .whereEqualTo("studentID", record.studentId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    db.collection("Attendance")
                        .add(attendanceData)
                        .addOnSuccessListener {
                            updateLocalAttendanceStatus(record.studentId, newStatus)
                        }
                        .addOnFailureListener { exception ->
                            _error.value = "Error saving attendance: ${exception.message}"
                        }
                } else {
                    val docId = documents.documents[0].id
                    db.collection("Attendance")
                        .document(docId)
                        .update("status", newStatus, "timestamp", System.currentTimeMillis())
                        .addOnSuccessListener {
                            updateLocalAttendanceStatus(record.studentId, newStatus)
                        }
                        .addOnFailureListener { exception ->
                            _error.value = "Error updating attendance: ${exception.message}"
                        }
                }
            }
    }

    private fun updateLocalAttendanceStatus(studentId: String, newStatus: String) {
        val currentList = _attendanceList.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.studentId == studentId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(status = newStatus)
            _attendanceList.value = currentList
        }
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}
