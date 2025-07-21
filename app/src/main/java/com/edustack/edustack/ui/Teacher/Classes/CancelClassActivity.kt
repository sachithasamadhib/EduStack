package com.edustack.edustack.ui.Teacher.Classes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.edustack.edustack.databinding.ActivityCancelClassBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class CancelClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCancelClassBinding
    private lateinit var classViewModel: ClassViewModel
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var classId: String
    private lateinit var className: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCancelClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classId = intent.getStringExtra("CLASS_ID") ?: ""
        className = intent.getStringExtra("CLASS_NAME") ?: ""

        classViewModel = ViewModelProvider(this)[ClassViewModel::class.java]

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.textClassName.text = "Cancel: $className"
        supportActionBar?.title = "Cancel Class"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupClickListeners() {
        binding.buttonSubmitCancellation.setOnClickListener {
            val reason = binding.editTextReason.text.toString().trim()

            if (reason.isEmpty()) {
                binding.editTextReason.error = "Please provide a reason for cancellation"
                return@setOnClickListener
            }

            submitCancellationRequest(reason)
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun submitCancellationRequest(reason: String) {
        binding.buttonSubmitCancellation.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE
        classViewModel.cancelClass(classId, reason)

        Toast.makeText(
            this,
            "Cancellation request submitted successfully!",
            Toast.LENGTH_LONG
        ).show()

        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}