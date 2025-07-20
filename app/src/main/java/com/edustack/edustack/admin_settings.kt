package com.edustack.edustack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.edit
import com.edustack.edustack.databinding.FragmentAdminSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class AdminSettingsFragment : Fragment() {

    private lateinit var binding: FragmentAdminSettingsBinding
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var inactivityTimer: InactivityTimer
    private val handler = Handler(Looper.getMainLooper())
    private val auth = FirebaseAuth.getInstance()
    private val fcm = FirebaseMessaging.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminSettingsBinding.inflate(inflater, container, false)
        sharedPrefs = requireContext().getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE)
        inactivityTimer = InactivityTimer(requireContext(), ::handleLogout)

        setupVersionInfo()
        setupDarkModeToggle()
        setupNotificationToggle()
        setupAutoLogout()
        setupCacheClear()
        setupLogout()

        return binding.root
    }

    private fun setupVersionInfo() {
        try {
            val pInfo = requireContext().packageManager.getPackageInfo(
                requireContext().packageName, 0
            )
            binding.txtAppVersion.text = "v${pInfo.versionName}"
        } catch (e: PackageManager.NameNotFoundException) {
            binding.txtAppVersion.text = "v1.0"
        }
    }

    private fun setupDarkModeToggle() {
        binding.switchDarkMode.isChecked = sharedPrefs.getBoolean("dark_mode", false)
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit { putBoolean("dark_mode", isChecked) }
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun setupNotificationToggle() {
        binding.switchNotifications.isChecked = sharedPrefs.getBoolean("notifications_enabled", true)
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit { putBoolean("notifications_enabled", isChecked) }
            updateNotificationSubscription(isChecked)
            showToast(if (isChecked) "Notifications enabled" else "Notifications disabled")
        }
    }

    private fun updateNotificationSubscription(enable: Boolean) {
        if (enable) {
            fcm.subscribeToTopic("admin_notifications")
        } else {
            fcm.unsubscribeFromTopic("admin_notifications")
        }
    }

    private fun setupAutoLogout() {
        val savedTimeout = sharedPrefs.getInt("auto_logout_minutes", 15)
        when (savedTimeout) {
            15 -> binding.radio15min.isChecked = true
            30 -> binding.radio30min.isChecked = true
            -1 -> binding.radioNever.isChecked = true
        }

        binding.radioLogoutTimer.setOnCheckedChangeListener { _, checkedId ->
            val minutes = when (checkedId) {
                R.id.radio15min -> 15
                R.id.radio30min -> 30
                else -> -1
            }
            setAutoLogoutTimeout(minutes)
        }
    }

    private fun setAutoLogoutTimeout(minutes: Int) {
        sharedPrefs.edit { putInt("auto_logout_minutes", minutes) }
        val timeoutMs = if (minutes == -1) -1L else minutes * 60 * 1000L
        inactivityTimer.setTimeout(timeoutMs)

        val message = when (minutes) {
            15 -> "Auto-logout set to 15 minutes"
            30 -> "Auto-logout set to 30 minutes"
            else -> "Auto-logout disabled"
        }
        showToast(message)
    }

    private fun setupCacheClear() {
        binding.btnClearCache.setOnClickListener {
            requireContext().cacheDir.deleteRecursively()
            showToast("Cache cleared successfully")
        }
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            navigateToLogin()
            showToast("Logged out successfully")
        }
    }

    private fun handleLogout() {
        auth.signOut()
        navigateToLogin()
        showToast("Auto-logged out due to inactivity")
    }

    private fun navigateToLogin() {
        startActivity(Intent(requireActivity(), AdminMenu::class.java))
        requireActivity().finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        inactivityTimer.resetTimer()
    }

    override fun onPause() {
        super.onPause()
        inactivityTimer.destroy()
    }

    private inner class InactivityTimer(
        private val context: Context,
        private val logoutCallback: () -> Unit
    ) {
        private var timeoutMillis: Long = 15 * 60 * 1000
        private lateinit var logoutRunnable: Runnable
        private lateinit var warningRunnable: Runnable

        init {
            setupRunnables()
        }

        private fun setupRunnables() {
            logoutRunnable = Runnable { logoutCallback() }
            warningRunnable = Runnable {
                showToast("You will be logged out in 30 seconds")
            }
        }

        fun setTimeout(timeout: Long) {
            handler.removeCallbacks(logoutRunnable)
            handler.removeCallbacks(warningRunnable)

            this.timeoutMillis = timeout
            if (timeout != -1L) {
                handler.postDelayed(logoutRunnable, timeoutMillis)
                if (timeoutMillis > 30_000) {
                    handler.postDelayed(warningRunnable, timeoutMillis - 30_000)
                }
            }
        }

        fun resetTimer() {
            if (timeoutMillis != -1L) {
                handler.removeCallbacks(logoutRunnable)
                handler.removeCallbacks(warningRunnable)
                handler.postDelayed(logoutRunnable, timeoutMillis)
                if (timeoutMillis > 30_000) {
                    handler.postDelayed(warningRunnable, timeoutMillis - 30_000)
                }
            }
        }

        fun destroy() {
            handler.removeCallbacks(logoutRunnable)
            handler.removeCallbacks(warningRunnable)
        }
    }
}