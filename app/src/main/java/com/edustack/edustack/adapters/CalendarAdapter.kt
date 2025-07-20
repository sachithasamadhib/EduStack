package com.edustack.edustack.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.databinding.ItemCalendarEventBinding
import com.edustack.edustack.model.CalendarEvent
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private var eventsList: List<CalendarEvent>,
    private val onEventClick: (CalendarEvent) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(
        private val binding: ItemCalendarEventBinding,
        private val onEventClick: (CalendarEvent) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: CalendarEvent) {
            binding.courseName.text = event.courseName
            binding.hallName.text = "Hall: ${event.hallName}"
            binding.dateText.text = formatDate(event.date)
            binding.timeText.text = formatTimeRange(event.startTime, event.endTime)

            // Set status indicator
            if (event.status) {
                binding.statusIndicator.setBackgroundColor(0xFF4CAF50.toInt()) // Green for active
                binding.statusText.text = "ACTIVE"
                binding.statusText.setTextColor(0xFF4CAF50.toInt())
            } else {
                binding.statusIndicator.setBackgroundColor(0xFFF44336.toInt()) // Red for inactive
                binding.statusText.text = "CANCELLED"
                binding.statusText.setTextColor(0xFFF44336.toInt())
            }

            // Set click listener
            binding.root.setOnClickListener {
                onEventClick(event)
            }
        }

        private fun formatDate(timestamp: Long): String {
            return try {
                val date = Date(timestamp)
                val format = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
                format.format(date)
            } catch (e: Exception) {
                "Unknown Date"
            }
        }

        private fun formatTimeRange(startTime: Long, endTime: Long): String {
            return try {
                val startDate = Date(startTime)
                val endDate = Date(endTime)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                "${timeFormat.format(startDate)} - ${timeFormat.format(endDate)}"
            } catch (e: Exception) {
                "Unknown Time"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CalendarViewHolder(binding, onEventClick)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(eventsList[position])
    }

    override fun getItemCount(): Int = eventsList.size

    fun updateEvents(events: List<CalendarEvent>) {
        eventsList = events
        notifyDataSetChanged()
    }

    fun filterByDate(selectedDate: Long) {
        val filteredEvents = eventsList.filter {
            isSameDay(it.date, selectedDate)
        }
        updateEvents(filteredEvents)
    }

    fun clearFilter() {
        // This will be handled by the fragment
    }

    private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}

