package com.edustack.edustack.Controller

import android.content.res.Resources
import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GetStudentCount : ViewModel(){
    var count : Int = 0
    var teacherCount : Int = 0
    val db = Firebase.firestore
    suspend fun getStudentCount(): Int = withContext(Dispatchers.IO)  {
         try {
            val result = db.collection("StudentAcc")
                .get()
                .await()
            return@withContext result.size()
        } catch (e: Exception) {
            throw Exception("Error occurred when getting count: ${e.message}", e)
        }
    }
    suspend fun getTeacherCount(): Int = withContext(Dispatchers.IO)  {
        try {
            val result = db.collection("TeacherAcc")
                .get()
                .await()
            return@withContext result.size()
        } catch (e: Exception) {
            throw Exception("Error occurred when teacher getting count: ${e.message}", e)
        }
    }
}