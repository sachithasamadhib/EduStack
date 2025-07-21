package com.edustack.edustack

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        // Auto-login if already logged in
        val role = sharedPreferences.getString("role", null)
        if (role != null) {
            navigateToActivity(role)
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        db = FirebaseFirestore.getInstance()

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when {
                username.endsWith("@student.lk") ->
                    loginRole("StudentAcc", "UserName", "Password", username, password, "student", true)
                username.endsWith("@teacher.lk") ->
                    loginRole("TeacherAcc", "UserName", "Password", username, password, "teacher", false)
                username.endsWith("@admin.lk") ->
                    loginRole("Admin", "UserName", "password", username, password, "admin", false)
                else ->
                    Toast.makeText(this, "Invalid email domain", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginRole(
        collection: String,
        usernameField: String,
        passwordField: String,
        username: String,
        password: String,
        role: String,
        checkStatus: Boolean
    ) {
        db.collection(collection)
            .whereEqualTo(usernameField, username)
            .whereEqualTo(passwordField, password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.first()
                    if (checkStatus) {
                        val isActive = doc.getBoolean("Status") ?: false
                        if (!isActive) {
                            Toast.makeText(this, "Account is not active", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }
                    }

                    saveLogin(role)
                    navigateToActivity(role)
                    finishAffinity() // Clears login from back stack
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveLogin(role: String) {
        sharedPreferences.edit()
            .putString("role", role)
            .apply()
    }

    private fun navigateToActivity(role: String) {
        val intent = when (role) {
            "student" -> Intent(this, Students_MainActivity::class.java)
            "teacher" -> Intent(this, TeacherActivity::class.java)
            "admin" -> Intent(this, AdminMenu::class.java)
            else -> return
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
