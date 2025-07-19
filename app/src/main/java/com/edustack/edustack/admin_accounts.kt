
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edustack.edustack.R
import com.edustack.edustack.databinding.ActivityAdminMenuBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [admin_accounts.newInstance] factory method to
 * create an instance of this fragment.
 */
class admin_accounts : Fragment() {
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
        return inflater.inflate(R.layout.fragment_admin_accounts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val accountTypeDropdown = view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteID)
        // Define dropdown items
        val accountTypes = listOf(
            "Student",
            "Teacher"
        )

        // Create adapter using requireContext()
        val adapter = ArrayAdapter(
            requireContext(),  // Use fragment's context
            R.layout.list_item,  // Ensure this layout exists in res/layout
            accountTypes
        )

        // Set adapter to dropdown
        if (accountTypeDropdown != null) {
            accountTypeDropdown.setAdapter(adapter)
        }

        // Set click listener to show dropdown
        if (accountTypeDropdown != null) {
            accountTypeDropdown.setOnClickListener {
                val selectedItem: String = accountTypeDropdown.text.toString()
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            admin_accounts().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}