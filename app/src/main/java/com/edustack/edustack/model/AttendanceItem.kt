package com.edustack.edustack.model

data class AttendanceItem(
    val calendarId: String = "",
    val courseId: String = "",
    val studentId: String = "",
    val date: String = "",
    val status: String = "", // "Present", "Absent", "Late"
    val timestamp: Long = 0L,
    val studentName: String = "", // New field
    val courseName: String = ""   // New field
)
// Old version for reference (commented out)
// data class AttendanceItem(
//     val calendarId: String = "",
//     val courseId: String = "",
//     val studentId: String = "",
//     val date: String = "",
//     val status: String = "", // "Present", "Absent", "Late"
//     val timestamp: Long = 0L
// )