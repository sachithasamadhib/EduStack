package com.edustack.edustack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.databinding.ItemNotificationBinding
import com.edustack.edustack.model.NotificationItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsAdapter(
    private var notificationsList: List<NotificationItem>,
    private val onNotificationClick: (NotificationItem) -> Unit,
    private val onMarkAsRead: (NotificationItem) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(
        private val binding: ItemNotificationBinding,
        private val onNotificationClick: (NotificationItem) -> Unit,
        private val onMarkAsRead: (NotificationItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: NotificationItem) {
            binding.notificationTitle.text = notification.title
            binding.notificationDescription.text = notification.description
            binding.notificationDate.text = formatDate(notification.date)

            // Set read/unread status
            if (notification.isRead) {
                binding.notificationCard.setCardBackgroundColor(0xFFF5F5F5.toInt())
                binding.unreadIndicator.visibility = android.view.View.GONE
            } else {
                binding.notificationCard.setCardBackgroundColor(0xFFFFFFFF.toInt())
                binding.unreadIndicator.visibility = android.view.View.VISIBLE
            }

            // Set click listeners
            binding.root.setOnClickListener {
                onNotificationClick(notification)
            }

            binding.markAsReadButton.setOnClickListener {
                onMarkAsRead(notification)
            }
        }

        private fun formatDate(timestamp: Long): String {
            return try {
                val date = Date(timestamp)
                val format = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                format.format(date)
            } catch (e: Exception) {
                "Unknown Date"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NotificationViewHolder(binding, onNotificationClick, onMarkAsRead)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notificationsList[position])
    }

    override fun getItemCount(): Int = notificationsList.size

    fun updateNotifications(notifications: List<NotificationItem>) {
        notificationsList = notifications
        notifyDataSetChanged()
    }

    fun getUnreadCount(): Int {
        return notificationsList.count { !it.isRead }
    }
}