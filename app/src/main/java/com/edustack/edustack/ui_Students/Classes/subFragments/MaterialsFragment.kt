package com.edustack.edustack.ui_Students.Classes.subFragments

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
import com.edustack.edustack.adapters.MaterialsAdapter
import com.edustack.edustack.databinding.FragmentClassMaterialsBinding
import com.edustack.edustack.model.CourseMaterial
import com.edustack.edustack.repository.StudentRepository
import kotlinx.coroutines.launch

class MaterialsFragment : Fragment() {

    private var _binding: FragmentClassMaterialsBinding? = null
    private val binding get() = _binding!!

    private lateinit var courseId: String
    private lateinit var courseName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassMaterialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get arguments passed from CourseDetailFragment
        courseId = arguments?.getString("courseId") ?: ""
        courseName = arguments?.getString("courseName") ?: "Course"

        // Set the course title
        binding.courseTitle.text = "$courseName - Materials"

        // Load materials data
        loadMaterialsData()
    }

    private fun loadMaterialsData() {
        // Commented out dummy data usage
        // val dummyMaterialsData = getDummyMaterialsData()
        // val adapter = MaterialsAdapter(dummyMaterialsData) { material ->
        //     downloadMaterial(material)
        // }
        // binding.materialsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // binding.materialsRecyclerView.adapter = adapter

        // Firebase integration: Load course materials for this course
        val repository = StudentRepository()
        val adapter = MaterialsAdapter(emptyList()) { material ->
            downloadMaterial(material)
        }
        binding.materialsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.materialsRecyclerView.adapter = adapter

        lifecycleScope.launch {
            val materials = repository.getCourseMaterials(courseId)
            adapter.updateData(materials)
        }
    }

    // Commented out dummy data function
    // private fun getDummyMaterialsData(): List<CourseMaterial> {
    //     return listOf(
    //         CourseMaterial(
    //             courseId = courseId,
    //             date = 1720500000000L, // July 9, 2025
    //             link = "https://drive.google.com/file/d/1ABC123/view?usp=sharing",
    //             title = "Introduction to Programming - Chapter 1",
    //             fileType = "PDF"
    //         ),
    //         CourseMaterial(
    //             courseId = courseId,
    //             date = 1720586400000L, // July 10, 2025
    //             link = "https://drive.google.com/file/d/2DEF456/view?usp=sharing",
    //             title = "Programming Fundamentals - Lecture Notes",
    //             fileType = "PDF"
    //         ),
    //         CourseMaterial(
    //             courseId = courseId,
    //             date = 1720672800000L, // July 11, 2025
    //             link = "https://drive.google.com/file/d/3GHI789/view?usp=sharing",
    //             title = "Practice Exercises - Week 1",
    //             fileType = "DOCX"
    //         ),
    //         CourseMaterial(
    //             courseId = courseId,
    //             date = 1720759200000L, // July 12, 2025
    //             link = "https://drive.google.com/file/d/4JKL012/view?usp=sharing",
    //             title = "Sample Code Examples",
    //             fileType = "ZIP"
    //         ),
    //         CourseMaterial(
    //             courseId = courseId,
    //             date = 1720845600000L, // July 13, 2025
    //             link = "https://drive.google.com/file/d/5MNO345/view?usp=sharing",
    //             title = "Assignment Guidelines",
    //             fileType = "PDF"
    //         ),
    //         CourseMaterial(
    //             courseId = courseId,
    //             date = 1720932000000L, // July 14, 2025
    //             link = "https://drive.google.com/file/d/6PQR678/view?usp=sharing",
    //             title = "Reference Materials",
    //             fileType = "PDF"
    //         )
    //     )
    // }

    private fun downloadMaterial(material: CourseMaterial) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(material.link))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unable to open link", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}