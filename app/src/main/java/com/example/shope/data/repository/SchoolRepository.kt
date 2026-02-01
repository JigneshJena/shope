package com.example.shope.data.repository

import com.example.shope.data.models.School
import com.example.shope.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

class SchoolRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "SchoolRepository"

    suspend fun getAllSchools(): Result<List<School>> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_SCHOOLS)
                .get()
                .await()
            val schools = snapshot.toObjects(School::class.java)
            Result.success(schools)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting schools", e)
            Result.failure(e)
        }
    }

    suspend fun addSchool(school: School): Result<String> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_SCHOOLS).document()
            school.schoolId = docRef.id
            docRef.set(school).await()
            Result.success(school.schoolId)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding school", e)
            Result.failure(e)
        }
    }
    
    suspend fun getSchoolCount(): Int {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_SCHOOLS)
                .get()
                .await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }
}
