package com.edustack.edustack.ui_Students.Classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.edustack.edustack.R
import com.edustack.edustack.adapters.ClassAdapter
import com.edustack.edustack.databinding.FragmentClassesBinding
import com.edustack.edustack.model.ClassItem
import androidx.lifecycle.lifecycleScope
import com.edustack.edustack.repository.StudentRepository
import kotlinx.coroutines.launch

class ClassesFragment : Fragment() {

    private var _binding: FragmentClassesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Commented out dummy data usage
        // val dummyList = getDummyClasses()
        // val adapter = ClassAdapter(dummyList) { selectedClass ->
        //     val bundle = Bundle().apply {
        //         putString("courseId", selectedClass.id)
        //         putString("courseName", selectedClass.name)
        //     }
        //     findNavController().navigate(
        //         R.id.action_navigation_classes_to_courseDetailFragment,
        //         bundle
        //     )
        // }
        // binding.classesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // binding.classesRecyclerView.adapter = adapter

        // Firebase integration: Load joined courses for the student
        val repository = StudentRepository()
        val adapter = ClassAdapter(emptyList()) { selectedClass ->
            val bundle = Bundle().apply {
                putString("courseId", selectedClass.id)
                putString("courseName", selectedClass.name)
            }
            findNavController().navigate(
                R.id.action_navigation_classes_to_courseDetailFragment,
                bundle
            )
        }
        binding.classesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.classesRecyclerView.adapter = adapter

        lifecycleScope.launch {
            val courses = repository.getJoinedCourses()
            // Map Course to ClassItem for adapter
            val classItems = courses.map {
                com.edustack.edustack.model.ClassItem(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    weekDay = it.weekDay
                )
            }
            adapter.updateData(classItems)
        }
    }

    // Commented out dummy data function
    // private fun getDummyClasses(): List<ClassItem> {
    //     return listOf(
    //         ClassItem(
    //             id = "1",
    //             name = "Mathematics",
    //             description = "Algebra and Geometry",
    //             startTime = "6:30",
    //             endTime = "8:30",
    //             weekDay = "Monday"
    //         ),
    //         ClassItem(
    //             id = "2",
    //             name = "Science",
    //             description = "Physics and Chemistry",
    //             startTime = "9:00",
    //             endTime = "11:00",
    //             weekDay = "Wednesday"
    //         ),
    //         ClassItem(
    //             id = "3",
    //             name = "English",
    //             description = "Grammar and Literature",
    //             startTime = "11:30",
    //             endTime = "1:00",
    //             weekDay = "Friday"
    //         )
    //     )
    // }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}