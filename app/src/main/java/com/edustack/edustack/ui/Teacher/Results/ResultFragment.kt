package com.edustack.edustack.ui.Teacher.Results

import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.R
import com.edustack.edustack.databinding.FragmentTeacherResultsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.text.SimpleDateFormat
import java.util.*

class ResultFragment : Fragment() {
    private var _binding: FragmentTeacherResultsBinding? = null
    private val binding get() = _binding!!

    private lateinit var resultViewModel: ResultViewModel
    private lateinit var submissionsAdapter: SubmissionsAdapter

    private var currentFilter = FilterOptions()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        resultViewModel = ViewModelProvider(this)[ResultViewModel::class.java]
        _binding = FragmentTeacherResultsBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupFilterUI()
        observeViewModel()

        val teacherId = "PBZSdZadxy0D9ovdcJ8x"
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

    private fun setupFilterUI() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                currentFilter = currentFilter.copy(searchQuery = s.toString())
                resultViewModel.applyFilter(currentFilter)
            }
        })

        binding.buttonFilter.setOnClickListener {
            showFilterBottomSheet()
        }

        binding.buttonClearFilters.setOnClickListener {
            clearAllFilters()
        }
    }

    private fun observeViewModel() {
        resultViewModel.submissions.observe(viewLifecycleOwner) { submissions ->
            submissionsAdapter.updateSubmissions(submissions)
            binding.textEmptyState.visibility = if (submissions.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewSubmissions.visibility = if (submissions.isEmpty()) View.GONE else View.VISIBLE
            binding.textResultsCount.text = "${submissions.size} submissions found"
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
                val teacherId = "PBZSdZadxy0D9ovdcJ8x"
                resultViewModel.loadSubmissions(teacherId)
            }
        }
    }

    private fun showFilterBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = LayoutInflater.from(context)
            .inflate(R.layout.bottom_sheet_filter, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val spinnerCourse = bottomSheetView.findViewById<Spinner>(R.id.spinnerCourse)
        val spinnerAssignment = bottomSheetView.findViewById<Spinner>(R.id.spinnerAssignment)
        val editTextMinMarks = bottomSheetView.findViewById<EditText>(R.id.editTextMinMarks)
        val editTextMaxMarks = bottomSheetView.findViewById<EditText>(R.id.editTextMaxMarks)
        val buttonDateFrom = bottomSheetView.findViewById<Button>(R.id.buttonDateFrom)
        val buttonDateTo = bottomSheetView.findViewById<Button>(R.id.buttonDateTo)
        val buttonApplyFilter = bottomSheetView.findViewById<Button>(R.id.buttonApplyFilter)
        val buttonClearFilter = bottomSheetView.findViewById<Button>(R.id.buttonClearFilter)

        resultViewModel.courses.observe(viewLifecycleOwner) { courses ->
            val courseNames = mutableListOf("All Courses")
            courseNames.addAll(courses.map { it.name })

            val courseAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courseNames)
            courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCourse.adapter = courseAdapter
            currentFilter.selectedCourseId?.let { selectedId ->
                val index = courses.indexOfFirst { it.id == selectedId }
                if (index >= 0) spinnerCourse.setSelection(index + 1)
            }
        }

        resultViewModel.assignments.observe(viewLifecycleOwner) { assignments ->
            val assignmentTitles = mutableListOf("All Assignments")
            assignmentTitles.addAll(assignments.map { it.title })

            val assignmentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, assignmentTitles)
            assignmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAssignment.adapter = assignmentAdapter

            currentFilter.selectedAssignmentId?.let { selectedId ->
                val index = assignments.indexOfFirst { it.id == selectedId }
                if (index >= 0) spinnerAssignment.setSelection(index + 1)
            }
        }

        editTextMinMarks.setText(currentFilter.minMarks?.toString() ?: "")
        editTextMaxMarks.setText(currentFilter.maxMarks?.toString() ?: "")

        // Date picker functionality
        var selectedDateFrom: Long? = currentFilter.dateFrom
        var selectedDateTo: Long? = currentFilter.dateTo

        buttonDateFrom.setOnClickListener {
            showDatePicker { date ->
                selectedDateFrom = date
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                buttonDateFrom.text = "From: ${sdf.format(Date(date))}"
            }
        }

        buttonDateTo.setOnClickListener {
            showDatePicker { date ->
                selectedDateTo = date
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                buttonDateTo.text = "To: ${sdf.format(Date(date))}"
            }
        }

        selectedDateFrom?.let {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            buttonDateFrom.text = "From: ${sdf.format(Date(it))}"
        }
        selectedDateTo?.let {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            buttonDateTo.text = "To: ${sdf.format(Date(it))}"
        }

        buttonApplyFilter.setOnClickListener {
            val courses = resultViewModel.courses.value ?: emptyList()
            val assignments = resultViewModel.assignments.value ?: emptyList()

            val selectedCourseId = if (spinnerCourse.selectedItemPosition > 0) {
                courses[spinnerCourse.selectedItemPosition - 1].id
            } else null

            val selectedAssignmentId = if (spinnerAssignment.selectedItemPosition > 0) {
                assignments[spinnerAssignment.selectedItemPosition - 1].id
            } else null

            val minMarks = editTextMinMarks.text.toString().toIntOrNull()
            val maxMarks = editTextMaxMarks.text.toString().toIntOrNull()

            currentFilter = FilterOptions(
                selectedCourseId = selectedCourseId,
                selectedAssignmentId = selectedAssignmentId,
                minMarks = minMarks,
                maxMarks = maxMarks,
                dateFrom = selectedDateFrom,
                dateTo = selectedDateTo,
                searchQuery = currentFilter.searchQuery
            )

            resultViewModel.applyFilter(currentFilter)
            updateActiveFiltersDisplay()
            bottomSheetDialog.dismiss()
        }

        buttonClearFilter.setOnClickListener {
            clearAllFilters()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun clearAllFilters() {
        currentFilter = FilterOptions()
        binding.editTextSearch.setText("")
        resultViewModel.clearFilters()
        updateActiveFiltersDisplay()
    }

    private fun updateActiveFiltersDisplay() {
        binding.chipGroupActiveFilters.removeAllViews()

        val courses = resultViewModel.courses.value ?: emptyList()
        val assignments = resultViewModel.assignments.value ?: emptyList()

        currentFilter.selectedCourseId?.let { courseId ->
            val course = courses.find { it.id == courseId }
            course?.let {
                addFilterChip("Course: ${it.name}") {
                    currentFilter = currentFilter.copy(selectedCourseId = null)
                    resultViewModel.applyFilter(currentFilter)
                    updateActiveFiltersDisplay()
                }
            }
        }

        currentFilter.selectedAssignmentId?.let { assignmentId ->
            val assignment = assignments.find { it.id == assignmentId }
            assignment?.let {
                addFilterChip("Assignment: ${it.title}") {
                    currentFilter = currentFilter.copy(selectedAssignmentId = null)
                    resultViewModel.applyFilter(currentFilter)
                    updateActiveFiltersDisplay()
                }
            }
        }

        if (currentFilter.minMarks != null || currentFilter.maxMarks != null) {
            val marksText = when {
                currentFilter.minMarks != null && currentFilter.maxMarks != null ->
                    "Marks: ${currentFilter.minMarks}-${currentFilter.maxMarks}"
                currentFilter.minMarks != null -> "Marks: ≥${currentFilter.minMarks}"
                currentFilter.maxMarks != null -> "Marks: ≤${currentFilter.maxMarks}"
                else -> ""
            }
            if (marksText.isNotEmpty()) {
                addFilterChip(marksText) {
                    currentFilter = currentFilter.copy(minMarks = null, maxMarks = null)
                    resultViewModel.applyFilter(currentFilter)
                    updateActiveFiltersDisplay()
                }
            }
        }

        if (currentFilter.dateFrom != null || currentFilter.dateTo != null) {
            val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
            val dateText = when {
                currentFilter.dateFrom != null && currentFilter.dateTo != null ->
                    "Date: ${sdf.format(Date(currentFilter.dateFrom!!))} - ${sdf.format(Date(currentFilter.dateTo!!))}"
                currentFilter.dateFrom != null -> "From: ${sdf.format(Date(currentFilter.dateFrom!!))}"
                currentFilter.dateTo != null -> "To: ${sdf.format(Date(currentFilter.dateTo!!))}"
                else -> ""
            }
            if (dateText.isNotEmpty()) {
                addFilterChip(dateText) {
                    currentFilter = currentFilter.copy(dateFrom = null, dateTo = null)
                    resultViewModel.applyFilter(currentFilter)
                    updateActiveFiltersDisplay()
                }
            }
        }

        binding.chipGroupActiveFilters.visibility =
            if (binding.chipGroupActiveFilters.childCount > 0) View.VISIBLE else View.GONE
    }

    private fun addFilterChip(text: String, onRemove: () -> Unit) {
        val chip = Chip(requireContext())
        chip.text = text
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener { onRemove() }
        binding.chipGroupActiveFilters.addView(chip)
    }

    private fun showSubmissionBottomSheet(submission: SubmissionData) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView = LayoutInflater.from(context)
            .inflate(R.layout.bottom_sheet_submission_details, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val textAssignmentTitle = bottomSheetView.findViewById<TextView>(R.id.textAssignmentTitle)
        val textStudentName = bottomSheetView.findViewById<TextView>(R.id.textStudentName)
        val textCourseName = bottomSheetView.findViewById<TextView>(R.id.textCourseName)
        val textCurrentMarks = bottomSheetView.findViewById<TextView>(R.id.textCurrentMarks)
        val editTextMarks = bottomSheetView.findViewById<EditText>(R.id.editTextMarks)
        val buttonUpdateMarks = bottomSheetView.findViewById<Button>(R.id.buttonUpdateMarks)
        val recyclerViewMaterials = bottomSheetView.findViewById<RecyclerView>(R.id.recyclerViewMaterials)

        textAssignmentTitle.text = submission.assignmentTitle
        textStudentName.text = submission.studentName
        textCourseName?.text = "Course: ${submission.courseName}"
        textCurrentMarks.text = "Current Marks: ${submission.marks}"
        editTextMarks.setText(submission.marks.toString())

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
            if (newMarks != null && newMarks >= 0) {
                resultViewModel.updateMarks(submission.submissionId, newMarks)
                bottomSheetDialog.dismiss()
            } else {
                Toast.makeText(context, "Please enter valid marks (0 or greater)", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetDialog.show()
    }

    private fun downloadFile(url: String) {
        try {
            if (url.isBlank()) {
                Toast.makeText(context, "Invalid download URL", Toast.LENGTH_SHORT).show()
                return
            }

            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle("Downloading Submission Material")
            request.setDescription("Downloading file...")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "submission_${System.currentTimeMillis()}"
            )

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
