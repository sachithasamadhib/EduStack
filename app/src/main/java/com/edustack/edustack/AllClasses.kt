package com.edustack.edustack

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
            // set cancelable to avoid closing of dialog box when clicking on the screen.
            dialogView.setCancelable(false)
            // set content view to our view.
            dialogView.setContentView(viewBottom)
            // call a show method to display a dialog
            dialogView.show()
        }
        updateBtn.setOnClickListener {
            val dialog = BottomSheetDialog(this)

            // inflate the layout file of bottom sheet
            val view = layoutInflater.inflate(R.layout.bottom_sheet_update_class, null)

            // initialize variable for dismiss button
            val dismissButton = view.findViewById<Button>(R.id.cancelButton)

            //start time
            val timePicker = Dialog(this)
            timePicker.setContentView(R.layout.time_picker)
            //end time
            val timePickerEnd = Dialog(this)
            timePickerEnd.setContentView(R.layout.time_picker_end)

            val getTimeStart: Button = view.findViewById<Button>(R.id.timeShowBottom)//button 1 (start time)
            val timeUI: TimePicker = timePicker.findViewById(R.id.timePickerUI)//start date
            val setTimeBtn: Button = timePicker.findViewById(R.id.setTimeButton)//start date

            val getTimeEnd: Button = view.findViewById(R.id.timeShowEndBottom)//button 2 (end time)
            val timeUIEnd: TimePicker = timePickerEnd.findViewById(R.id.timePickerUIEnd)//end
            val setTimeBtnEnd: Button = timePickerEnd.findViewById(R.id.setTimeButtonEnd)//end
            getTimeEnd.setOnClickListener{
                timePickerEnd.window?.setLayout(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                timePickerEnd.setCancelable(false)
                timePickerEnd.show()
            }
            getTimeStart.setOnClickListener{
                timePicker.window?.setLayout(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                timePicker.setCancelable(false)
                timePicker.show()

            }
            timeUI.setOnTimeChangedListener { view, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                getTimeStart.setText(selectedTime)
            }
            timeUIEnd.setOnTimeChangedListener { view, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                getTimeEnd.setText(selectedTime)
            }
            setTimeBtn.setOnClickListener {
                timePicker.dismiss()
            }
            setTimeBtnEnd.setOnClickListener {
                timePickerEnd.dismiss()
            }

            // on click event for dismiss button
            dismissButton.setOnClickListener {
                // call dismiss method to close the dialog
                dialog.dismiss()
            }
            // set cancelable to avoid closing of dialog box when clicking on the screen.
            dialog.setCancelable(false)
            // set content view to our view.
            dialog.setContentView(view)
            // call a show method to display a dialog
            dialog.show()
        }
    }
}