package com.edustack.edustack.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import java.io.File

class FileUploadHelper(private val fragment: Fragment) {

    private var onFileSelected: ((Uri, String) -> Unit)? = null

    private val filePickerLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            val fileName = getFileName(selectedUri)
            onFileSelected?.invoke(selectedUri, fileName)
        }
    }

    fun pickFile(onFileSelected: (Uri, String) -> Unit) {
        this.onFileSelected = onFileSelected
        filePickerLauncher.launch("*/*") // Allow all file types
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "Unknown File"
        fragment.requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        return fileName
    }
}