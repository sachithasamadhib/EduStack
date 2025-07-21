package com.edustack.edustack.ui.Teacher.Classes

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edustack.edustack.databinding.FragmentTeacherClassBinding
import java.text.SimpleDateFormat
import java.util.*

class ClassFragment : Fragment() {

    private var _binding: FragmentTeacherClassBinding? = null
    private val binding get() = _binding!!

    private lateinit var classViewModel: ClassViewModel
    private lateinit var classAdapter: ClassAdapter
    private var selectedDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        classViewModel = ViewModelProvider(this)[ClassViewModel::class.java]
        _binding = FragmentTeacherClassBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        classViewModel.loadClasses()
        classViewModel.loadClassStats()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to fragment
        classViewModel.loadClasses()
        classViewModel.loadClassStats()
    }

    private fun setupRecyclerView() {
        classAdapter = ClassAdapter(
            onFilesClick = { classItem ->
                showMaterialTypeDialog(classItem)
            },
            onCancelClick = { classItem ->
                showCancelClassDialog(classItem)
            }
        )

        binding.recyclerViewClasses.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = classAdapter
        }
    }

    private fun setupObservers() {
        classViewModel.classes.observe(viewLifecycleOwner) { classes ->
            classAdapter.submitList(classes)
        }

        classViewModel.classStats.observe(viewLifecycleOwner) { stats ->
            binding.textActiveClasses.text = stats.activeClasses.toString()
            binding.textTotalStudents.text = stats.totalStudents.toString()
        }

        classViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        classViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddClass.setOnClickListener {
            startActivity(Intent(context, AddClassActivity::class.java))
        }

        binding.buttonFilterByDate.setOnClickListener {
            showDatePicker()
        }

        binding.buttonClearFilter.setOnClickListener {
            selectedDate = null
            classViewModel.loadClasses()
            binding.textSelectedDate.visibility = View.GONE
            binding.buttonClearFilter.visibility = View.GONE
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.time

                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                binding.textSelectedDate.text = "Filtered by: ${dateFormat.format(selectedDate!!)}"
                binding.textSelectedDate.visibility = View.VISIBLE
                binding.buttonClearFilter.visibility = View.VISIBLE

                classViewModel.loadClassesByDate(selectedDate!!)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showMaterialTypeDialog(classItem: ClassItem) {
        val options = arrayOf("Course Materials", "General Materials")

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Material Type")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openFilePicker(classItem, "course_materials")
                    1 -> openFilePicker(classItem, "general_materials")
                }
            }
            .show()
    }

    private fun openFilePicker(classItem: ClassItem, materialType: String) {
        val intent = Intent(context, FilePickerActivity::class.java).apply {
            putExtra("CLASS_ID", classItem.id)
            putExtra("MATERIAL_TYPE", materialType)
        }
        startActivity(intent)
    }

    private fun showCancelClassDialog(classItem: ClassItem) {
        val intent = Intent(context, CancelClassActivity::class.java).apply {
            putExtra("CLASS_ID", classItem.id)
            putExtra("CLASS_NAME", classItem.name)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}