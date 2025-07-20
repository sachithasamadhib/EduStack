package com.edustack.edustack.model

data class CalendarEvent(
    val id: String = "",
    val courseID: String = "",
    val date: Long = 0L, // timestamp
    val endTime: Long = 0L, // timestamp
    val hallID: String = "",
    val startTime: Long = 0L, // timestamp
    val status: Boolean = true,
    val courseName: String = "", // We'll add this for better UI
    val hallName: String = "" // We'll add this for better UI
)