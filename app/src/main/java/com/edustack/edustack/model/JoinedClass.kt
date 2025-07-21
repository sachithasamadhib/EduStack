package com.edustack.edustack.model

import com.google.firebase.Timestamp

data class JoinedClass(
    val id: String = "",
    val courseId: String = "",
    val studentId: String = "",
    val joinedDate: Timestamp? = null,
    val status: Boolean = true
) 