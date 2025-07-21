package com.edustack.edustack.ui.Teacher.Classes

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.edustack.edustack.databinding.ActivityAddClassBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddClassBinding
    private lateinit var classViewModel: ClassViewModel
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classViewModel = ViewModelProvider(this)[ClassViewModel::class.java]

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        supportActionBar?.title = "Add New Class"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val weekDays = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, weekDays)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWeekDay.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.buttonSave.setOnClickListener {
            saveClass()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveClass() {
        val className = binding.editTextClassName.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val startTime = binding.editTextStartTime.text.toString().trim()
        val endTime = binding.editTextEndTime.text.toString().trim()
        val weekDay = binding.spinnerWeekDay.selectedItem.toString()
        val defaultHall = binding.editTextDefaultHall.text.toString().trim()

        if (validateInputs(className, startTime, endTime, defaultHall)) {
            binding.buttonSave.isEnabled = false
            binding.progressBar.visibility = android.view.View.VISIBLE

            classViewModel.addClass(
                name = className,
                description = description,
                startTime = startTime,
                endTime = endTime,
                weekDay = weekDay,
                defaultHall = defaultHall
            )

            Toast.makeText(this, "Class added successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun validateInputs(className: String, startTime: String, endTime: String, defaultHall: String): Boolean {
        when {
            className.isEmpty() -> {
                binding.editTextClassName.error = "Class name is required"
                return false
            }
            startTime.isEmpty() -> {
                binding.editTextStartTime.error = "Start time is required"
                return false
            }
            endTime.isEmpty() -> {
                binding.editTextEndTime.error = "End time is required"
                return false
            }
            defaultHall.isEmpty() -> {
                binding.editTextDefaultHall.error = "Hall is required"
                return false
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}