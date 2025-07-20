package com.edustack.edustack.services

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebaseUploadService {

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun uploadAssignment(
        context: Context,
        assignmentId: String,
        courseId: String,
        studentId: String,
        fileUri: Uri,
        fileName: String
    ): Result<String> {
        return try {
            // 1. Upload file to Firebase Storage
            val storageRef = storage.reference
                .child("assignments")
                .child(courseId)
                .child(assignmentId)
                .child("${studentId}_${Date().time}_$fileName")

            val uploadTask = storageRef.putFile(fileUri).await()
            val downloadUrl = storageRef.downloadUrl.await()

            // 2. Create Submissions document
            val submissionData = hashMapOf(
                "assignmentID" to assignmentId,
                "courseID" to courseId,
                "studentID" to studentId,
                "date" to Date(),
                "marks" to 0 // Will be updated by teacher
            )

            val submissionDoc = firestore.collection("Submissions").add(submissionData).await()

            // 3. Create SubmissionMaterials document
            val submissionMaterialData = hashMapOf(
                "link" to downloadUrl.toString(),
                "submissionID" to submissionDoc.id,
                "time" to Date()
            )

            firestore.collection("SubmissionMaterials").add(submissionMaterialData).await()

            Result.success("Upload successful")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}