package com.edustack.edustack.ui_Students.Attendance

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edustack.edustack.databinding.FragmentAttendanceBinding
import com.edustack.edustack.model.StudentAccount
import com.edustack.edustack.utils.QRCodeGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!

    private var currentStudentID: String = ""
    private var qrCodeBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadStudentData()
        setupUI()
    }

    private fun loadStudentData() {
        // TODO: Replace with Firebase data
        val dummyStudentAccount = getDummyStudentAccount()
        currentStudentID = dummyStudentAccount.studentID

        // Generate QR code for attendance
        generateAttendanceQRCode()
    }

    private fun getDummyStudentAccount(): StudentAccount {
        return StudentAccount(
            password = "20015646",
            status = "ACTIVE",
            studentID = "user1234",
            studentInfoID = "1"
        )
    }

    private fun generateAttendanceQRCode() {
        try {
            val qrContent = QRCodeGenerator.generateAttendanceQRCode(currentStudentID)
            qrCodeBitmap = QRCodeGenerator.generateQRCode(qrContent, 400)

            // Display QR code
            binding.qrCodeImageView.setImageBitmap(qrCodeBitmap)

            // Display student info
            binding.studentIdText.text = "Student ID: $currentStudentID"
            binding.qrCodeInfoText.text = "Show this QR code to your teacher to mark attendance"

            // Show timestamp
            val timestamp = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm:ss", Locale.getDefault())
            binding.timestampText.text = "Generated: ${dateFormat.format(Date(timestamp))}"

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error generating QR code: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupUI() {
        binding.refreshQrButton.setOnClickListener {
            generateAttendanceQRCode()
            Toast.makeText(requireContext(), "QR Code refreshed", Toast.LENGTH_SHORT).show()
        }

        binding.copyStudentIdButton.setOnClickListener {
            // Copy student ID to clipboard
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = android.content.ClipData.newPlainText("Student ID", currentStudentID)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Student ID copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}