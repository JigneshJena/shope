package com.example.shope.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtils {
    
    private const val TAG = "ImageUtils"
    
    /**
     * Convert image URI to Base64 string
     * Compresses image to max 800x800px and 75% quality
     */
    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (bitmap != null) {
                val compressedBitmap = compressBitmap(bitmap)
                bitmapToBase64(compressedBitmap)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error converting URI to Base64", e)
            null
        }
    }
    
    /**
     * Convert Bitmap to Base64 string
     */
    fun bitmapToBase64(bitmap: Bitmap, quality: Int = Constants.IMAGE_QUALITY): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    
    /**
     * Convert Base64 string to Bitmap
     */
    fun base64ToBitmap(base64String: String?): Bitmap? {
        return try {
            if (base64String.isNullOrEmpty()) {
                null
            } else {
                val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error converting Base64 to Bitmap", e)
            null
        }
    }
    
    /**
     * Compress bitmap to max dimensions while maintaining aspect ratio
     */
    private fun compressBitmap(
        bitmap: Bitmap,
        maxWidth: Int = Constants.MAX_IMAGE_SIZE_KB,
        maxHeight: Int = Constants.MAX_IMAGE_SIZE_KB
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        // Calculate scaling factor
        val scale = Math.min(
            maxWidth.toFloat() / width,
            maxHeight.toFloat() / height
        )
        
        // Only scale down, never scale up
        if (scale >= 1) {
            return bitmap
        }
        
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * Rotate bitmap by specified degrees
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    
    /**
     * Get circular crop of bitmap (for profile pictures)
     */
    fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = Math.min(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(output)
        
        val paint = android.graphics.Paint()
        val rect = android.graphics.Rect(0, 0, size, size)
        
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        
        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        
        return output
    }
}
