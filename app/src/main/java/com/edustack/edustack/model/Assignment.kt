package com.edustack.edustack.model

data class Assignment(
    val assignmentID: String = "",
    val courseID: String = "",
    val description: String = "",
    val handInDate: Long = 0L, // timestamp
    val handOutDate: Long = 0L, // timestamp
    val status: Boolean = true,
    val teacherID: String = "",
    val title: String = "",
    val date: Long = 0L, // timestamp
    val assignmentMaterialLink: String = "" // Link to assignment materials
)

data class AssignmentMaterial(
    val assignmentID: String = "",
    val date: Long = 0L, // timestamp
    val link: String = ""
)

data class Submission(
    val assignmentID: String = "",
    val courseID: String = "",
    val date: Long = 0L, // timestamp
    val studentID: String = "",
    val submissionMaterialLink: String = "" // Link to submitted work
)

data class SubmissionMaterial(
    val link: String = "",
    val submissionID: String = "",
    val time: Long = 0L // timestamp
)