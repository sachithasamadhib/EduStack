package com.edustack.edustack.data

data class AttendanceRecord(
    val studentID: String = "",
    val calenderID: String = "",
    val courseID: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "present"
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", 0L, "present")
}