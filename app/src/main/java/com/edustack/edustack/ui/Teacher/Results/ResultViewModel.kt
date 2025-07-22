package com.edustack.edustack.ui.Teacher.Results

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class SubmissionData(
    val submissionId: String = "",
    val assignmentId: String = "",
    val courseId: String = "",
    val courseName: String = "",
    val studentId: String = "",
    val marks: Int = 0,
    val date: com.google.firebase.Timestamp? = null,
    val assignmentTitle: String = "",
    val studentName: String = "",
    val submissionMaterials: List<String> = emptyList()
)

data class AssignmentInfo(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val courseId: String = ""
)

class ResultViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _allSubmissions = MutableLiveData<List<SubmissionData>>()
    private val _submissions = MutableLiveData<List<SubmissionData>>()
    val submissions: LiveData<List<SubmissionData>> = _submissions

    private val _courses = MutableLiveData<List<CourseInfo>>()
    val courses: LiveData<List<CourseInfo>> = _courses

    private val _assignments = MutableLiveData<List<AssignmentFilterInfo>>()
    val assignments: LiveData<List<AssignmentFilterInfo>> = _assignments

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    private var currentFilter = FilterOptions()

    fun loadSubmissions(teacherId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                // Get assignments for this teacher
                val assignmentsSnapshot = firestore.collection("Assignments")
                    .whereEqualTo("TeacherID", teacherId)
                    .get()
                    .await()

                if (assignmentsSnapshot.documents.isEmpty()) {
                    _allSubmissions.value = emptyList()
                    _submissions.value = emptyList()
                    _loading.value = false
                    _error.value = "No assignments found for teacher ID: $teacherId"
                    return@launch
                }

                val assignments = assignmentsSnapshot.documents.map { doc ->
                    AssignmentInfo(
                        id = doc.id,
                        title = doc.getString("Title") ?: "Untitled Assignment",
                        description = doc.getString("Description") ?: "",
                        courseId = doc.getString("CourseID") ?: ""
                    )
                }

                // Load filter data
                loadFilterData(assignments)

                val submissionsList = mutableListOf<SubmissionData>()

                // Get submissions for each assignment
                for (assignment in assignments) {
                    val submissionsSnapshot = firestore.collection("Submissions")
                        .whereEqualTo("AssignmentID", assignment.id)
                        .get()
                        .await()

                    for (submissionDoc in submissionsSnapshot.documents) {
                        val studentId = submissionDoc.getString("StudentID") ?: ""
                        val courseId = submissionDoc.getString("CourseID") ?: assignment.courseId

                        // Get course name
                        var courseName = "Unknown Course"
                        try {
                            val courseDoc = firestore.collection("Cources")
                                .document(courseId)
                                .get()
                                .await()

                            if (courseDoc.exists()) {
                                courseName = courseDoc.getString("Name") ?: "Unknown Course"
                            }
                        } catch (e: Exception) {
                            courseName = "Course $courseId"
                        }

                        // Get student name
                        var studentName = "Unknown Student"
                        try {
                            val studentAccDoc = firestore.collection("StudentAcc")
                                .document(studentId)
                                .get()
                                .await()

                            if (studentAccDoc.exists()) {
                                val studentInfoId = studentAccDoc.getString("StudentInfoID") ?: ""

                                if (studentInfoId.isNotEmpty()) {
                                    val studentInfoDoc = firestore.collection("StudentInfo")
                                        .document(studentInfoId)
                                        .get()
                                        .await()

                                    if (studentInfoDoc.exists()) {
                                        val fname = studentInfoDoc.getString("Fname") ?: ""
                                        val lname = studentInfoDoc.getString("Lname") ?: ""
                                        studentName = "$fname $lname".trim()
                                        if (studentName.isBlank()) {
                                            studentName = "Student $studentId"
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            studentName = "Error Loading Student: ${e.message}"
                        }

                        // Get submission materials
                        var materials = emptyList<String>()
                        try {
                            val materialsSnapshot = firestore.collection("SubmissionMaterials")
                                .whereEqualTo("SubmissionID", submissionDoc.id)
                                .get()
                                .await()
                            materials = materialsSnapshot.documents.mapNotNull { it.getString("Link") }
                        } catch (e: Exception) {
                            // Materials loading failed, continue with empty list
                        }

                        val submission = SubmissionData(
                            submissionId = submissionDoc.id,
                            assignmentId = assignment.id,
                            courseId = courseId,
                            courseName = courseName,
                            studentId = studentId,
                            marks = submissionDoc.getLong("Marks")?.toInt() ?: 0,
                            date = submissionDoc.getTimestamp("Date"),
                            assignmentTitle = assignment.title,
                            studentName = studentName,
                            submissionMaterials = materials
                        )
                        submissionsList.add(submission)
                    }
                }

                _allSubmissions.value = submissionsList
                applyFilter(currentFilter)
                _loading.value = false

            } catch (e: Exception) {
                _error.value = "Error loading submissions: ${e.message}"
                _loading.value = false
            }
        }
    }

    private fun loadFilterData(assignments: List<AssignmentInfo>) {
        viewModelScope.launch {
            try {
                // Load courses
                val courseIds = assignments.map { it.courseId }.distinct()
                val coursesList = mutableListOf<CourseInfo>()

                for (courseId in courseIds) {
                    try {
                        val courseDoc = firestore.collection("Cources")
                            .document(courseId)
                            .get()
                            .await()

                        if (courseDoc.exists()) {
                            coursesList.add(
                                CourseInfo(
                                    id = courseId,
                                    name = courseDoc.getString("Name") ?: "Unknown Course"
                                )
                            )
                        }
                    } catch (e: Exception) {
                        coursesList.add(CourseInfo(id = courseId, name = "Course $courseId"))
                    }
                }

                _courses.value = coursesList

                // Load assignments for filter
                val assignmentFilterList = assignments.map { assignment ->
                    AssignmentFilterInfo(
                        id = assignment.id,
                        title = assignment.title,
                        courseId = assignment.courseId
                    )
                }
                _assignments.value = assignmentFilterList

            } catch (e: Exception) {
                // Handle error silently for filter data
            }
        }
    }

    fun applyFilter(filter: FilterOptions) {
        currentFilter = filter
        val allSubmissions = _allSubmissions.value ?: emptyList()

        val filteredSubmissions = allSubmissions.filter { submission ->
            // Course filter
            if (filter.selectedCourseId != null && submission.courseId != filter.selectedCourseId) {
                return@filter false
            }

            // Assignment filter
            if (filter.selectedAssignmentId != null && submission.assignmentId != filter.selectedAssignmentId) {
                return@filter false
            }

            // Marks filter
            if (filter.minMarks != null && submission.marks < filter.minMarks) {
                return@filter false
            }
            if (filter.maxMarks != null && submission.marks > filter.maxMarks) {
                return@filter false
            }

            // Date filter
            submission.date?.let { timestamp ->
                val submissionTime = timestamp.toDate().time
                if (filter.dateFrom != null && submissionTime < filter.dateFrom) {
                    return@filter false
                }
                if (filter.dateTo != null && submissionTime > filter.dateTo) {
                    return@filter false
                }
            }

            // Search query filter
            if (filter.searchQuery.isNotEmpty()) {
                val query = filter.searchQuery.lowercase()
                val matchesStudent = submission.studentName.lowercase().contains(query)
                val matchesAssignment = submission.assignmentTitle.lowercase().contains(query)
                val matchesCourse = submission.courseName.lowercase().contains(query)

                if (!matchesStudent && !matchesAssignment && !matchesCourse) {
                    return@filter false
                }
            }

            true
        }

        _submissions.value = filteredSubmissions
    }

    fun clearFilters() {
        currentFilter = FilterOptions()
        _submissions.value = _allSubmissions.value ?: emptyList()
    }

    fun updateMarks(submissionId: String, marks: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                firestore.collection("Submissions")
                    .document(submissionId)
                    .update("Marks", marks)
                    .await()
                _updateSuccess.value = true
                _loading.value = false
            } catch (e: Exception) {
                _error.value = "Error updating marks: ${e.message}"
                _loading.value = false
                _updateSuccess.value = false
            }
        }
    }
}
