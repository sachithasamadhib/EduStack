package com.edustack.edustack.ui_Students.Calender

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.edustack.edustack.adapters.CalendarAdapter
import com.edustack.edustack.databinding.FragmentCalenderBinding
import com.edustack.edustack.model.CalendarEvent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalenderFragment : Fragment() {

    private var _binding: FragmentCalenderBinding? = null
    private val binding get() = _binding!!

    private lateinit var calendarAdapter: CalendarAdapter
    private var allEvents = mutableListOf<CalendarEvent>()
    private var filteredEvents = mutableListOf<CalendarEvent>()
    private var isFiltered = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadCalendarData()
        setupButtons()
        updateCalendarView()
    }

    private fun setupRecyclerView() {
        calendarAdapter = CalendarAdapter(
            eventsList = emptyList(),
            onEventClick = { event ->
                showEventDetails(event)
            }
        )

        binding.calendarRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.calendarRecyclerView.adapter = calendarAdapter
    }

    private fun loadCalendarData() {
        // TODO: Replace with Firebase data
        allEvents = getDummyCalendarData().toMutableList()
        filteredEvents = allEvents.toMutableList()
        calendarAdapter.updateEvents(filteredEvents)
        updateEmptyState()
    }

    private fun getDummyCalendarData(): List<CalendarEvent> {
        return listOf(
            CalendarEvent(
                id = "1",
                courseID = "1",
                date = 1722240000000L, // July 30, 2025
                endTime = 1722240000000L + (3600000L * 2L), // 2 hours later
                hallID = "1",
                startTime = 1722240000000L,
                status = true,
                courseName = "Programming Fundamentals",
                hallName = "Computer Lab A"
            ),
            CalendarEvent(
                id = "2",
                courseID = "1",
                date = 1722326400000L, // July 31, 2025
                endTime = 1722326400000L + (3600000L * 1L) + (1800000L), // 1.5 hours later (1 hour + 30 minutes)
                hallID = "2",
                startTime = 1722326400000L,
                status = true,
                courseName = "Database Design",
                hallName = "Lecture Hall B"
            ),
            CalendarEvent(
                id = "3",
                courseID = "1",
                date = 1722412800000L, // August 1, 2025
                endTime = 1722412800000L + (3600000L * 3L), // 3 hours later
                hallID = "3",
                startTime = 1722412800000L,
                status = true,
                courseName = "Web Development",
                hallName = "Computer Lab C"
            ),
            CalendarEvent(
                id = "4",
                courseID = "1",
                date = 1722499200000L, // August 2, 2025
                endTime = 1722499200000L + (3600000L * 2L), // 2 hours later
                hallID = "1",
                startTime = 1722499200000L,
                status = false, // Cancelled
                courseName = "Mobile App Development",
                hallName = "Computer Lab A"
            ),
            CalendarEvent(
                id = "5",
                courseID = "1",
                date = 1722585600000L, // August 3, 2025
                endTime = 1722585600000L + (3600000L * 2L) + (1800000L), // 2.5 hours later (2 hours + 30 minutes)
                hallID = "2",
                startTime = 1722585600000L,
                status = true,
                courseName = "Software Engineering",
                hallName = "Lecture Hall B"
            ),
            CalendarEvent(
                id = "6",
                courseID = "1",
                date = 1722672000000L, // August 4, 2025
                endTime = 1722672000000L + (3600000L * 1L), // 1 hour later
                hallID = "3",
                startTime = 1722672000000L,
                status = true,
                courseName = "Project Presentation",
                hallName = "Computer Lab C"
            ),
            CalendarEvent(
                id = "7",
                courseID = "1",
                date = 1722758400000L, // August 5, 2025
                endTime = 1722758400000L + (3600000L * 2L), // 2 hours later
                hallID = "1",
                startTime = 1722758400000L,
                status = true,
                courseName = "Final Exam",
                hallName = "Computer Lab A"
            )
        )
    }

    private fun setupButtons() {
        binding.selectDateButton.setOnClickListener {
            showDatePicker()
        }

        binding.clearFiltersButton.setOnClickListener {
            clearFilters()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                filterByDate(selectedCalendar.timeInMillis)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun filterByDate(selectedDate: Long) {
        isFiltered = true
        filteredEvents = allEvents.filter {
            isSameDay(it.date, selectedDate)
        }.toMutableList()

        calendarAdapter.updateEvents(filteredEvents)
        updateCalendarView()
        updateEmptyState()

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val selectedDateString = dateFormat.format(Date(selectedDate))
        binding.selectedDateText.text = "Showing events for: $selectedDateString"
        binding.selectedDateText.visibility = View.VISIBLE
    }

    private fun clearFilters() {
        isFiltered = false
        filteredEvents = allEvents.toMutableList()
        calendarAdapter.updateEvents(filteredEvents)
        updateCalendarView()
        updateEmptyState()

        binding.selectedDateText.visibility = View.GONE
    }

    private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun updateCalendarView() {
        // Update calendar view with events
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            .format(calendar.time)
        binding.currentMonthText.text = monthName

        // Count events for current month
        val monthEvents = allEvents.filter {
            val eventCal = Calendar.getInstance().apply { timeInMillis = it.date }
            eventCal.get(Calendar.MONTH) == currentMonth &&
                    eventCal.get(Calendar.YEAR) == currentYear
        }

        binding.eventsCountText.text = "${monthEvents.size} events this month"
    }

    private fun updateEmptyState() {
        if (filteredEvents.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.calendarRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.calendarRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun showEventDetails(event: CalendarEvent) {
        val message = """
            Course: ${event.courseName}
            Date: ${SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(Date(event.date))}
            Time: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(event.startTime))} - ${
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                Date(event.endTime)
            )}
            Hall: ${event.hallName}
            Status: ${if (event.status) "Active" else "Cancelled"}
        """.trimIndent()

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}