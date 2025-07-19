package com.edustack.edustack

import admin_accounts
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.edustack.edustack.databinding.ActivityAdminMenuBinding

class AdminMenu : AppCompatActivity() {
    private lateinit var binding : ActivityAdminMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            replaceFragment(admin_accounts())
        }
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.admin_home -> replaceFragment(admin_accounts())
                R.id.admin_Settings -> replaceFragment(admin_settings())
                R.id.admin_classes -> replaceFragment(admin_classes())
                R.id.admin_reports -> replaceFragment(admin_reports())
                R.id.admin_addClasses -> replaceFragment(admin_assign())
                else -> {
                }
            }
            true
        }
    }
    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutID, fragment)
        fragmentTransaction.commit()
    }
}