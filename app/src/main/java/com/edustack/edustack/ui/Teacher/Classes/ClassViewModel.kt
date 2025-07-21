package com.edustack.edustack.ui.Teacher.Classes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class ClassViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _classes = MutableLiveData<List<ClassItem>>()
    val classes: LiveData<List<ClassItem>> = _classes

    private val _classStats = MutableLiveData<ClassStats>()
    val classStats: LiveData<ClassStats> = _classStats

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val demoClasses = mutableListOf<ClassItem>()

    init {
        loadDemoData()
    }

    private fun loadDemoData() {
        demoClasses.clear()
        demoClasses.addAll(
            listOf(
                ClassItem(
                    id = "demo_1",
                    name = "Mathematics Grade 10",
                    description = "Advanced Mathematics Course",
                    startTime = "10:00",
                    endTime = "11:30",
                    weekDay = "Monday",
                    defaultHall = "Room 101",
                    studentCount = 25,
                    isActive = true,
                    isCancelled = false,
                    hallId = "101",
                    cancellationReason = ""
                ),
                ClassItem(
                    id = "demo_2",
                    name = "Physics Grade 11",
                    description = "Basic Physics Concepts",
                    startTime = "14:00",
                    endTime = "15:30",
                    weekDay = "Tuesday",
                    defaultHall = "Room 102",
                    studentCount = 18,
                    isActive = true,
                    isCancelled = false,
                    hallId = "102",
                    cancellationReason = ""
                )
            )
        )
    }

    fun loadClasses() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val firebaseClasses = loadClassesFromFirebase()
                val allClasses = mutableListOf<ClassItem>()
                allClasses.addAll(demoClasses)
                allClasses.addAll(firebaseClasses)

                _classes.value = allClasses
                updateStats(allClasses)

            } catch (e: Exception) {
                _error.value = "Error loading classes: ${e.message}"
                _classes.value = demoClasses.toList()
                updateStats(demoClasses)
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun loadClassesFromFirebase(): List<ClassItem> {
        return try {
            val teacherId = getCurrentTeacherId()
            if (teacherId == null) {
                return emptyList()
            }

            val coursesSnapshot = firestore.collection("Cources")
                .whereEqualTo("TeacherID", teacherId)
                .whereEqualTo("Status", true)
                .get()
                .await()

            val classList = mutableListOf<ClassItem>()

            for (courseDoc in coursesSnapshot.documents) {
                val courseData = courseDoc.data ?: continue

                val todayCalendar = getTodaysCalendarEntry(courseDoc.id)

                val classItem = ClassItem(
                    id = courseDoc.id,
                    name = courseData["Name"] as? String ?: "",
                    description = courseData["Description"] as? String ?: "",
                    startTime = courseData["StartTime"] as? String ?: "",
                    endTime = courseData["EndTime"] as? String ?: "",
                    weekDay = courseData["WeekDay"] as? String ?: "",
                    defaultHall = courseData["DefaultHall"] as? String ?: "",
                    studentCount = getStudentCount(courseDoc.id),
                    isActive = todayCalendar != null && (todayCalendar["Status"] as? Boolean == true),
                    isCancelled = courseData["isCancelled"] as? Boolean ?: false,
                    hallId = todayCalendar?.get("HallID") as? String ?: courseData["DefaultHall"] as? String ?: "",
                    cancellationReason = courseData["cancellationReason"] as? String ?: ""
                )

                classList.add(classItem)
            }

            return classList
        } catch (e: Exception) {
            return emptyList()
        }
    }

    fun loadClassesByDate(date: Date) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val calendar = Calendar.getInstance()
                calendar.time = date
                val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> "Monday"
                    Calendar.TUESDAY -> "Tuesday"
                    Calendar.WEDNESDAY -> "Wednesday"
                    Calendar.THURSDAY -> "Thursday"
                    Calendar.FRIDAY -> "Friday"
                    Calendar.SATURDAY -> "Saturday"
                    Calendar.SUNDAY -> "Sunday"
                    else -> ""
                }

                val filteredDemoClasses = demoClasses.filter { it.weekDay == dayOfWeek }
                val firebaseClasses = loadClassesByDateFromFirebase(date)
                val allClasses = mutableListOf<ClassItem>()
                allClasses.addAll(filteredDemoClasses)
                allClasses.addAll(firebaseClasses)

                _classes.value = allClasses

            } catch (e: Exception) {
                _error.value = "Error loading classes by date: ${e.message}"
                val calendar = Calendar.getInstance()
                calendar.time = date
                val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> "Monday"
                    Calendar.TUESDAY -> "Tuesday"
                    Calendar.WEDNESDAY -> "Wednesday"
                    Calendar.THURSDAY -> "Thursday"
                    Calendar.FRIDAY -> "Friday"
                    Calendar.SATURDAY -> "Saturday"
                    Calendar.SUNDAY -> "Sunday"
                    else -> ""
                }
                val filteredClasses = demoClasses.filter { it.weekDay == dayOfWeek }
                _classes.value = filteredClasses
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun loadClassesByDateFromFirebase(date: Date): List<ClassItem> {
        return try {
            val teacherId = getCurrentTeacherId() ?: return emptyList()

            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.time

            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val startOfNextDay = calendar.time

            val calendarSnapshot = firestore.collection("Calender")
                .whereGreaterThanOrEqualTo("Date", com.google.firebase.Timestamp(startOfDay))
                .whereLessThan("Date", com.google.firebase.Timestamp(startOfNextDay))
                .get()
                .await()

            val classList = mutableListOf<ClassItem>()

            for (calendarDoc in calendarSnapshot.documents) {
                val calendarData = calendarDoc.data ?: continue
                val courseId = calendarData["CourceID"] as? String ?: continue

                val courseDoc = firestore.collection("Cources")
                    .document(courseId)
                    .get()
                    .await()

                val courseData = courseDoc.data ?: continue

                if (courseData["TeacherID"] != teacherId) continue

                val classItem = ClassItem(
                    id = courseDoc.id,
                    name = courseData["Name"] as? String ?: "",
                    description = courseData["Description"] as? String ?: "",
                    startTime = courseData["StartTime"] as? String ?: "",
                    endTime = courseData["EndTime"] as? String ?: "",
                    weekDay = courseData["WeekDay"] as? String ?: "",
                    defaultHall = courseData["DefaultHall"] as? String ?: "",
                    studentCount = getStudentCount(courseDoc.id),
                    isActive = calendarData["Status"] as? Boolean ?: false,
                    isCancelled = courseData["isCancelled"] as? Boolean ?: false,
                    hallId = calendarData["HallID"] as? String ?: "",
                    cancellationReason = courseData["cancellationReason"] as? String ?: ""
                )

                classList.add(classItem)
            }

            return classList
        } catch (e: Exception) {
            return emptyList()
        }
    }

    fun loadClassStats() {
        viewModelScope.launch {
            try {
                val firebaseStats = loadStatsFromFirebase()

                val demoActiveClasses = demoClasses.count { it.isActive && !it.isCancelled }
                val demoTotalStudents = demoClasses.sumOf { it.studentCount }

                val totalActiveClasses = firebaseStats.activeClasses + demoActiveClasses
                val totalStudents = firebaseStats.totalStudents + demoTotalStudents

                _classStats.value = ClassStats(totalActiveClasses, totalStudents)

            } catch (e: Exception) {
                _error.value = "Error loading class stats: ${e.message}"
                val demoActiveClasses = demoClasses.count { it.isActive && !it.isCancelled }
                val demoTotalStudents = demoClasses.sumOf { it.studentCount }
                _classStats.value = ClassStats(demoActiveClasses, demoTotalStudents)
            }
        }
    }

    private suspend fun loadStatsFromFirebase(): ClassStats {
        return try {
            val teacherId = getCurrentTeacherId() ?: return ClassStats(0, 0)

            val coursesSnapshot = firestore.collection("Cources")
                .whereEqualTo("TeacherID", teacherId)
                .whereEqualTo("Status", true)
                .get()
                .await()

            var totalStudents = 0
            var activeClasses = 0

            for (courseDoc in coursesSnapshot.documents) {
                val studentCount = getStudentCount(courseDoc.id)
                totalStudents += studentCount

                val todayCalendar = getTodaysCalendarEntry(courseDoc.id)
                if (todayCalendar != null && (todayCalendar["Status"] as? Boolean == true)) {
                    activeClasses++
                }
            }

            return ClassStats(activeClasses, totalStudents)
        } catch (e: Exception) {
            return ClassStats(0, 0)
        }
    }

    fun addClass(
        name: String,
        description: String,
        startTime: String,
        endTime: String,
        weekDay: String,
        defaultHall: String
    ) {
        viewModelScope.launch {
            try {
                val teacherId = getCurrentTeacherId()

                if (teacherId != null) {
                    val classData = hashMapOf(
                        "Name" to name,
                        "Description" to description,
                        "StartTime" to startTime,
                        "EndTime" to endTime,
                        "WeekDay" to weekDay,
                        "DefaultHall" to defaultHall,
                        "TeacherID" to teacherId,
                        "Status" to true,
                        "StartedDate" to com.google.firebase.Timestamp.now(),
                        "isCancelled" to false,
                        "cancellationReason" to ""
                    )

                    firestore.collection("Cources")
                        .add(classData)
                        .await()

                    loadClasses()
                } else {
                    addToDemoData(name, description, startTime, endTime, weekDay, defaultHall)
                }

            } catch (e: Exception) {
                addToDemoData(name, description, startTime, endTime, weekDay, defaultHall)
                _error.value = "Added locally (Firebase error: ${e.message})"
            }
        }
    }

    private fun addToDemoData(
        name: String,
        description: String,
        startTime: String,
        endTime: String,
        weekDay: String,
        defaultHall: String
    ) {
        val newClass = ClassItem(
            id = "demo_${System.currentTimeMillis()}",
            name = name,
            description = description,
            startTime = startTime,
            endTime = endTime,
            weekDay = weekDay,
            defaultHall = defaultHall,
            studentCount = 0,
            isActive = true,
            isCancelled = false,
            hallId = defaultHall,
            cancellationReason = ""
        )

        demoClasses.add(newClass)

        val currentClasses = _classes.value?.toMutableList() ?: mutableListOf()
        currentClasses.add(newClass)
        _classes.value = currentClasses
        updateStats(currentClasses)
    }

    fun cancelClass(classId: String, reason: String) {
        viewModelScope.launch {
            try {
                if (!classId.startsWith("demo_")) {
                    val teacherId = getCurrentTeacherId()

                    if (teacherId != null) {
                        val cancellationData = hashMapOf(
                            "courseID" to classId,
                            "teacherId" to teacherId,
                            "reason" to reason,
                            "requestDate" to com.google.firebase.Timestamp.now(),
                            "status" to false,
                            "adminResponse" to ""
                        )

                        firestore.collection("ClassCancellations")
                            .add(cancellationData)
                            .await()

                        firestore.collection("Cources")
                            .document(classId)
                            .update(
                                mapOf(
                                    "isCancelled" to true,
                                    "cancellationReason" to reason
                                )
                            )
                            .await()

                        createAdminNotification(reason)
                        loadClasses()
                        return@launch
                    }
                }
                cancelDemoClass(classId, reason)

            } catch (e: Exception) {
                // If Firebase fails, handle as demo data
                cancelDemoClass(classId, reason)
                _error.value = "Cancelled locally (Firebase error: ${e.message})"
            }
        }
    }

    private fun cancelDemoClass(classId: String, reason: String) {
        val classIndex = demoClasses.indexOfFirst { it.id == classId }
        if (classIndex != -1) {
            val updatedClass = demoClasses[classIndex].copy(
                isCancelled = true,
                cancellationReason = reason,
                isActive = false
            )
            demoClasses[classIndex] = updatedClass
        }

        val currentClasses = _classes.value?.toMutableList() ?: mutableListOf()
        val currentIndex = currentClasses.indexOfFirst { it.id == classId }
        if (currentIndex != -1) {
            val updatedClass = currentClasses[currentIndex].copy(
                isCancelled = true,
                cancellationReason = reason,
                isActive = false
            )
            currentClasses[currentIndex] = updatedClass
            _classes.value = currentClasses
            updateStats(currentClasses)
        }
    }

    private fun updateStats(classList: List<ClassItem>) {
        val activeClasses = classList.count { it.isActive && !it.isCancelled }
        val totalStudents = classList.sumOf { it.studentCount }
        _classStats.value = ClassStats(activeClasses, totalStudents)
    }

    private suspend fun getCurrentTeacherId(): String? {
        return try {
            val currentUser = auth.currentUser ?: return null
            val teacherAccSnapshot = firestore.collection("TeacherAcc")
                .whereEqualTo("UserName", currentUser.email)
                .limit(1)
                .get()
                .await()

            teacherAccSnapshot.documents.firstOrNull()?.get("TeacherInfoID") as? String
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getStudentCount(courseId: String): Int {
        return try {
            val joinedClassSnapshot = firestore.collection("JoinedClass")
                .whereEqualTo("CourseID", courseId)
                .whereEqualTo("Status", true)
                .get()
                .await()

            joinedClassSnapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    private suspend fun getTodaysCalendarEntry(courseId: String): Map<String, Any>? {
        return try {
            val today = Date()
            val calendar = Calendar.getInstance()
            calendar.time = today
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.time

            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val startOfNextDay = calendar.time

            val calendarSnapshot = firestore.collection("Calender")
                .whereEqualTo("CourceID", courseId)
                .whereGreaterThanOrEqualTo("Date", com.google.firebase.Timestamp(startOfDay))
                .whereLessThan("Date", com.google.firebase.Timestamp(startOfNextDay))
                .limit(1)
                .get()
                .await()

            calendarSnapshot.documents.firstOrNull()?.data
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun createAdminNotification(reason: String) {
        try {
            val adminSnapshot = firestore.collection("Admin")
                .limit(1)
                .get()
                .await()

            for (adminDoc in adminSnapshot.documents) {
                val notificationData = hashMapOf(
                    "PersonID" to adminDoc.id,
                    "Type" to "ADMIN",
                    "Title" to "Class Cancellation Request",
                    "Description" to "Teacher has requested to cancel a class. Reason: $reason",
                    "Date" to com.google.firebase.Timestamp.now()
                )

                firestore.collection("NotificationSaved")
                    .add(notificationData)
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

data class ClassItem(
    val id: String,
    val name: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val weekDay: String,
    val defaultHall: String,
    val studentCount: Int,
    val isActive: Boolean,
    val isCancelled: Boolean,
    val hallId: String,
    val cancellationReason: String = ""
)

data class ClassStats(
    val activeClasses: Int,
    val totalStudents: Int
)