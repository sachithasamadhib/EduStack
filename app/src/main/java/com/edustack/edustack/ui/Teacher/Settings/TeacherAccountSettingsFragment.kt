package com.edustack.edustack

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edustack.edustack.databinding.FragmentTeacherAccountSettingsBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class TeacherAccountSettingsFragment : Fragment() {
    private var _binding: FragmentTeacherAccountSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private var isLoading = false

    private val teacherDocumentId = "pIz2T40tozP5BNuC1YbF"

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

        firestore = FirebaseFirestore.getInstance()

        setupViews()
        loadUserDataFromFirestore()
    }

    private fun setupViews() {
        val genderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = genderAdapter

        binding.etDob.setOnClickListener { showDatePicker() }
        binding.btnUpdateProfile.setOnClickListener { updateProfile() }
        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentDateText = binding.etDob.text.toString()

        if (currentDateText.isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val currentDate = dateFormat.parse(currentDateText)
                if (currentDate != null) calendar.time = currentDate
            } catch (_: Exception) {}
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etDob.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadUserDataFromFirestore() {
        setLoading(true)

        Toast.makeText(requireContext(), "Loading profile data...", Toast.LENGTH_SHORT).show()
        firestore.collection("TeachersInfo")
            .document(teacherDocumentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    try {
                        binding.etFirstName.setText(document.getString("Fname") ?: "")
                        binding.etLastName.setText(document.getString("Lname") ?: "")
                        binding.etEmail.setText(document.getString("Email") ?: "")
                        binding.etContactNo.setText(document.getString("ContactNo") ?: "")
                        binding.etAddress.setText(document.getString("Address") ?: "")
                        binding.etCity.setText(document.getString("City") ?: "")
                        binding.etSpeciality.setText(document.getString("Speciality") ?: "")

                        val dobTimestamp = document.getTimestamp("DOB")
                        dobTimestamp?.let {
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            binding.etDob.setText(dateFormat.format(it.toDate()))
                        }

                        val gender = document.getString("Gender") ?: "Male"
                        val genderArray = resources.getStringArray(R.array.gender_array)
                        val genderPosition = genderArray.indexOf(gender)
                        if (genderPosition >= 0) {
                            binding.spinnerGender.setSelection(genderPosition)
                        }

                        Toast.makeText(requireContext(), "Profile data loaded successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error parsing profile data: ${e.message}", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(requireContext(), "No profile data found", Toast.LENGTH_LONG).show()
                    clearAllFields()
                }
                setLoading(false)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load profile: ${it.message}", Toast.LENGTH_LONG).show()
                it.printStackTrace()
                setLoading(false)
            }
    }

    private fun updateProfile() {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val contactNo = binding.etContactNo.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val speciality = binding.etSpeciality.text.toString().trim()
        val dobString = binding.etDob.text.toString().trim()
        val gender = binding.spinnerGender.selectedItem.toString()

        if (firstName.isEmpty()) { binding.etFirstName.error = "Required"; return }
        if (lastName.isEmpty()) { binding.etLastName.error = "Required"; return }
        if (email.isEmpty()) { binding.etEmail.error = "Required"; return }
        if (contactNo.isEmpty()) { binding.etContactNo.error = "Required"; return }
        if (address.isEmpty()) { binding.etAddress.error = "Required"; return }
        if (city.isEmpty()) { binding.etCity.error = "Required"; return }
        if (speciality.isEmpty()) { binding.etSpeciality.error = "Required"; return }
        if (dobString.isEmpty()) {
            Toast.makeText(requireContext(), "Please select DOB", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        val dobDate = try {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dobString)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Invalid DOB format", Toast.LENGTH_SHORT).show()
            setLoading(false)
            return
        }

        val updateData = hashMapOf(
            "Fname" to firstName,
            "Lname" to lastName,
            "Email" to email,
            "ContactNo" to contactNo,
            "Address" to address,
            "City" to city,
            "Speciality" to speciality,
            "DOB" to com.google.firebase.Timestamp(dobDate!!),
            "Gender" to gender,
            "JoinedDate" to com.google.firebase.Timestamp.now()
        )

        firestore.collection("TeachersInfo")
            .document(teacherDocumentId)
            .set(updateData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_LONG).show()
                setLoading(false)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update profile: ${it.message}", Toast.LENGTH_LONG).show()
                it.printStackTrace()
                setLoading(false)
            }
    }

    private fun clearAllFields() {
        binding.etFirstName.setText("")
        binding.etLastName.setText("")
        binding.etEmail.setText("")
        binding.etContactNo.setText("")
        binding.etAddress.setText("")
        binding.etCity.setText("")
        binding.etSpeciality.setText("")
        binding.etDob.setText("")
        binding.spinnerGender.setSelection(0)
    }

    private fun logout() {
        val userPrefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val loginPrefs = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        userPrefs.edit().clear().apply()
        loginPrefs.edit().clear().apply()

        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setLoading(loading: Boolean) {
        isLoading = loading
        binding.btnUpdateProfile.isEnabled = !loading
        binding.btnUpdateProfile.text = if (loading) "Updating..." else "Update Profile"
        binding.etFirstName.isEnabled = !loading
        binding.etLastName.isEnabled = !loading
        binding.etEmail.isEnabled = !loading
        binding.etContactNo.isEnabled = !loading
        binding.etAddress.isEnabled = !loading
        binding.etCity.isEnabled = !loading
        binding.etSpeciality.isEnabled = !loading
        binding.etDob.isEnabled = !loading
        binding.spinnerGender.isEnabled = !loading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
