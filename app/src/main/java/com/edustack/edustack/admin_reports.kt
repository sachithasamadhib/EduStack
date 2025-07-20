package com.edustack.edustack

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [admin_reports.newInstance] factory method to
 * create an instance of this fragment.
 */
class admin_reports : Fragment() {
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
        return inflater.inflate(R.layout.fragment_admin_reports, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        //view all reports
        val viewAllReports: Button = view.findViewById(R.id.viewAllReports)
        viewAllReports.setOnClickListener {
            val intent = Intent(activity, AllReports::class.java)
            startActivity(intent)
        }
        //start date
        val datePickerStart = Dialog(requireContext())
        datePickerStart.setContentView(R.layout.date_picker_start)
        //end date
        val datePickerEnd = Dialog(requireContext())
        datePickerEnd.setContentView(R.layout.date_picker_end)

        val getDateStart: Button = view.findViewById(R.id.DateShowStart)//button 1 (start date)
        val dateUIStart: DatePicker = datePickerStart.findViewById(R.id.datePickerStart)//date ui/ start
        val dateActionBtnStart: Button = datePickerStart.findViewById(R.id.setDateStart)//date select btn/end

        val getDateEnd: Button = view.findViewById(R.id.DateShowEnd)//button 2 (end date)
        val dateUIEnd: DatePicker = datePickerEnd.findViewById(R.id.datePickerEnd)//date ui/ end
        val dateActionBtnEnd: Button = datePickerEnd.findViewById(R.id.setDateEnd)//date select btn/end

        //start action
        getDateStart.setOnClickListener {
            datePickerStart.window?.setLayout(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            datePickerStart.setCancelable(false)
            datePickerStart.show()
        }
        //end action
        getDateEnd.setOnClickListener {
            datePickerEnd.window?.setLayout(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            datePickerEnd.setCancelable(false)
            datePickerEnd.show()
        }
        //start (popup)
        dateActionBtnStart.setOnClickListener {
            with(dateUIStart){
                val day = dayOfMonth
                val month = month
                val year = year
                getDateStart.text = "$day/$month/$year"
            }
            datePickerStart.dismiss()
        }
        //end(popup)
        dateActionBtnEnd.setOnClickListener {
            with(dateUIEnd){
                val day = dayOfMonth
                val month = month
                val year = year
                getDateEnd.text = "$day/$month/$year"
            }
            datePickerEnd.dismiss()
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment admin_reports.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            admin_reports().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}