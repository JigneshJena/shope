package com.example.shope.data.repository

import android.util.Log
import com.example.shope.data.models.User
import com.example.shope.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "UserRepository"
    }
    
    /**
     * Get user by ID
     */
    suspend fun getUser(userId: String): Result<User> {
        return try {
            val doc = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
            
            val user = doc.toObject(User::class.java) ?: throw Exception("User not found")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user", e)
            Result.failure(e)
        }
    }
    
    /**
     * Update user profile
     */
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_USERS)
                .document(user.userId)
                .set(user)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update user", e)
            Result.failure(e)
        }
    }
}
