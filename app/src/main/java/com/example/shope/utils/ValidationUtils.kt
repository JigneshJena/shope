package com.example.shope.utils

import android.util.Patterns
import java.util.regex.Pattern

object ValidationUtils {
    
    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Validate password (minimum 6 characters)
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= Constants.MIN_PASSWORD_LENGTH
    }
    
    /**
     * Validate phone number (minimum 10 digits)
     */
    fun isValidPhone(phone: String): Boolean {
        val digitsOnly = phone.replace(Regex("[^0-9]"), "")
        return digitsOnly.length >= Constants.MIN_PHONE_LENGTH
    }
    
    /**
     * Validate name (not empty and only letters and spaces)
     */
    fun isValidName(name: String): Boolean {
        return name.isNotEmpty() && name.length >= 2
    }
    
    /**
     * Validate price/amount (positive number)
     */
    fun isValidPrice(price: String): Boolean {
        return try {
            val value = price.toDouble()
            value >= 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Validate quantity (positive integer)
     */
    fun isValidQuantity(quantity: String): Boolean {
        return try {
            val value = quantity.toInt()
            value > 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check if string is not empty
     */
    fun isNotEmpty(text: String): Boolean {
        return text.trim().isNotEmpty()
    }
    
    /**
     * Validate selling price against purchase price
     */
    fun isValidSellingPrice(purchasePrice: Double, sellingPrice: Double): Boolean {
        return sellingPrice >= purchasePrice
    }
    
    /**
     * Get validation error message for email
     */
    fun getEmailError(email: String): String? {
        return when {
            email.isEmpty() -> "Email is required"
            !isValidEmail(email) -> "Invalid email format"
            else -> null
        }
    }
    
    /**
     * Get validation error message for password
     */
    fun getPasswordError(password: String): String? {
        return when {
            password.isEmpty() -> "Password is required"
            password.length < Constants.MIN_PASSWORD_LENGTH -> 
                "Password must be at least ${Constants.MIN_PASSWORD_LENGTH} characters"
            else -> null
        }
    }
    
    /**
     * Get validation error message for phone
     */
    fun getPhoneError(phone: String): String? {
        return when {
            phone.isEmpty() -> "Phone number is required"
            !isValidPhone(phone) -> "Phone number must be at least ${Constants.MIN_PHONE_LENGTH} digits"
            else -> null
        }
    }
    
    /**
     * Get validation error message for name
     */
    fun getNameError(name: String): String? {
        return when {
            name.isEmpty() -> "Name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            else -> null
        }
    }
}
