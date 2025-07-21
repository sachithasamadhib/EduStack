package com.edustack.edustack.repository

import com.edustack.edustack.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StudentRepository {
    private val firestore = FirebaseFirestore.getInstance()
    val currentStudentId = "lkwEiMpp79yrQDFz93TN"
    
    // Collections
    private val joinedClassesCollection = firestore.collection("JoinedClass")
    private val coursesCollection = firestore.collection("Cources")  // Changed to match actual collection name
    private val studentsCollection = firestore.collection("StudentAcc")
    private val studentInfoCollection = firestore.collection("StudentInfo")
    private val assignmentsCollection = firestore.collection("Assignments")
    private val attendanceCollection = firestore.collection("Attendance")
    private val calendarCollection = firestore.collection("Calender")
    private val notificationsCollection = firestore.collection("NotificationSaved")
    private val resultsCollection = firestore.collection("Results")
    private val submissionsCollection = firestore.collection("Submissions")
    private val assignmentMaterialsCollection = firestore.collection("AssignmentMaterials")
    private val courseMaterialsCollection = firestore.collection("CourseMaterials")
    private val teachersInfoCollection = firestore.collection("TeachersInfo")

    // MARK: - Joined Classes
    suspend fun getJoinedClassesForStudent(studentId: String): List<JoinedClass> {
        return try {
            println("🔍 Fetching joined classes for student: $studentId")
            println("🔍 Query: JoinedClass collection where StudentID = $studentId and Status = true")
            
            // First, let's check if we can access the collection at all
            val allDocs = joinedClassesCollection.limit(10).get().await()
            println("🔍 Total documents in JoinedClass collection: ${allDocs.size()}")
            
            // Let's see what documents exist
            for (doc in allDocs.documents) {
                println("🔍 Document ID: ${doc.id}")
                println("🔍 Document data: ${doc.data}")
                println("🔍 StudentID field: ${doc.getString("StudentID")}")
                println("🔍 studentID field: ${doc.getString("studentID")}")
                println("🔍 studentId field: ${doc.getString("studentId")}")
                println("🔍 Status field: ${doc.getBoolean("Status")}")
                println("🔍 status field: ${doc.getBoolean("status")}")
            }
            
            // First try without Status filter to see if we can find the student
            val snapshotWithoutStatus = joinedClassesCollection
                .whereEqualTo("StudentID", studentId)
                .get()
                .await()
            
            println("🔍 Found ${snapshotWithoutStatus.size()} documents for student $studentId (without Status filter)")
            
            // Now try with Status filter
            val snapshot = joinedClassesCollection
                .whereEqualTo("StudentID", studentId)
                .whereEqualTo("Status", true)
                .get()
                .await()
            
            println("🔍 Found ${snapshot.size()} documents in JoinedClass collection for student $studentId (with Status filter)")
            
            val joinedClasses = snapshot.documents.mapNotNull { document ->
                println("🔍 Processing document: ${document.id}")
                println("🔍 Document data: ${document.data}")
                
                val joinedClass = JoinedClass(
                    id = document.id,
                    courseId = document.getString("CourseID") ?: "",
                    studentId = document.getString("StudentID") ?: "",
                    joinedDate = document.getTimestamp("Joineddate"),
                    status = document.getBoolean("Status") ?: true
                )
                println("🔍 Created JoinedClass: $joinedClass")
                joinedClass
            }
            
            println("🔍 Returning ${joinedClasses.size} joined classes")
            joinedClasses
        } catch (e: Exception) {
            println("❌ Error fetching joined classes: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getJoinedCoursesForStudent(studentId: String): List<Course> {
        println("🔍 Getting joined courses for student: $studentId")
        val joinedClasses = getJoinedClassesForStudent(studentId)
        println("🔍 Found ${joinedClasses.size} joined classes")
        
        val courses = mutableListOf<Course>()
        
        for (joinedClass in joinedClasses) {
            println("🔍 Fetching course for joined class: ${joinedClass.courseId}")
            val course = getCourseById(joinedClass.courseId)
            course?.let { 
                courses.add(it)
                println("✅ Added course: ${it.name}")
            } ?: println("❌ Course not found for ID: ${joinedClass.courseId}")
        }
        
        println("🔍 Total courses found: ${courses.size}")
        return courses
    }

    // Get all joined classes for the current student
    suspend fun getJoinedClasses(): List<JoinedClass> {
        return getJoinedClassesForStudent(currentStudentId)
    }

    // Get all joined courses for the current student
    suspend fun getJoinedCourses(): List<Course> {
        return getJoinedCoursesForStudent(currentStudentId)
    }

    // Get attendance for the current student in a course
    suspend fun getAttendanceForCourse(courseId: String): List<Attendance> {
        return getAttendanceForStudentInCourse(currentStudentId, courseId)
    }

    // MARK: - Courses
    suspend fun getCourseById(courseId: String): Course? {
        return try {
            println("🔍 Fetching course with ID: $courseId")
            
            // First, let's check if we can access the Courses collection at all
            val allCourses = coursesCollection.limit(10).get().await()
            println("🔍 Total documents in Courses collection: ${allCourses.size()}")
            
            // Let's see what documents exist
            for (doc in allCourses.documents) {
                println("🔍 Course Document ID: ${doc.id}")
                println("🔍 Course Document data: ${doc.data}")
            }
            
            val document = coursesCollection.document(courseId).get().await()
            
            if (document.exists()) {
                println("🔍 Course document found: ${document.data}")
                val course = Course(
                    id = document.id,
                    name = document.getString("Name") ?: "",
                    description = document.getString("Description") ?: "",
                    startTime = document.getString("StartTime") ?: "",
                    endTime = document.getString("EndTime") ?: "",
                    weekDay = document.getString("WeekDay") ?: "",
                    teacherId = document.getString("TeacherID") ?: "",
                    teacherName = "" // Will be populated separately if needed
                )
                println("🔍 Created Course: $course")
                course
            } else {
                println("❌ Course document does not exist for ID: $courseId")
                null
            }
        } catch (e: Exception) {
            println("❌ Error fetching course: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    // MARK: - Assignments
    suspend fun getAssignmentsForCourse(courseId: String): List<Assignment> {
        return try {
            val snapshot = assignmentsCollection
                .whereEqualTo("CourseID", courseId)
                .whereEqualTo("Status", true)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                Assignment(
                    assignmentID = document.id,
                    courseID = document.getString("CourseID") ?: "",
                    title = document.getString("Title") ?: "",
                    description = document.getString("Description") ?: "",
                    handOutDate = document.getTimestamp("HandOutDate")?.seconds?.times(1000) ?: 0L,
                    handInDate = document.getTimestamp("HandInDate")?.seconds?.times(1000) ?: 0L,
                    date = document.getTimestamp("date")?.seconds?.times(1000) ?: 0L,
                    teacherID = document.getString("TeacherID") ?: "",
                    status = document.getBoolean("Status") ?: true,
                    assignmentMaterialLink = "" // Add if you have this field in Firestore
                )
            }
        } catch (e: Exception) {
            println("Error fetching assignments: ${e.message}")
            emptyList()
        }
    }

    // Get assignments and their materials for a course
    suspend fun getAssignmentsWithMaterials(courseId: String): List<Pair<Assignment, List<AssignmentMaterial>>> {
        val assignments = getAssignmentsForCourse(courseId)
        return assignments.map { assignment ->
            val materials = getAssignmentMaterials(assignment.assignmentID)
            assignment to materials
        }
    }

    // MARK: - Assignment Materials
    suspend fun getAssignmentMaterials(assignmentId: String): List<AssignmentMaterial> {
        return try {
            val snapshot = assignmentMaterialsCollection
                .whereEqualTo("AssignmentID", assignmentId)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                AssignmentMaterial(
                    assignmentID = document.getString("AssignmentID") ?: "",
                    link = document.getString("Link") ?: "",
                    date = document.getTimestamp("Date")?.seconds?.times(1000) ?: 0L
                )
            }
        } catch (e: Exception) {
            println("Error fetching assignment materials: ${e.message}")
            emptyList()
        }
    }

    // MARK: - Attendance
    suspend fun getAttendanceForStudent(studentId: String): List<Attendance> {
        return try {
            val snapshot = attendanceCollection
                .whereEqualTo("studentID", studentId)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                Attendance(
                    id = document.id,
                    calendarId = document.getString("calenderID") ?: "",
                    courseId = document.getString("courseID") ?: "",
                    studentId = document.getString("studentID") ?: "",
                    status = document.getString("status") ?: "",
                    timestamp = document.getLong("timestamp") ?: 0L
                )
            }
        } catch (e: Exception) {
            println("Error fetching attendance: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAttendanceForStudentInCourse(studentId: String, courseId: String): List<Attendance> {
        return try {
            val snapshot = attendanceCollection
                .whereEqualTo("studentID", studentId)
                .whereEqualTo("courseID", courseId)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                Attendance(
                    id = document.id,
                    calendarId = document.getString("calenderID") ?: "",
                    courseId = document.getString("courseID") ?: "",
                    studentId = document.getString("studentID") ?: "",
                    status = document.getString("status") ?: "",
                    timestamp = document.getLong("timestamp") ?: 0L
                )
            }
        } catch (e: Exception) {
            println("Error fetching attendance: ${e.message}")
            emptyList()
        }
    }

    // MARK: - Calendar Events
    suspend fun getCalendarEventsForCourse(courseId: String): List<CalendarEvent> {
        return try {
            val snapshot = calendarCollection
                .whereEqualTo("CourceID", courseId)
                .whereEqualTo("Status", true)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                CalendarEvent(
                    id = document.id,
                    courseID = document.getString("CourceID") ?: "",
                    date = document.getTimestamp("Date")?.seconds?.times(1000) ?: 0L,
                    startTime = document.getTimestamp("StartTime")?.seconds?.times(1000) ?: 0L,
                    endTime = document.getTimestamp("EndTime")?.seconds?.times(1000) ?: 0L,
                    hallID = document.getString("HallID") ?: "",
                    status = document.getBoolean("Status") ?: true,
                    courseName = "", // Will be populated separately if needed
                    hallName = "Hall ${document.getString("HallID") ?: ""}"
                )
            }
        } catch (e: Exception) {
            println("Error fetching calendar events: ${e.message}")
            emptyList()
        }
    }

    // MARK: - Course Materials
    suspend fun getCourseMaterials(courseId: String): List<CourseMaterial> {
        return try {
            val snapshot = courseMaterialsCollection
                .whereEqualTo("CourseID", courseId)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                val link = document.getString("Link") ?: ""
                CourseMaterial(
                    courseId = document.getString("CourseID") ?: "",
                    link = link,
                    date = document.getTimestamp("Date")?.seconds?.times(1000) ?: 0L,
                    title = extractTitleFromLink(link),
                    fileType = extractFileTypeFromLink(link)
                )
            }
        } catch (e: Exception) {
            println("Error fetching course materials: ${e.message}")
            emptyList()
        }
    }

    private fun extractTitleFromLink(link: String): String {
        return try {
            val fileName = link.substringAfterLast("/").substringBefore("?")
            if (fileName.isNotEmpty()) fileName else "Course Material"
        } catch (e: Exception) {
            "Course Material"
        }
    }

    private fun extractFileTypeFromLink(link: String): String {
        return try {
            val extension = link.substringAfterLast(".").substringBefore("?")
            when (extension.uppercase()) {
                "PDF" -> "PDF"
                "DOC", "DOCX" -> "DOCX"
                "ZIP", "RAR" -> "ZIP"
                else -> "FILE"
            }
        } catch (e: Exception) {
            "FILE"
        }
    }

    // MARK: - Notifications
    suspend fun getNotificationsForStudent(studentId: String): List<NotificationItem> {
        return try {
            val snapshot = notificationsCollection
                .whereEqualTo("PersonID", studentId)
                .whereEqualTo("Type", "STUDENT")
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                NotificationItem(
                    id = document.id,
                    title = document.getString("Title") ?: "",
                    description = document.getString("Description") ?: "",
                    date = document.getTimestamp("Date")?.seconds?.times(1000) ?: 0L,
                    personID = document.getString("PersonID") ?: "",
                    type = document.getString("Type") ?: "",
                    isRead = false // Default, update if you have this field
                )
            }
        } catch (e: Exception) {
            println("Error fetching notifications: ${e.message}")
            emptyList()
        }
    }

    suspend fun getNotificationsForCurrentStudent(): List<NotificationItem> {
        return getNotificationsForStudent(currentStudentId)
    }

    // MARK: - Results/Submissions
    suspend fun getResultsForStudent(studentId: String): List<ResultsItem> {
        return try {
            val snapshot = submissionsCollection
                .whereEqualTo("StudentID", studentId)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                ResultsItem(
                    assignmentID = document.getString("AssignmentID") ?: "",
                    courseId = document.getString("CourseID") ?: "",
                    studentId = document.getString("StudentID") ?: "",
                    marks = document.getLong("Marks")?.toString() ?: "",
                    date = document.getTimestamp("Date")?.toDate()?.toString() ?: ""
                )
            }
        } catch (e: Exception) {
            println("Error fetching results: ${e.message}")
            emptyList()
        }
    }

    // Get results/submissions for the current student and course
    suspend fun getResultsForCourse(courseId: String): List<ResultsItem> {
        return try {
            val snapshot = submissionsCollection
                .whereEqualTo("StudentID", currentStudentId)
                .whereEqualTo("CourseID", courseId)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                ResultsItem(
                    assignmentID = document.getString("AssignmentID") ?: "",
                    courseId = document.getString("CourseID") ?: "",
                    studentId = document.getString("StudentID") ?: "",
                    marks = document.getLong("Marks")?.toString() ?: "",
                    date = document.getTimestamp("Date")?.toDate()?.toString() ?: ""
                )
            }
        } catch (e: Exception) {
            println("Error fetching results: ${e.message}")
            emptyList()
        }
    }

    // MARK: - Join Class
    suspend fun joinClass(studentId: String, courseId: String): Boolean {
        return try {
            val joinedClass = JoinedClass(
                courseId = courseId,
                studentId = studentId,
                joinedDate = com.google.firebase.Timestamp.now(),
                status = true
            )
            
            joinedClassesCollection.add(joinedClass).await()
            true
        } catch (e: Exception) {
            println("Error joining class: ${e.message}")
            false
        }
    }

    // MARK: - Utility Methods
    suspend fun fixJoinedClassCourseReference() {
        try {
            println("🔧 Fixing JoinedClass course reference...")
            
            // Get the joined class document
            val joinedClassQuery = joinedClassesCollection
                .whereEqualTo("StudentID", "oThb1Ng0sOYRYXKIZ6FC")
                .whereEqualTo("Status", true)
                .get()
                .await()
            
            if (joinedClassQuery.size() > 0) {
                val joinedClassDoc = joinedClassQuery.documents[0]
                println("🔧 Found JoinedClass document: ${joinedClassDoc.id}")
                println("🔧 Current CourseID: ${joinedClassDoc.getString("CourseID")}")
                
                // Get the correct course ID from the Cources collection
                val coursesQuery = coursesCollection.limit(1).get().await()
                if (coursesQuery.size() > 0) {
                    val correctCourseId = coursesQuery.documents[0].id
                    println("🔧 Correct Course ID: $correctCourseId")
                    
                    // Update the JoinedClass document
                    joinedClassesCollection.document(joinedClassDoc.id)
                        .update("CourseID", correctCourseId)
                        .await()
                    
                    println("✅ Updated JoinedClass CourseID to: $correctCourseId")
                }
            }
        } catch (e: Exception) {
            println("❌ Error fixing course reference: ${e.message}")
            e.printStackTrace()
        }
    }

    // MARK: - Student Profile
    suspend fun getStudentAccount(): StudentAccount? {
        return try {
            val document = studentsCollection.document(currentStudentId).get().await()
            if (document.exists()) {
                val statusValue = document.get("Status")
                val statusString = when (statusValue) {
                    is String -> statusValue
                    is Boolean -> statusValue.toString()
                    else -> ""
                }
                StudentAccount(
                    password = document.getString("Password") ?: "",
                    status = statusString,
                    studentID = document.id, // Use document ID as studentID
                    studentInfoID = document.getString("StudentInfoID") ?: ""
                )
            } else null
        } catch (e: Exception) {
            println("Error fetching student account: ${e.message}")
            null
        }
    }

    suspend fun getStudentInfo(studentInfoId: String): StudentInfo? {
        return try {
            val document = studentInfoCollection.document(studentInfoId).get().await()
            if (document.exists()) {
                StudentInfo(
                    address = document.getString("Address") ?: "",
                    city = document.getString("City") ?: "",
                    contactNumber = document.getString("ContactNumber") ?: "",
                    dob = document.getTimestamp("DOB")?.seconds?.times(1000) ?: 0L,
                    email = document.getString("Email") ?: "",
                    fname = document.getString("Fname") ?: "",
                    gender = document.getString("Gender") ?: "",
                    joinedDate = document.getTimestamp("JoinedDate")?.seconds?.times(1000) ?: 0L,
                    lname = document.getString("Lname") ?: "",
                    school = document.getString("School") ?: ""
                )
            } else null
        } catch (e: Exception) {
            println("Error fetching student info: ${e.message}")
            null
        }
    }
} 