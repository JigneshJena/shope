package com.example.shope.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    
    /**
     * Format timestamp to display date (e.g., "25 Jan 2024")
     */
    fun formatDate(timestamp: Long): String {
        val formatter = SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
    
    /**
     * Format timestamp to full date with time (e.g., "25 Jan 2024, 02:30 PM")
     */
    fun formatDateTimeFull(timestamp: Long): String {
        val formatter = SimpleDateFormat(Constants.DATE_FORMAT_FULL, Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
    
    /**
     * Format timestamp to short date (e.g., "25/01/2024")
     */
    fun formatDateShort(timestamp: Long): String {
        val formatter = SimpleDateFormat(Constants.DATE_FORMAT_SHORT, Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
    
    /**
     * Format timestamp to time only (e.g., "02:30 PM")
     */
    fun formatTime(timestamp: Long): String {
        val formatter = SimpleDateFormat(Constants.DATE_FORMAT_TIME, Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
    
    /**
     * Get current timestamp in milliseconds
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
    
    /**
     * Get start of day timestamp (00:00:00)
     */
    fun getStartOfDay(date: Date = Date()): Long {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    /**
     * Get end of day timestamp (23:59:59)
     */
    fun getEndOfDay(date: Date = Date()): Long {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
    
    /**
     * Get yesterday's date
     */
    fun getYesterday(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return calendar.time
    }
    
    /**
     * Get start of current week (Monday)
     */
    fun getStartOfWeek(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return getStartOfDay(calendar.time)
    }
    
    /**
     * Get start of current month
     */
    fun getStartOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return getStartOfDay(calendar.time)
    }
    
    /**
     * Get start of current year
     */
    fun getStartOfYear(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        return getStartOfDay(calendar.time)
    }
    
    /**
     * Check if timestamp is today
     */
    fun isToday(timestamp: Long): Boolean {
        val today = getStartOfDay()
        val tomorrow = getStartOfDay(Date(today + 86400000)) // +1 day
        return timestamp >= today && timestamp < tomorrow
    }
    
    /**
     * Check if timestamp is yesterday
     */
    fun isYesterday(timestamp: Long): Boolean {
        val yesterday = getStartOfDay(getYesterday())
        val today = getStartOfDay()
        return timestamp >= yesterday && timestamp < today
    }
    
    /**
     * Get relative time string (e.g., "Just now", "2 hours ago", "Yesterday")
     */
    fun getRelativeTime(timestamp: Long): String {
        val now = getCurrentTimestamp()
        val diff = now - timestamp
        
        return when {
            diff < 60000 -> "Just now" // Less than 1 minute
            diff < 3600000 -> "${diff / 60000} minutes ago" // Less than 1 hour
            diff < 86400000 -> "${diff / 3600000} hours ago" // Less than 1 day
            isYesterday(timestamp) -> "Yesterday"
            diff < 604800000 -> "${diff / 86400000} days ago" // Less than 1 week
            else -> formatDate(timestamp)
        }
    }
    
    /**
     * Parse date string to timestamp
     */
    fun parseDate(dateString: String, format: String = Constants.DATE_FORMAT_SHORT): Long? {
        return try {
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            formatter.parse(dateString)?.time
        } catch (e: Exception) {
            null
        }
    }
}
