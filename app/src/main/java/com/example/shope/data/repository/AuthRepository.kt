package com.example.shope.data.repository

import android.util.Log
import com.example.shope.data.models.User
import com.example.shope.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "AuthRepository"
    }
    
    /**
     * Login with email and password
     */
    suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID not found")
            
            // Get user data from Firestore
            val user = getUserFromFirestore(userId)
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            Result.failure(e)
        }
    }
    
    /**
     * Sign up with email and password
     */
    suspend fun signupWithEmail(name: String, email: String, phone: String, password: String): Result<User> {
        return try {
            // Create user in Firebase Auth
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID not found")
            
            // Create user document in Firestore
            val user = User(
                userId = userId,
                name = name,
                email = email,
                phone = phone,
                role = when {
                    email.contains("@owner", ignoreCase = true) -> Constants.ROLE_OWNER
                    email.contains("@employee", ignoreCase = true) -> Constants.ROLE_EMPLOYEE
                    else -> Constants.ROLE_CUSTOMER
                },
                status = Constants.STATUS_ACTIVE
            )
            
            firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .set(user)
                .await()
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Signup failed", e)
            Result.failure(e)
        }
    }
    
    /**
     * Sign in with Google
     */
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val userId = result.user?.uid ?: throw Exception("User ID not found")
            
            // Check if user exists in Firestore
            val userDoc = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
            
            val user = if (userDoc.exists()) {
                // Existing user - login
                userDoc.toObject(User::class.java) ?: throw Exception("Failed to parse user data")
            } else {
                // New user - create account
                val newUser = User(
                    userId = userId,
                    name = account.displayName ?: "",
                    email = account.email ?: "",
                    phone = "",
                    role = Constants.ROLE_CUSTOMER,
                    status = Constants.STATUS_ACTIVE
                )
                
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(userId)
                    .set(newUser)
                    .await()
                
                newUser
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Google sign in failed", e)
            Result.failure(e)
        }
    }
    
    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Password reset failed", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get current user
     */
    fun getCurrentUser() = auth.currentUser
    
    /**
     * Logout
     */
    fun logout() {
        auth.signOut()
    }
    
    /**
     * Get user data from Firestore
     */
    private suspend fun getUserFromFirestore(userId: String): User {
        val doc = firestore.collection(Constants.COLLECTION_USERS)
            .document(userId)
            .get()
            .await()
        
        val user = doc.toObject(User::class.java) ?: throw Exception("User not found")
        
        // Normalize role based on email convention for testing/initial setup
        if (user.isOwner()) {
            user.role = Constants.ROLE_OWNER
        } else if (user.isEmployee()) {
            user.role = Constants.ROLE_EMPLOYEE
        }
        
        return user
    }
    
    /**
     * Get user role and determine dashboard
     */
    suspend fun getUserRole(userId: String): String {
        val user = getUserFromFirestore(userId)
        return when {
            user.isOwner() -> Constants.ROLE_OWNER
            user.isEmployee() -> Constants.ROLE_EMPLOYEE
            else -> Constants.ROLE_CUSTOMER
        }
    }
}
