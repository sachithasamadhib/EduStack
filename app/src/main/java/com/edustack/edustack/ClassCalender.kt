package com.edustack.edustack

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialog

class ClassCalender : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_class_calender)
        //create calender event
        //bottom sheet
        val createCalender = findViewById<Button>(R.id.createCalenderEventBtn)
        createCalender.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.edit_calender_event, null)

            //pop up setup (calender)
            val datePickerStart = Dialog(this)
            datePickerStart.setContentView(R.layout.date_picker_start)

            val changeEventDateBtn = view.findViewById<Button>(R.id.change_event_btn_new)//trigger btn for calender popup (create calender event)
            val dateUIStart: DatePicker = datePickerStart.findViewById(R.id.datePickerStart)//date ui/ start
            val dateActionBtnStart: Button = datePickerStart.findViewById(R.id.setDateStart)//date select btn/end
            changeEventDateBtn.setOnClickListener {
                datePickerStart.window?.setLayout(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                datePickerStart.setCancelable(false)
                datePickerStart.show()
            }
            dateActionBtnStart.setOnClickListener {
                with(dateUIStart){
                    val day = dayOfMonth
                    val month = month
                    val year = year
                    changeEventDateBtn.text = "$day/$month/$year"
                }
                datePickerStart.dismiss()
            }
            //end
            //popup clock (start time)
            //start time
            val timePicker = Dialog(this)
            timePicker.setContentView(R.layout.time_picker)
            val startTiBot = view.findViewById<Button>(R.id.timeShowBottom)//start time trigger button
            val timeUI: TimePicker = timePicker.findViewById(R.id.timePickerUI)//start date
            val setTimeBtn: Button = timePicker.findViewById(R.id.setTimeButton)//start date
            startTiBot.setOnClickListener {
                timePicker.window?.setLayout(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                timePicker.setCancelable(false)
                timePicker.show()
            }
            timeUI.setOnTimeChangedListener { view, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                startTiBot.setText(selectedTime)
            }
            setTimeBtn.setOnClickListener {
                timePicker.dismiss()
            }
            //end
            //popup clock (end time)
            val timePickerEnd = Dialog(this)
            timePickerEnd.setContentView(R.layout.time_picker_end)
            val endTiBot = view.findViewById<Button>(R.id.timeShowEndBottom)//end time trigger button
            val timeUIEnd: TimePicker = timePickerEnd.findViewById(R.id.timePickerUIEnd)//end
            val setTimeBtnEnd: Button = timePickerEnd.findViewById(R.id.setTimeButtonEnd)//end
            endTiBot.setOnClickListener {
                timePickerEnd.window?.setLayout(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                timePickerEnd.setCancelable(false)
                timePickerEnd.show()
            }
            timeUIEnd.setOnTimeChangedListener { view, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                endTiBot.setText(selectedTime)
            }
            setTimeBtnEnd.setOnClickListener {
                timePickerEnd.dismiss()
            }
            //end
            val dismissButton = view.findViewById<Button>(R.id.updateCancelButtonEvent)
            dismissButton.setOnClickListener {
                dialog.dismiss()
            }
            //change the btn name
            val updateBtn = view.findViewById<Button>(R.id.updateInfoBtn)
            updateBtn.text = "Create Event"
            // set cancelable to avoid closing of dialog box when clicking on the screen.
            dialog.setCancelable(false)
            // set content view to our view.
            dialog.setContentView(view)
            // call a show method to display a dialog
            dialog.show()
        }
        //TODO: end
        //date picker part
        val datePickerStart = Dialog(this)
        datePickerStart.setContentView(R.layout.date_picker_start)
        val dateUIStart: DatePicker = datePickerStart.findViewById(R.id.datePickerStart)//date ui/ start
        val dateActionBtnStart: Button = datePickerStart.findViewById(R.id.setDateStart)//date select btn/end

        val picDateBtn = findViewById<Button>(R.id.picDateBtn)
        picDateBtn.setOnClickListener {
            datePickerStart.window?.setLayout(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            datePickerStart.setCancelable(false)
            dateActionBtnStart.setOnClickListener {
                with(dateUIStart){
                    val day = dayOfMonth
                    val month = month
                    val year = year
                    picDateBtn.text = "$day/$month/$year"
                }
                datePickerStart.dismiss()
            }
            datePickerStart.show()
        }
        //calender popup
        //bottom sheet
        val updateBtn = findViewById<Button>(R.id.editEventBtn)
        updateBtn.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.edit_calender_event, null)

            //pop up setup (calender)
            val datePickerStart = Dialog(this)
            datePickerStart.setContentView(R.layout.date_picker_start)

            val changeEventDateBtn = view.findViewById<Button>(R.id.change_event_btn_new)//trigger btn for calender popup
            val dateUIStart: DatePicker = datePickerStart.findViewById(R.id.datePickerStart)//date ui/ start
            val dateActionBtnStart: Button = datePickerStart.findViewById(R.id.setDateStart)//date select btn/end
            changeEventDateBtn.setOnClickListener {
                datePickerStart.window?.setLayout(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                datePickerStart.setCancelable(false)
                datePickerStart.show()
            }
            dateActionBtnStart.setOnClickListener {
                with(dateUIStart){
                    val day = dayOfMonth
                    val month = month
                    val year = year
                    changeEventDateBtn.text = "$day/$month/$year"
                }
                datePickerStart.dismiss()
            }
            //end
            //popup clock (start time)
            //start time
            val timePicker = Dialog(this)
            timePicker.setContentView(R.layout.time_picker)
            val startTiBot = view.findViewById<Button>(R.id.timeShowBottom)//start time trigger button
            val timeUI: TimePicker = timePicker.findViewById(R.id.timePickerUI)//start date
            val setTimeBtn: Button = timePicker.findViewById(R.id.setTimeButton)//start date
            startTiBot.setOnClickListener {
                timePicker.window?.setLayout(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                timePicker.setCancelable(false)
                timePicker.show()
            }
            timeUI.setOnTimeChangedListener { view, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                startTiBot.setText(selectedTime)
            }
            setTimeBtn.setOnClickListener {
                timePicker.dismiss()
            }
            //end
            //popup clock (end time)
            val timePickerEnd = Dialog(this)
            timePickerEnd.setContentView(R.layout.time_picker_end)
            val endTiBot = view.findViewById<Button>(R.id.timeShowEndBottom)//end time trigger button
            val timeUIEnd: TimePicker = timePickerEnd.findViewById(R.id.timePickerUIEnd)//end
            val setTimeBtnEnd: Button = timePickerEnd.findViewById(R.id.setTimeButtonEnd)//end
            endTiBot.setOnClickListener {
                timePickerEnd.window?.setLayout(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                timePickerEnd.setCancelable(false)
                timePickerEnd.show()
            }
            timeUIEnd.setOnTimeChangedListener { view, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                endTiBot.setText(selectedTime)
            }
            setTimeBtnEnd.setOnClickListener {
                timePickerEnd.dismiss()
            }
            //end
            val dismissButton = view.findViewById<Button>(R.id.updateCancelButtonEvent)
            dismissButton.setOnClickListener {
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