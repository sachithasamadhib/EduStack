package com.edustack.edustack.ui.Teacher.CameraQr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.edustack.edustack.data.AttendanceRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CameraViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult: StateFlow<ScanResult?> = _scanResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun processQRCode(qrContent: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val parts = qrContent.split("|")

                if (parts.size >= 3 && parts[0] == "STUDENT_ATTENDANCE") {
                    val studentId = parts[1]
                    val timestamp = parts[2].toLongOrNull() ?: System.currentTimeMillis()

                    // Save to Firestore
                    saveAttendance(studentId, timestamp)
                } else {
                    _scanResult.value = ScanResult.Error("Invalid QR code format")
                }
            } catch (e: Exception) {
                _scanResult.value = ScanResult.Error("Error processing QR code: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun saveAttendance(studentId: String, timestamp: Long) {
        try {
            val calendarId = getCurrentCalendarId()
            val courseId = getCurrentCourseId()

            val attendanceRecord = AttendanceRecord(
                studentID = studentId,
                calenderID = calendarId,
                courseID = courseId,
                timestamp = timestamp
            )

            // Save to Firestore
            firestore.collection("Attendance")
                .add(attendanceRecord)
                .await()

            _scanResult.value = ScanResult.Success("Attendance recorded for student: $studentId")

        } catch (e: Exception) {
            _scanResult.value = ScanResult.Error("Failed to save attendance: ${e.message}")
        }
    }

    private fun getCurrentCalendarId(): String {

        return "LVZsDokI5VCu9R0rvHGK"
    }

    private fun getCurrentCourseId(): String {

        return "TRokaIkRl7lmfV4yNCb5"
    }

    fun clearScanResult() {
        _scanResult.value = null
    }

    sealed class ScanResult {
        data class Success(val message: String) : ScanResult()
        data class Error(val message: String) : ScanResult()
    }
}