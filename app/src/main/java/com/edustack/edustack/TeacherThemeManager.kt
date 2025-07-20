package com.edustack.edustack

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object TeacherThemeManager {
    private const val PREF_NAME = "theme_pref"
    private const val KEY_THEME = "current_theme"

    fun toggleTheme(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val currentTheme = sharedPref.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        val newTheme = when (currentTheme) {
            AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }

        saveTheme(context, newTheme)
        AppCompatDelegate.setDefaultNightMode(newTheme)
    }

    fun applyTheme(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val theme = sharedPref.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(theme)
    }

    private fun saveTheme(context: Context, theme: Int) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(KEY_THEME, theme)
            apply()
        }
    }
}