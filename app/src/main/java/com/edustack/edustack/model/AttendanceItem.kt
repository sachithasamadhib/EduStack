package com.edustack.edustack.model

data class AttendanceItem(
    val calendarId: String = "",
    val courseId: String = "",
    val studentId: String = "",
    val date: String = "",
    val status: String = "", // "Present", "Absent", "Late"
    val timestamp: Long = 0L
)