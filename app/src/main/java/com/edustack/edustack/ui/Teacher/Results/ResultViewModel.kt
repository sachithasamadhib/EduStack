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

    private val _submissions = MutableLiveData<List<SubmissionData>>()
    val submissions: LiveData<List<SubmissionData>> = _submissions

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    fun loadSubmissions(teacherId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val assignmentsSnapshot = firestore.collection("Assignments")
                    .whereEqualTo("TeacherID", teacherId)
                    .get()
                    .await()

                if (assignmentsSnapshot.documents.isEmpty()) {
                    _submissions.value = emptyList()
                    _loading.value = false
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

                val submissionsList = mutableListOf<SubmissionData>()

                for (assignment in assignments) {
                    val submissionsSnapshot = firestore.collection("Submissions")
                        .whereEqualTo("AssignmentID", assignment.id)
                        .get()
                        .await()

                    for (submissionDoc in submissionsSnapshot.documents) {
                        val studentId = submissionDoc.getString("StudentID") ?: ""
                        var studentName = "Unknown Student"
                        try {
                            val studentDoc = firestore.collection("StudentInfo")
                                .document(studentId)
                                .get()
                                .await()

                            if (studentDoc.exists()) {
                                val fname = studentDoc.getString("Fname") ?: ""
                                val lname = studentDoc.getString("Lname") ?: ""
                                studentName = "$fname $lname".trim()
                                if (studentName.isBlank()) {
                                    studentName = "Student $studentId"
                                }
                            }
                        } catch (e: Exception) {
                        }
                        var materials = emptyList<String>()
                        try {
                            val materialsSnapshot = firestore.collection("SubmissionMaterials")
                                .whereEqualTo("SubmissionID", submissionDoc.id)
                                .get()
                                .await()

                            materials = materialsSnapshot.documents.mapNotNull { it.getString("Link") }
                        } catch (e: Exception) {
                        }

                        val submission = SubmissionData(
                            submissionId = submissionDoc.id,
                            assignmentId = assignment.id,
                            courseId = submissionDoc.getString("CourseID") ?: "",
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

                _submissions.value = submissionsList
                _loading.value = false

            } catch (e: Exception) {
                _error.value = "Error loading submissions: ${e.message}"
                _loading.value = false
            }
        }
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
            }
        }
    }
}
