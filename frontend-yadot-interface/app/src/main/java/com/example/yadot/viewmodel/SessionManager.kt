package com.example.yadot.viewmodel

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "yadot_session"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_LOGGED_IN = "logged_in"

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun salvarSessao(context: Context, id: Long, nome: String, email: String) {
        getPrefs(context).edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putLong(KEY_USER_ID, id)
            .putString(KEY_USER_NAME, nome)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_LOGGED_IN, false)

    fun getUserId(context: Context): Long =
        getPrefs(context).getLong(KEY_USER_ID, -1)

    fun getUserName(context: Context): String =
        getPrefs(context).getString(KEY_USER_NAME, "") ?: ""

    fun getUserEmail(context: Context): String =
        getPrefs(context).getString(KEY_USER_EMAIL, "") ?: ""

    fun limparSessao(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}