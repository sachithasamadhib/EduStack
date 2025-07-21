package com.edustack.edustack

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class AllClasses : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_classes)

        val updateBtn = findViewById<Button>(R.id.updateBottomSheetBtn)
        val viewClassBtn = findViewById<Button>(R.id.viewClassBtn)

        viewClassBtn.setOnClickListener {
            val dialogView = BottomSheetDialog(this)
            val viewBottom = layoutInflater.inflate(R.layout.view_class_info, null)
            val closePanelBtn = viewBottom.findViewById<Button>(R.id.closePanelBtn)
            closePanelBtn.setOnClickListener {
                dialogView.dismiss()
            }
            dialogView.setCancelable(false)
            dialogView.setContentView(viewBottom)
            dialogView.show()
        }

        updateBtn.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_update_class, null)

            // Cancel button
            val dismissButton = view.findViewById<Button>(R.id.cancelButton)

            // Time picker dialogs
            val timePicker = Dialog(this)
            timePicker.setContentView(R.layout.time_picker)

            val timePickerEnd = Dialog(this)
            timePickerEnd.setContentView(R.layout.time_picker_end)

            // Start time
            val getTimeStart: Button = view.findViewById(R.id.timeShowBottom)
            val timeUI: TimePicker = timePicker.findViewById(R.id.timePickerUI)
            val setTimeBtn: Button = timePicker.findViewById(R.id.setTimeButton)

            // End time
            val getTimeEnd: Button = view.findViewById(R.id.timeShowEndBottom)
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

            dismissButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
        }
    }
}
