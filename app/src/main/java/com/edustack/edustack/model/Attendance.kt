package com.edustack.edustack.model

data class Attendance(
    val id: String = "",
    val calendarId: String = "",
    val courseId: String = "",
    val studentId: String = "",
    val status: String = "",
    val timestamp: Long = 0L
) 