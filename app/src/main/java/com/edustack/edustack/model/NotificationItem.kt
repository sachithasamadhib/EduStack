package com.edustack.edustack.model

data class NotificationItem(
    val id: String = "",
    val date: Long = 0L, // timestamp
    val description: String = "",
    val personID: String = "",
    val title: String = "",
    val type: String = "",
    val isRead: Boolean = false
)