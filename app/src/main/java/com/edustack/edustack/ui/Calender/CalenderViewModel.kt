package com.edustack.edustack.ui.Calender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalenderViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is calender Fragment"
    }
    val text: LiveData<String> = _text
}