package com.example.shope.utils

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import com.example.shope.utils.Constants.PREF_IS_LOGGED_IN
import com.example.shope.utils.Constants.PREF_NAME
import com.example.shope.utils.Constants.PREF_USER_EMAIL
import com.example.shope.utils.Constants.PREF_USER_ID
import com.example.shope.utils.Constants.PREF_USER_NAME
import com.example.shope.utils.Constants.PREF_USER_ROLE

class PreferenceManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    fun saveUserSession(userId: String, name: String, email: String, role: String, phone: String = "") {
        sharedPreferences.edit().apply {
            putString(PREF_USER_ID, userId)
            putString(PREF_USER_NAME, name)
            putString(PREF_USER_EMAIL, email)
            putString(PREF_USER_ROLE, role)
            putString("pref_user_phone", phone)
            putBoolean(PREF_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun saveUserName(name: String) {
        sharedPreferences.edit().putString(PREF_USER_NAME, name).apply()
    }

    fun saveUserPhone(phone: String) {
        sharedPreferences.edit().putString("pref_user_phone", phone).apply()
    }

    fun getUserPhone(): String? = sharedPreferences.getString("pref_user_phone", "")

    fun getUserId(): String? = sharedPreferences.getString(PREF_USER_ID, null)
    
    fun getUserName(): String? = sharedPreferences.getString(PREF_USER_NAME, null)
    
    fun getUserEmail(): String? = sharedPreferences.getString(PREF_USER_EMAIL, null)
    
    fun getUserRole(): String? = sharedPreferences.getString(PREF_USER_ROLE, null)
    
    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(PREF_IS_LOGGED_IN, false)
    
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
