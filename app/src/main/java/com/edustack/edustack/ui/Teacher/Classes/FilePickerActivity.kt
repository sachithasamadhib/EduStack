package com.edustack.edustack.ui.Teacher.Classes

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edustack.edustack.databinding.ActivityFilePickerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class FilePickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilePickerBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var classId: String
    private lateinit var materialType: String
    private var selectedFileUri: Uri? = null

    companion object {
        private const val FILE_PICKER_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classId = intent.getStringExtra("CLASS_ID") ?: ""
        materialType = intent.getStringExtra("MATERIAL_TYPE") ?: ""

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        val title = if (materialType == "course_materials") "Course Materials" else "General Materials"
        binding.textMaterialType.text = title
        supportActionBar?.title = "Upload $title"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupClickListeners() {
        binding.buttonSelectFile.setOnClickListener {
            openFilePicker()
        }

        binding.buttonUpload.setOnClickListener {
            selectedFileUri?.let { uri ->
                uploadFile(uri)
            } ?: run {
                Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Select File"), FILE_PICKER_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedFileUri = uri
                val fileName = getFileName(uri)
                binding.textSelectedFile.text = "Selected: $fileName"
                binding.buttonUpload.isEnabled = true
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "Unknown"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }
        return fileName
    }

    private fun uploadFile(uri: Uri) {
        lifecycleScope.launch {
            try {
                binding.buttonUpload.isEnabled = false
                binding.progressBar.visibility = android.view.View.VISIBLE
                binding.textUploadStatus.text = "Uploading..."

                val fileName = getFileName(uri)
                val fileExtension = fileName.substringAfterLast(".", "")
                val uniqueFileName = "${UUID.randomUUID()}_$fileName"

                // Upload to Firebase Storage
                val storageRef = storage.reference
                    .child("course_materials")
                    .child(classId)
                    .child(uniqueFileName)

                val uploadTask = storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await()

                val googleDriveLink = "https://drive.google.com/file/d/${UUID.randomUUID()}/view"

                // Save to Firestore
                val materialData = hashMapOf(
                    "CourseID" to classId,
                    "Date" to com.google.firebase.Timestamp.now(),
                    "Link" to googleDriveLink,
                    "FileName" to fileName,
                    "FileType" to fileExtension,
                    "UploadedBy" to auth.currentUser?.uid,
                    "MaterialType" to materialType
                )

                firestore.collection("CourseMaterials")
                    .add(materialData)
                    .await()

                binding.textUploadStatus.text = "Upload successful!"

                Toast.makeText(
                    this@FilePickerActivity,
                    "File uploaded successfully!",
                    Toast.LENGTH_LONG
                ).show()

                // Wait a moment then finish
                kotlinx.coroutines.delay(1500)
                finish()

            } catch (e: Exception) {
                binding.textUploadStatus.text = "Upload failed"
                Toast.makeText(
                    this@FilePickerActivity,
                    "Error uploading file: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.buttonUpload.isEnabled = true
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}