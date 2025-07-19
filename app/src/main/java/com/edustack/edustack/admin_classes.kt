package com.edustack.edustack

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TimePicker

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [admin_classes.newInstance] factory method to
 * create an instance of this fragment.
 */
class admin_classes : Fragment() {
    // TODO: Rename and change types of parameters
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_classes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        val btnViewAllClasses : Button = view.findViewById(R.id.btnViewClasses)
        //start time
        val timePicker = Dialog(requireContext())
        timePicker.setContentView(R.layout.time_picker)
        //end time
        val timePickerEnd = Dialog(requireContext())
        timePickerEnd.setContentView(R.layout.time_picker_end)


        val getTimeStart: Button = view.findViewById(R.id.timeShow)//button 1 (start time)
        val timeUI: TimePicker = timePicker.findViewById(R.id.timePickerUI)//start date
        val setTimeBtn: Button = timePicker.findViewById(R.id.setTimeButton)//start date

        val getTimeEnd: Button = view.findViewById(R.id.timeShowEnd)//button 2 (end time)
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
        btnViewAllClasses.setOnClickListener {
            val intent = Intent(activity, AllClasses::class.java)
            startActivity(intent)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment admin_classes.
         */
        // TODO: Rename and change types and number of parameters
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