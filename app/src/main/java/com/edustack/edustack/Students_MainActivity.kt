package com.edustack.edustack

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.edustack.edustack.databinding.ActivityStudentsMainBinding

class Students_MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentsMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStudentsMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_students_main)

        // Setup bottom navigation
        navView.setupWithNavController(navController)

        // Setup custom top bar
        setupCustomTopBar()
    }

    private fun setupCustomTopBar() {
        // Set initial title
        updateTopBarTitle("Classes")

        // Setup back arrow click listener
        binding.customTopBar.backArrow.setOnClickListener {
            if (navController.currentDestination?.id != R.id.navigation_classes) {
                navController.navigateUp()
            }
        }

        // Listen for navigation changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_classes -> {
                    updateTopBarTitle("Classes")
                    binding.customTopBar.backArrow.visibility = android.view.View.GONE
                }
                R.id.navigation_notifications -> {
                    updateTopBarTitle("Notifications")
                    binding.customTopBar.backArrow.visibility = android.view.View.GONE
                }
                R.id.navigation_calender -> {
                    updateTopBarTitle("Calendar")
                    binding.customTopBar.backArrow.visibility = android.view.View.GONE
                }
                R.id.navigation_attendance -> {
                    updateTopBarTitle("Attendance")
                    binding.customTopBar.backArrow.visibility = android.view.View.GONE
                }
                R.id.navigation_settings -> {
                    updateTopBarTitle("Settings")
                    binding.customTopBar.backArrow.visibility = android.view.View.GONE
                }
                R.id.CourseDetailFragment -> {
                    updateTopBarTitle("Course Details")
                    binding.customTopBar.backArrow.visibility = android.view.View.VISIBLE
                }
                R.id.AttendanceFragment -> {
                    updateTopBarTitle("Attendance")
                    binding.customTopBar.backArrow.visibility = android.view.View.VISIBLE
                }
                R.id.ResultsFragment -> {
                    updateTopBarTitle("Results")
                    binding.customTopBar.backArrow.visibility = android.view.View.VISIBLE
                }
                R.id.MaterialsFragment -> {
                    updateTopBarTitle("Materials")
                    binding.customTopBar.backArrow.visibility = android.view.View.VISIBLE
                }
                R.id.AssignmentsFragment -> {
                    updateTopBarTitle("Assignments")
                    binding.customTopBar.backArrow.visibility = android.view.View.VISIBLE
                }
                else -> {
                    updateTopBarTitle("Classes")
                    binding.customTopBar.backArrow.visibility = android.view.View.GONE
                }
            }
        }
    }

    // Add this method to your Students_MainActivity class
    private fun updateNotificationBadge(count: Int) {
        val badge = binding.navView.getOrCreateBadge(R.id.navigation_notifications)
        if (count > 0) {
            badge.number = count
            badge.isVisible = true
        } else {
            badge.isVisible = false
        }
    }

// Call this method when notifications change
// You can get the unread count from your NotificationsFragment

    private fun updateTopBarTitle(title: String) {
        binding.customTopBar.topBarTitle.text = title
    }
}