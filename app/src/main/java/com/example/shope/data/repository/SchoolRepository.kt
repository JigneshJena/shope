package com.example.shope.data.repository

import com.example.shope.data.models.School
import com.example.shope.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

class SchoolRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "SchoolRepository"

    private fun getSchoolsCollection(ownerUid: String) = 
        firestore.collection(Constants.COLLECTION_OWNERS)
            .document(ownerUid)
            .collection(Constants.COLLECTION_SCHOOLS)

    suspend fun getAllSchools(ownerUid: String): Result<List<School>> {
        return try {
            val snapshot = getSchoolsCollection(ownerUid)
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
            val ownerUid = auth.currentUser?.uid ?: throw Exception("Not logged in")
            val docRef = getSchoolsCollection(ownerUid).document()
            school.schoolId = docRef.id
            school.addedBy = ownerUid
            docRef.set(school).await()
            Result.success(school.schoolId)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding school", e)
            Result.failure(e)
        }
    }

    suspend fun getSchoolById(ownerUid: String, schoolId: String): Result<School?> {
        return try {
            val doc = getSchoolsCollection(ownerUid)
                .document(schoolId)
                .get()
                .await()
            Result.success(doc.toObject(School::class.java))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting school by id", e)
            Result.failure(e)
        }
    }

    suspend fun updateSchool(ownerUid: String, school: School): Result<Unit> {
        return try {
            getSchoolsCollection(ownerUid)
                .document(school.schoolId)
                .set(school)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating school", e)
            Result.failure(e)
        }
    }

    suspend fun getSchoolCount(ownerUid: String): Int {
        return try {
            val snapshot = getSchoolsCollection(ownerUid)
                .get()
                .await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }
}
