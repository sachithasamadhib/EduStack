package com.edustack.edustack.ui_Students.Classes.subFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.edustack.edustack.R
import com.edustack.edustack.databinding.FragmentCourseDetailBinding

class CourseDetailFragment : Fragment() {
    private var _binding: FragmentCourseDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseDetailBinding.inflate(inflater, container, false)

        // Show course title from arguments
        val courseName = arguments?.getString("courseName") ?: "Unknown Course"
        binding.courseTitle.text = courseName

        // Buttons are visible and ready for navigation
        binding.materialsButton.setOnClickListener {
            // TODO: Navigate to MaterialsFragment
        }
        binding.assignmentsButton.setOnClickListener {
            // TODO: Navigate to AssignmentsFragment
        }
        binding.attendanceButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("courseId", arguments?.getString("courseId") ?: "")
                putString("courseName", arguments?.getString("courseName") ?: "")
            }
            findNavController().navigate(
                R.id.action_CourseDetailFragment_to_AttendanceFragment,
                bundle
            )
        }
        binding.resultsButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("courseId", arguments?.getString("courseId") ?: "")
                putString("courseName", arguments?.getString("courseName") ?: "")
            }
            findNavController().navigate(
                R.id.action_CourseDetailFragment_to_ResultsFragment,
                bundle
            )
        }

        binding.materialsButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("courseId", arguments?.getString("courseId") ?: "")
                putString("courseName", arguments?.getString("courseName") ?: "")
            }
            findNavController().navigate(
                R.id.action_CourseDetailFragment_to_MaterialsFragment,
                bundle
            )
        }
        binding.assignmentsButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("courseId", arguments?.getString("courseId") ?: "")
                putString("courseName", arguments?.getString("courseName") ?: "")
            }
            findNavController().navigate(
                R.id.action_CourseDetailFragment_to_AssignmentsFragment,
                bundle
            )
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

