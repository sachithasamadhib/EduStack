package com.edustack.edustack.ui_Students.Settings

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.edustack.edustack.adapters.SettingsAdapter
import com.edustack.edustack.adapters.SettingsOption
import com.edustack.edustack.databinding.FragmentSettingsBinding
import com.edustack.edustack.model.StudentAccount
import com.edustack.edustack.model.StudentInfo
import com.edustack.edustack.repository.StudentRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsAdapter: SettingsAdapter
    private var studentAccount: StudentAccount? = null
    private var studentInfo: StudentInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Commented out dummy data usage
        // loadUserData()
        // setupUserProfile()
        // setupSettingsOptions()

        // Firebase integration: Load student profile and set up UI
        val repository = StudentRepository()
        lifecycleScope.launch {
            studentAccount = repository.getStudentAccount()
            studentInfo = studentAccount?.studentInfoID?.let { repository.getStudentInfo(it) }
            setupUserProfile()
            setupSettingsOptions()
        }
    }

    // Commented out dummy data function
    // private fun loadUserData() {
    //     // TODO: Replace with Firebase data
    //     studentAccount = getDummyStudentAccount()
    //     studentInfo = getDummyStudentInfo()
    // }

    // private fun getDummyStudentAccount(): StudentAccount {
    //     return StudentAccount(
    //         password = "20015646",
    //         status = "ACTIVE",
    //         studentID = "user1234",
    //         studentInfoID = "1"
    //     )
    // }

    // private fun getDummyStudentInfo(): StudentInfo {
    //     return StudentInfo(
    //         address = "123 Main Street",
    //         city = "Kurunegala",
    //         contactNumber = "+94778733392",
    //         dob = 1466700000000L, // June 23, 2016
    //         email = "raveen@gmail.com",
    //         fname = "Raveen",
    //         gender = "Male",
    //         joinedDate = 1721580000000L, // July 15, 2025
    //         lname = "Perera",
    //         school = "APC"
    //     )
    // }

    private fun setupUserProfile() {
        studentInfo?.let { info ->
            binding.userNameText.text = "${info.fname} ${info.lname}"
            binding.userEmailText.text = info.email
            binding.userSchoolText.text = info.school
            binding.userStatusText.text = studentAccount?.status ?: "UNKNOWN"

            // Format joined date
            val joinedDate = Date(info.joinedDate)
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.joinedDateText.text = "Joined: ${dateFormat.format(joinedDate)}"
        }
    }

    private fun setupSettingsOptions() {
        val settingsOptions = listOf(
            SettingsOption(
                title = "Profile",
                subtitle = "View and edit your profile information",
                icon = android.R.drawable.ic_menu_myplaces,
                action = "profile"
            ),
            SettingsOption(
                title = "Help Center",
                subtitle = "Get help and find answers to common questions",
                icon = android.R.drawable.ic_menu_help,
                action = "help"
            ),
            SettingsOption(
                title = "Report Error",
                subtitle = "Report bugs or issues with the application",
                icon = android.R.drawable.ic_menu_report_image,
                action = "report"
            ),
            SettingsOption(
                title = "Contact Support",
                subtitle = "Get in touch with our support team",
                icon = android.R.drawable.ic_menu_send,
                action = "contact"
            ),
            SettingsOption(
                title = "About App",
                subtitle = "Version 1.0.0 - EduStack Student Portal",
                icon = android.R.drawable.ic_menu_info_details,
                action = "about"
            ),
            SettingsOption(
                title = "Logout",
                subtitle = "Sign out of your account",
                icon = android.R.drawable.ic_menu_close_clear_cancel,
                action = "logout"
            )
        )

        settingsAdapter = SettingsAdapter(settingsOptions) { option ->
            handleSettingsAction(option.action)
        }

        binding.settingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.settingsRecyclerView.adapter = settingsAdapter
    }

    private fun handleSettingsAction(action: String) {
        when (action) {
            "profile" -> showProfileDialog()
            "help" -> showHelpCenter()
            "report" -> showReportErrorDialog()
            "contact" -> contactSupport()
            "about" -> showAboutDialog()
            "logout" -> showLogoutDialog()
        }
    }

    private fun showProfileDialog() {
        studentInfo?.let { info ->
            val profileText = """
                Full Name: ${info.fname} ${info.lname}
                Email: ${info.email}
                Phone: ${info.contactNumber}
                Address: ${info.address}
                City: ${info.city}
                School: ${info.school}
                Gender: ${info.gender}
                Date of Birth: ${formatDate(info.dob)}
                Joined Date: ${formatDate(info.joinedDate)}
                Student ID: ${studentAccount?.studentID}
                Status: ${studentAccount?.status}
            """.trimIndent()

            AlertDialog.Builder(requireContext())
                .setTitle("Profile Information")
                .setMessage(profileText)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun showHelpCenter() {
        val helpText = """
            Welcome to EduStack Help Center!
            
            Frequently Asked Questions:
            
            Q: How do I view my assignments?
            A: Go to Classes → Select a course → Click Assignments
            
            Q: How do I mark attendance?
            A: Go to Attendance → Show QR code to your teacher
            
            Q: How do I view my results?
            A: Go to Classes → Select a course → Click Results
            
            Q: How do I access course materials?
            A: Go to Classes → Select a course → Click Materials
            
            For more help, contact support.
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Help Center")
            .setMessage(helpText)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showReportErrorDialog() {
        val reportText = """
            Report an Error
            
            Please describe the issue you encountered:
            - What were you trying to do?
            - What happened instead?
            - When did this occur?
            
            You can also contact us at:
            Email: support@edustack.com
            Phone: +94 11 234 5678
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Report Error")
            .setMessage(reportText)
            .setPositiveButton("Contact Support") { _, _ ->
                contactSupport()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun contactSupport() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@edustack.com")
            putExtra(Intent.EXTRA_SUBJECT, "EduStack Support Request")
            putExtra(Intent.EXTRA_TEXT, "Student ID: ${studentAccount?.studentID}\n\nDescribe your issue here:")
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAboutDialog() {
        val aboutText = """
            EduStack Student Portal
            Version 1.0.0
            
            A comprehensive student management system
            designed to enhance the learning experience.
            
            Features:
            • Course Management
            • Assignment Submission
            • Attendance Tracking
            • Results Viewing
            • Material Access
            • Calendar Integration
            
            © 2025 EduStack. All rights reserved.
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("About")
            .setMessage(aboutText)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                // Sign out from Firebase Auth
                FirebaseAuth.getInstance().signOut()
                // Clear loginPrefs role
                requireContext().getSharedPreferences("loginPrefs", AppCompatActivity.MODE_PRIVATE)
                    .edit()
                    .remove("role")
                    .apply()
                // Navigate to login and clear back stack
                val intent = Intent(requireContext(), com.edustack.edustack.LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun formatDate(timestamp: Long): String {
        return try {
            val date = Date(timestamp)
            val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            format.format(date)
        } catch (e: Exception) {
            "Unknown Date"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}