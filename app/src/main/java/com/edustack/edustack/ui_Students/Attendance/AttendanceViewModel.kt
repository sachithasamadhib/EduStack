package com.edustack.edustack.ui_Students.Attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AttendanceViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Attendance Fragment"
    }
    val text: LiveData<String> = _text
}