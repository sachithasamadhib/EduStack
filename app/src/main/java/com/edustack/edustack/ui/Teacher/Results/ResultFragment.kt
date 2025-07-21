package com.edustack.edustack.ui.Teacher.Results

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.R
import com.edustack.edustack.databinding.FragmentTeacherResultsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ResultFragment : Fragment() {

    private var _binding: FragmentTeacherResultsBinding? = null
    private val binding get() = _binding!!

    private lateinit var resultViewModel: ResultViewModel
    private lateinit var submissionsAdapter: SubmissionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        resultViewModel = ViewModelProvider(this)[ResultViewModel::class.java]
        _binding = FragmentTeacherResultsBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()

        val teacherId = "1"

        resultViewModel.loadSubmissions(teacherId)

        return binding.root
    }

    private fun setupRecyclerView() {
        submissionsAdapter = SubmissionsAdapter { submission ->
            showSubmissionBottomSheet(submission)
        }

        binding.recyclerViewSubmissions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = submissionsAdapter
        }
    }

    private fun observeViewModel() {
        resultViewModel.submissions.observe(viewLifecycleOwner) { submissions ->
            submissionsAdapter.updateSubmissions(submissions)
            binding.textEmptyState.visibility = if (submissions.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewSubmissions.visibility = if (submissions.isEmpty()) View.GONE else View.VISIBLE
        }

        resultViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        resultViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
            }
        }

        resultViewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Marks updated successfully", Toast.LENGTH_SHORT).show()
                val teacherId = "1"
                resultViewModel.loadSubmissions(teacherId)
            }
        }
    }

    private fun showSubmissionBottomSheet(submission: SubmissionData) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = LayoutInflater.from(context)
            .inflate(R.layout.bottom_sheet_submission_details, null)

        bottomSheetDialog.setContentView(bottomSheetView)

        val textAssignmentTitle = bottomSheetView.findViewById<TextView>(R.id.textAssignmentTitle)
        val textStudentName = bottomSheetView.findViewById<TextView>(R.id.textStudentName)
        val textCurrentMarks = bottomSheetView.findViewById<TextView>(R.id.textCurrentMarks)
        val editTextMarks = bottomSheetView.findViewById<EditText>(R.id.editTextMarks)
        val buttonUpdateMarks = bottomSheetView.findViewById<Button>(R.id.buttonUpdateMarks)
        val recyclerViewMaterials = bottomSheetView.findViewById<RecyclerView>(R.id.recyclerViewMaterials)

        textAssignmentTitle.text = submission.assignmentTitle
        textStudentName.text = submission.studentName
        textCurrentMarks.text = "Current Marks: ${submission.marks}"
        editTextMarks.setText(submission.marks.toString())

        // Set up materials RecyclerView
        val materialsAdapter = MaterialsAdapter { materialUrl ->
            downloadFile(materialUrl)
        }

        recyclerViewMaterials.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = materialsAdapter
        }

        materialsAdapter.updateMaterials(submission.submissionMaterials)

        buttonUpdateMarks.setOnClickListener {
            val newMarks = editTextMarks.text.toString().toIntOrNull()
            if (newMarks != null) {
                resultViewModel.updateMarks(submission.submissionId, newMarks)
                bottomSheetDialog.dismiss()
            } else {
                Toast.makeText(context, "Please enter valid marks", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetDialog.show()
    }

    private fun downloadFile(url: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle("Downloading Submission Material")
            request.setDescription("Downloading file...")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "submission_${System.currentTimeMillis()}")

            val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error downloading file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
