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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.edustack.edustack.adapters.AssignmentsAdapter
import com.edustack.edustack.adapters.ResultsAdapter
import com.edustack.edustack.databinding.FragmentClassResultsBinding
import com.edustack.edustack.model.Assignment
import com.edustack.edustack.model.ResultsItem
import com.edustack.edustack.repository.StudentRepository
import kotlinx.coroutines.launch

class ResultsFragment : Fragment() {

    private var _binding: FragmentClassResultsBinding? = null
    private val binding get() = _binding!!

    private lateinit var courseId: String
    private lateinit var courseName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get arguments passed from CourseDetailFragment
        courseId = arguments?.getString("courseId") ?: ""
        courseName = arguments?.getString("courseName") ?: "Course"

        // Set the course title
        binding.courseTitle.text = "$courseName - Results"

        // Load results data
        loadResultsData()
    }

    private fun loadResultsData() {
        // Commented out dummy data usage
        // val dummyResultsData = getDummyResultsData()
        // val adapter = ResultsAdapter(dummyResultsData)
        // binding.resultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // binding.resultsRecyclerView.adapter = adapter

        // Firebase integration: Load results for the current student in this course
        val repository = StudentRepository()
        val adapter = ResultsAdapter(emptyList())
        binding.resultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.resultsRecyclerView.adapter = adapter

        lifecycleScope.launch {
            val results = repository.getResultsForCourse(courseId)
            adapter.updateData(results)
        }
    }

    // Commented out dummy data function
    // private fun getDummyResultsData(): List<ResultsItem> {
    //     return listOf(
    //         ResultsItem(
    //             assignmentID = "ASS001",
    //             courseId = courseId,
    //             studentId = "1",
    //             date = "2024-01-10",
    //             marks = "85"
    //         ),
    //         ResultsItem(
    //             assignmentID = "ASS002",
    //             courseId = courseId,
    //             studentId = "1",
    //             date = "2024-01-15",
    //             marks = "92"
    //         ),
    //         ResultsItem(
    //             assignmentID = "ASS003",
    //             courseId = courseId,
    //             studentId = "1",
    //             date = "2024-01-20",
    //             marks = "78"
    //         ),
    //         ResultsItem(
    //             assignmentID = "ASS004",
    //             courseId = courseId,
    //             studentId = "1",
    //             date = "2024-01-25",
    //             marks = "95"
    //         ),
    //         ResultsItem(
    //             assignmentID = "ASS005",
    //             courseId = courseId,
    //             studentId = "1",
    //             date = "2024-01-30",
    //             marks = "88"
    //         ),
    //         ResultsItem(
    //             assignmentID = "ASS006",
    //             courseId = courseId,
    //             studentId = "1",
    //             date = "2024-02-05",
    //             marks = "91"
    //         )
    //     )
    // }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}