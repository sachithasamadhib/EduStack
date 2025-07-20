package com.edustack.edustack.ui_Students.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.edustack.edustack.adapters.NotificationsAdapter
import com.edustack.edustack.databinding.FragmentNotificationsBinding
import com.edustack.edustack.model.NotificationItem

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationsAdapter: NotificationsAdapter
    private var notificationsList = mutableListOf<NotificationItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadNotifications()
        setupButtons()
    }

    private fun setupRecyclerView() {
        notificationsAdapter = NotificationsAdapter(
            notificationsList = emptyList(),
            onNotificationClick = { notification ->
                // Handle notification click
                Toast.makeText(requireContext(), "Notification: ${notification.title}", Toast.LENGTH_SHORT).show()
            },
            onMarkAsRead = { notification ->
                markAsRead(notification)
            }
        )

        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationsRecyclerView.adapter = notificationsAdapter
    }

    private fun loadNotifications() {
        // TODO: Replace with Firebase data
        val dummyNotifications = getDummyNotifications()

        // Filter for STUDENT type notifications
        val studentNotifications = dummyNotifications.filter { it.type == "STUDENT" }

        notificationsList.clear()
        notificationsList.addAll(studentNotifications)
        notificationsAdapter.updateNotifications(notificationsList)

        updateEmptyState()
    }

    private fun getDummyNotifications(): List<NotificationItem> {
        return listOf(
            NotificationItem(
                id = "1",
                date = 1720396800000L, // July 8, 2025
                description = "Your assignment 'Calculator Application' has been graded. You received 85/100 marks.",
                personID = "1",
                title = "Assignment Graded",
                type = "STUDENT",
                isRead = false
            ),
            NotificationItem(
                id = "2",
                date = 1720310400000L, // July 7, 2025
                description = "New assignment 'Database Design Project' has been posted. Due date: July 11, 2025.",
                personID = "1",
                title = "New Assignment Posted",
                type = "STUDENT",
                isRead = false
            ),
            NotificationItem(
                id = "3",
                date = 1720224000000L, // July 6, 2025
                description = "Your attendance for today's class has been marked as present.",
                personID = "1",
                title = "Attendance Updated",
                type = "STUDENT",
                isRead = true
            ),
            NotificationItem(
                id = "4",
                date = 1720137600000L, // July 5, 2025
                description = "Course materials for 'Web Development' have been updated. Please check the materials section.",
                personID = "1",
                title = "Course Materials Updated",
                type = "STUDENT",
                isRead = false
            ),
            NotificationItem(
                id = "5",
                date = 1720051200000L, // July 4, 2025
                description = "Your submission for 'Calculator Application' has been received successfully.",
                personID = "1",
                title = "Assignment Submitted",
                type = "STUDENT",
                isRead = true
            ),
            NotificationItem(
                id = "6",
                date = 1719964800000L, // July 3, 2025
                description = "Class schedule has been updated. Next class will be on July 8, 2025 at 10:00 AM.",
                personID = "1",
                title = "Schedule Update",
                type = "STUDENT",
                isRead = false
            ),
            NotificationItem(
                id = "7",
                date = 1719878400000L, // July 2, 2025
                description = "This is a teacher notification that should not appear for students.",
                personID = "1",
                title = "Teacher Only Notification",
                type = "TEACHER",
                isRead = false
            )
        )
    }

    private fun setupButtons() {
        binding.clearAllButton.setOnClickListener {
            clearAllNotifications()
        }

        binding.markAllReadButton.setOnClickListener {
            markAllAsRead()
        }
    }

    private fun markAsRead(notification: NotificationItem) {
        val index = notificationsList.indexOfFirst { it.id == notification.id }
        if (index != -1) {
            notificationsList[index] = notification.copy(isRead = true)
            notificationsAdapter.updateNotifications(notificationsList)
            updateEmptyState()

            // TODO: Update Firebase
            Toast.makeText(requireContext(), "Marked as read", Toast.LENGTH_SHORT).show()
        }
    }

    private fun markAllAsRead() {
        notificationsList = notificationsList.map { it.copy(isRead = true) }.toMutableList()
        notificationsAdapter.updateNotifications(notificationsList)
        updateEmptyState()

        // TODO: Update Firebase
        Toast.makeText(requireContext(), "All notifications marked as read", Toast.LENGTH_SHORT).show()
    }

    private fun clearAllNotifications() {
        notificationsList.clear()
        notificationsAdapter.updateNotifications(notificationsList)
        updateEmptyState()

        // TODO: Update Firebase
        Toast.makeText(requireContext(), "All notifications cleared", Toast.LENGTH_SHORT).show()
    }

    private fun updateEmptyState() {
        if (notificationsList.isEmpty()) {
            binding.emptyStateLayout.visibility = android.view.View.VISIBLE
            binding.notificationsRecyclerView.visibility = android.view.View.GONE
            binding.buttonsLayout.visibility = android.view.View.GONE
        } else {
            binding.emptyStateLayout.visibility = android.view.View.GONE
            binding.notificationsRecyclerView.visibility = android.view.View.VISIBLE
            binding.buttonsLayout.visibility = android.view.View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}