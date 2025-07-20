package com.edustack.edustack

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TimePicker

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class admin_classes : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_classes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Calendar button
        val calenderBtn: Button = view.findViewById(R.id.btnNewButton)
        calenderBtn.setOnClickListener {
            val intentCalender = Intent(activity, ClassCalender::class.java)
            startActivity(intentCalender)
        }

        // View all classes button
        val btnViewAllClasses: Button = view.findViewById(R.id.btnViewClasses)
        btnViewAllClasses.setOnClickListener {
            val intent = Intent(activity, AllClasses::class.java)
            startActivity(intent)
        }

        // Time Picker Dialogs
        val timePicker = Dialog(requireContext())
        timePicker.setContentView(R.layout.time_picker)

        val timePickerEnd = Dialog(requireContext())
        timePickerEnd.setContentView(R.layout.time_picker_end)

        val getTimeStart: Button = view.findViewById(R.id.timeShow)
        val timeUI: TimePicker = timePicker.findViewById(R.id.timePickerUI)
        val setTimeBtn: Button = timePicker.findViewById(R.id.setTimeButton)

        val getTimeEnd: Button = view.findViewById(R.id.timeShowEnd)
        val timeUIEnd: TimePicker = timePickerEnd.findViewById(R.id.timePickerUIEnd)
        val setTimeBtnEnd: Button = timePickerEnd.findViewById(R.id.setTimeButtonEnd)

        getTimeStart.setOnClickListener {
            timePicker.window?.setLayout(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            timePicker.setCancelable(false)
            timePicker.show()
        }

        getTimeEnd.setOnClickListener {
            timePickerEnd.window?.setLayout(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            timePickerEnd.setCancelable(false)
            timePickerEnd.show()
        }

        timeUI.setOnTimeChangedListener { _, hourOfDay, minute ->
            val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
            getTimeStart.text = selectedTime
        }

        timeUIEnd.setOnTimeChangedListener { _, hourOfDay, minute ->
            val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
            getTimeEnd.text = selectedTime
        }

        setTimeBtn.setOnClickListener {
            timePicker.dismiss()
        }

        setTimeBtnEnd.setOnClickListener {
            timePickerEnd.dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            admin_classes().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
