package com.edustack.edustack.model

data class StudentInfo(
    val address: String = "",
    val city: String = "",
    val contactNumber: String = "",
    val dob: Long = 0L, // timestamp
    val email: String = "",
    val fname: String = "",
    val gender: String = "",
    val joinedDate: Long = 0L, // timestamp
    val lname: String = "",
    val school: String = ""
)