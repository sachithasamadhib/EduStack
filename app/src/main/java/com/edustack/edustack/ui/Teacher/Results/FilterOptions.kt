package com.edustack.edustack.ui.Teacher.Results

data class FilterOptions(
    val selectedCourseId: String? = null,
    val selectedAssignmentId: String? = null,
    val minMarks: Int? = null,
    val maxMarks: Int? = null,
    val dateFrom: Long? = null,
    val dateTo: Long? = null,
    val searchQuery: String = ""
)

data class CourseInfo(
    val id: String = "",
    val name: String = ""
)

data class AssignmentFilterInfo(
    val id: String = "",
    val title: String = "",
    val courseId: String = ""
)
