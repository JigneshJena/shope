package com.example.shope.utils

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.shope.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension function to show a view
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Extension function to hide a view
 */
fun View.hide() {
    visibility = View.GONE
}

/**
 * Extension function to make view invisible (maintains space)
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Format number to currency string with rupee symbol
 */
fun Double.toCurrency(): String {
    return "${Constants.DEFAULT_CURRENCY}${String.format("%.2f", this)}"
}

/**
 * Format number to currency string (Int version)
 */
fun Int.toCurrency(): String {
    return this.toDouble().toCurrency()
}

/**
 * Load Base64 image into ImageView using Glide
 */
fun ImageView.loadBase64(base64String: String?, placeholder: Int = R.drawable.ic_placeholder) {
    if (base64String.isNullOrEmpty()) {
        Glide.with(this.context)
            .load(placeholder)
            .into(this)
    } else {
        val bitmap = ImageUtils.base64ToBitmap(base64String)
        if (bitmap != null) {
            Glide.with(this.context)
                .load(bitmap)
                .placeholder(placeholder)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(this)
        } else {
            Glide.with(this.context)
                .load(placeholder)
                .into(this)
        }
    }
}

/**
 * Load circular Base64 image into ImageView using Glide
 */
fun ImageView.loadCircularBase64(base64String: String?, placeholder: Int = R.drawable.ic_avatar) {
    if (base64String.isNullOrEmpty()) {
        Glide.with(this.context)
            .load(placeholder)
            .circleCrop()
            .into(this)
    } else {
        val bitmap = ImageUtils.base64ToBitmap(base64String)
        if (bitmap != null) {
            Glide.with(this.context)
                .load(bitmap)
                .placeholder(placeholder)
                .error(placeholder)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(this)
        } else {
            Glide.with(this.context)
                .load(placeholder)
                .circleCrop()
                .into(this)
        }
    }
}

/**
 * Calculate percentage safely
 */
fun calculatePercentageChange(current: Double, previous: Double): Double {
    return if (previous == 0.0) {
        0.0
    } else {
        ((current - previous) / previous) * 100
    }
}

/**
 * Format percentage to string with + or - sign
 */
fun Double.toPercentageString(): String {
    val sign = if (this >= 0) "+" else ""
    return "$sign${String.format("%.1f", this)}%"
}

/**
 * Calculate profit margin percentage
 */
fun calculateProfitMargin(sellingPrice: Double, purchasePrice: Double): Double {
    return if (purchasePrice == 0.0) {
        0.0
    } else {
        ((sellingPrice - purchasePrice) / purchasePrice) * 100
    }
}

/**
 * Check if email contains owner identifier
 */
fun String.isOwnerEmail(): Boolean {
    return this.contains("@owner", ignoreCase = true)
}

/**
 * Generate unique ID with prefix
 */
fun generateUniqueId(prefix: String): String {
    val timestamp = System.currentTimeMillis()
    val random = (1000..9999).random()
    val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(timestamp))
    return "$prefix-$date-$random"
}

/**
 * Generate order number: ORD-YYYYMMDD-XXXX
 */
fun generateOrderNumber(): String {
    return generateUniqueId("ORD")
}

/**
 * Generate invoice number: INV-YYYYMMDD-XXXX
 */
fun generateInvoiceNumber(): String {
    return generateUniqueId("INV")
}
