package com.edustack.edustack.model

data class CourseMaterial(
    val courseId: String = "",
    val date: Long = 0L, // timestamp
    val link: String = "",
    val title: String = "", // We'll add this for better UI
    val fileType: String = "" // We'll add this to show file type
)