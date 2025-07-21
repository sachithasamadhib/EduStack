package com.edustack.edustack.Controller

import androidx.lifecycle.ViewModel
import com.edustack.edustack.Models.StudentAccounts
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId

class AccountDetails : ViewModel() {
    val db = Firebase.firestore
    suspend fun getStudentAccDetails() : List<StudentAccounts> = withContext(Dispatchers.IO) {
        try{
            val list1 = mutableListOf<StudentAccounts>()
            db.collection("StudentInfo")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val accObject = StudentAccounts(
                            address = document.getString("Address") ?: "",
                            city = document.getString("City") ?: "",
                            contactNumber = document.getString("ContactNumber") ?: "",
                            dob = document.getTimestamp("DOB")?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime() ?: LocalDateTime.MIN,
                            email = document.getString("Email") ?: "",
                            firstName = document.getString("Fname") ?: "",
                            gender = document.getString("Gender") ?: "",
                            joinedDate = document.getTimestamp("JoinedDate")?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime() ?: LocalDateTime.MIN,
                            lastName = document.getString("Lname") ?: "",
                            school = document.getString("School") ?: ""
                        )
                        list1.add(accObject)
                    }
                }
                .addOnFailureListener { exception ->
                    throw Exception("Error occured when reading data (AccountDetails) ${exception.toString()}")
                }
            return@withContext list1
        }catch (e : Exception){
            throw Exception("Error occured when reading data (AccountDetails) ${e.toString()}")
        }
    }
}