package com.edustack.edustack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edustack.edustack.databinding.FragmentTeacherAccountSettingsBinding

class TeacherAccountSettingsFragment : Fragment() {

    private var _binding: FragmentTeacherAccountSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        loadUserData()
    }

    private fun setupViews() {
        binding.btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

        binding.btnChangePassword.setOnClickListener {
            changePassword()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun loadUserData() {
        // Load user data from SharedPreferences or database
        val sharedPref = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)

        binding.etFullName.setText(sharedPref.getString("full_name", ""))
        binding.etSubject.setText(sharedPref.getString("subject", "Mathematics"))
        binding.etEmployeeId.setText(sharedPref.getString("employee_id", "TCH001"))
    }

    private fun updateProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val subject = binding.etSubject.text.toString().trim()
        val employeeId = binding.etEmployeeId.text.toString().trim()

        if (fullName.isEmpty() || subject.isEmpty() || employeeId.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Save to SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("full_name", fullName)
            putString("subject", subject)
            putString("employee_id", employeeId)
            apply()
        }

        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
    }

    private fun changePassword() {
        // Implement change password functionality
        Toast.makeText(requireContext(), "Change password clicked", Toast.LENGTH_SHORT).show()
        // You can navigate to a change password fragment or show a dialog
    }

    private fun logout() {
        // Clear user session
        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        // Navigate to login activity
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        // Add your login activity navigation here
        // val intent = Intent(requireContext(), LoginActivity::class.java)
        // startActivity(intent)
        // requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}