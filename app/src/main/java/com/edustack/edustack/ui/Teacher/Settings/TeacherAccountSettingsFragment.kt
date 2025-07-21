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

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        setupViews()
        loadUserDataFromFirestore()
    }

    private fun setupViews() {
        // Setup gender spinner
        val genderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = genderAdapter

        // Setup date picker for DOB
        binding.etDob.setOnClickListener {
            showDatePicker()
        }

        binding.btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        // If there's already a date in the field, use it as the initial date
        val currentDateText = binding.etDob.text.toString()
        if (currentDateText.isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val currentDate = dateFormat.parse(currentDateText)
                if (currentDate != null) {
                    calendar.time = currentDate
                }
            } catch (e: Exception) {
                // Use current date if parsing fails
            }
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etDob.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun loadUserDataFromFirestore() {
        setLoading(true)

        // Show loading message
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
                        if (dobTimestamp != null) {
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            binding.etDob.setText(dateFormat.format(dobTimestamp.toDate()))
                        }

                        val gender = document.getString("Gender") ?: "Male"
                        val genderArray = resources.getStringArray(R.array.gender_array)
                        val genderPosition = genderArray.indexOf(gender)
                        if (genderPosition >= 0) {
                            binding.spinnerGender.setSelection(genderPosition)
                        }

                        Toast.makeText(requireContext(), "Profile data loaded successfully", Toast.LENGTH_SHORT).show()

                        println("Loaded data:")
                        println("First Name: ${document.getString("Fname")}")
                        println("Last Name: ${document.getString("Lname")}")
                        println("Email: ${document.getString("Email")}")
                        println("Contact: ${document.getString("ContactNo")}")
                        println("Address: ${document.getString("Address")}")
                        println("City: ${document.getString("City")}")
                        println("Speciality: ${document.getString("Speciality")}")
                        println("Gender: ${document.getString("Gender")}")

                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error parsing profile data: ${e.message}", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(requireContext(), "No profile data found for document ID: $teacherDocumentId", Toast.LENGTH_LONG).show()
                    clearAllFields()
                }
                setLoading(false)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to load profile: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
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

        if (firstName.isEmpty()) {
            binding.etFirstName.error = "First name is required"
            binding.etFirstName.requestFocus()
            return
        }
        if (lastName.isEmpty()) {
            binding.etLastName.error = "Last name is required"
            binding.etLastName.requestFocus()
            return
        }
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.etEmail.requestFocus()
            return
        }
        if (contactNo.isEmpty()) {
            binding.etContactNo.error = "Contact number is required"
            binding.etContactNo.requestFocus()
            return
        }
        if (address.isEmpty()) {
            binding.etAddress.error = "Address is required"
            binding.etAddress.requestFocus()
            return
        }
        if (city.isEmpty()) {
            binding.etCity.error = "City is required"
            binding.etCity.requestFocus()
            return
        }
        if (speciality.isEmpty()) {
            binding.etSpeciality.error = "Speciality is required"
            binding.etSpeciality.requestFocus()
            return
        }
        if (dobString.isEmpty()) {
            Toast.makeText(requireContext(), "Please select date of birth", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dobDate = try {
            dateFormat.parse(dobString)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Invalid date format. Please use DD/MM/YYYY", Toast.LENGTH_SHORT).show()
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

                // Log the updated data for debugging
                println("Profile updated with data:")
                println("First Name: $firstName")
                println("Last Name: $lastName")
                println("Email: $email")
                println("Contact: $contactNo")
                println("Address: $address")
                println("City: $city")
                println("Speciality: $speciality")
                println("Gender: $gender")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to update profile: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
                setLoading(false)
            }
    }

    private fun logout() {
        // Clear all saved login and user data
        val userPrefs = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val loginPrefs = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        userPrefs.edit().clear().apply()
        loginPrefs.edit().clear().apply()


        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Start LoginActivity with closing others activities
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)

        // Finish the current activity that hosts this fragment
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