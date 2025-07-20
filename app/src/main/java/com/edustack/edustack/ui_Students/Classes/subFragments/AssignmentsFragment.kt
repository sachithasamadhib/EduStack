package com.edustack.edustack.ui_Students.Classes.subFragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.edustack.edustack.adapters.AssignmentsAdapter
import com.edustack.edustack.databinding.DialogUploadAssignmentBinding
import com.edustack.edustack.databinding.FragmentClassAssignmentsBinding
import com.edustack.edustack.model.Assignment
import com.edustack.edustack.utils.FileUploadHelper

class AssignmentsFragment : Fragment() {

    private var _binding: FragmentClassAssignmentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var courseId: String
    private lateinit var courseName: String
    private lateinit var fileUploadHelper: FileUploadHelper
    private var selectedFileUri: Uri? = null
    private var selectedFileName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassAssignmentsBinding.inflate(inflater, container, false)
        fileUploadHelper = FileUploadHelper(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get arguments passed from CourseDetailFragment
        courseId = arguments?.getString("courseId") ?: ""
        courseName = arguments?.getString("courseName") ?: "Course"

        // Set the course title
        binding.courseTitle.text = "$courseName - Assignments"

        // Load assignments data
        loadAssignmentsData()
    }

    private fun loadAssignmentsData() {
        // TODO: Replace with Firebase data
        val dummyAssignmentsData = getDummyAssignmentsData()

        // Create and set adapter
        val adapter = AssignmentsAdapter(dummyAssignmentsData) { assignment, action ->
            when (action) {
                "download" -> downloadAssignment(assignment)
                "submit" -> showUploadDialog(assignment)
            }
        }
        binding.assignmentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.assignmentsRecyclerView.adapter = adapter
    }

    private fun getDummyAssignmentsData(): List<Assignment> {
        // ... your existing dummy data ...
        return listOf(
            Assignment(
                assignmentID = "ASS001",
                courseID = courseId,
                description = "Create a simple calculator application using Java. The calculator should support basic arithmetic operations (+, -, *, /) and have a graphical user interface.",
                handInDate = 1754006400000L
                , // July 10, 2025
                handOutDate = 1720656000000L, // July 31, 2025
                status = true,
                teacherID = "1",
                title = "Calculator Application",
                date = 1720656000000L, // August 1, 2025
                assignmentMaterialLink = "https://drive.google.com/file/d/1ABC123/view?usp=sharing"
            ),
            // ... add more assignments as needed
        )
    }

    private fun downloadAssignment(assignment: Assignment) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(assignment.assignmentMaterialLink))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unable to open assignment link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showUploadDialog(assignment: Assignment) {
        val dialogBinding = DialogUploadAssignmentBinding.inflate(layoutInflater)

        // Set assignment title
        dialogBinding.assignmentTitleText.text = "Assignment: ${assignment.title}"

        // Reset file selection
        selectedFileUri = null
        selectedFileName = ""
        dialogBinding.selectedFileName.text = "No file selected"
        dialogBinding.uploadButton.isEnabled = false

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        // Select file button
        dialogBinding.selectFileButton.setOnClickListener {
            fileUploadHelper.pickFile { uri, fileName ->
                selectedFileUri = uri
                selectedFileName = fileName
                dialogBinding.selectedFileName.text = "Selected: $fileName"
                dialogBinding.uploadButton.isEnabled = true
            }
        }

        // Cancel button
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        // Upload button
        dialogBinding.uploadButton.setOnClickListener {
            selectedFileUri?.let { uri ->
                uploadAssignment(assignment, uri, selectedFileName)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun uploadAssignment(assignment: Assignment, fileUri: Uri, fileName: String) {
        // Show loading
        Toast.makeText(requireContext(), "Uploading assignment...", Toast.LENGTH_SHORT).show()

        // TODO: Implement Firebase upload here
        // For now, simulate upload
        simulateUpload(assignment, fileUri, fileName)
    }

    private fun simulateUpload(assignment: Assignment, fileUri: Uri, fileName: String) {
        // Simulate upload delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // TODO: Replace with actual Firebase upload
            val submissionData = mapOf(
                "assignmentID" to assignment.assignmentID,
                "courseID" to assignment.courseID,
                "studentID" to "1", // TODO: Get actual student ID
                "date" to System.currentTimeMillis(),
                "fileName" to fileName,
                "fileUri" to fileUri.toString()
            )

            // TODO: Upload to Firebase Submissions collection
            // TODO: Upload file to Firebase Storage
            // TODO: Create SubmissionMaterials entry

            Toast.makeText(requireContext(), "Assignment uploaded successfully!", Toast.LENGTH_LONG).show()

            // TODO: Refresh the assignments list to show submission status

        }, 2000) // 2 second delay to simulate upload
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}