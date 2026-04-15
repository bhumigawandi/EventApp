package com.example.event_management_app.com.example.event_management_app



import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

    fun saveUser(name: String, email: String, role: String) {
        val editor = prefs.edit()
        editor.putString("name", name)
        editor.putString("email", email)
        editor.putString("role", role)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }
    fun getName(): String? {
        return prefs.getString("name", null)
    }
    fun getEmail(): String? {
        return prefs.getString("email", null)
    }
    fun getRole(): String? {
        return prefs.getString("role", null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getString("role", null) != null
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

}