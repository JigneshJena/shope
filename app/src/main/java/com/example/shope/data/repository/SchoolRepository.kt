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

    private fun getSchoolsCollection() = 
        firestore.collection(Constants.COLLECTION_SCHOOLS)

    suspend fun getAllSchools(ownerUid: String? = null): Result<List<School>> {
        return try {
            val query = if (ownerUid != null) {
                getSchoolsCollection()
                    .whereEqualTo("addedBy", ownerUid)
            } else {
                getSchoolsCollection()
            }
            val snapshot = query.get().await()
            Log.d(TAG, "getAllSchools: Found ${snapshot.size()} schools")
            val schools = snapshot.toObjects(School::class.java)
            // Ensure schoolId is populated from document ID if field is empty
            schools.forEachIndexed { index, school ->
                if (school.schoolId.isEmpty()) {
                    school.schoolId = snapshot.documents[index].id
                }
            }
            Result.success(schools)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting schools", e)
            Result.failure(e)
        }
    }

    suspend fun addSchool(school: School): Result<String> {
        return try {
            val ownerUid = auth.currentUser?.uid ?: throw Exception("Not logged in")
            val docRef = getSchoolsCollection().document()
            school.schoolId = docRef.id
            school.addedBy = ownerUid
            docRef.set(school).await()
            Result.success(school.schoolId)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding school", e)
            Result.failure(e)
        }
    }

    suspend fun getSchoolById(schoolId: String): Result<School?> {
        return try {
            Log.d(TAG, "getSchoolById: Searching for $schoolId")
            
            // Direct fetch from top-level schools collection
            val directDoc = getSchoolsCollection().document(schoolId).get().await()
            if (directDoc.exists()) {
                val school = directDoc.toObject(School::class.java)
                if (school != null) {
                    if (school.schoolId.isEmpty()) school.schoolId = directDoc.id
                    Log.d(TAG, "getSchoolById: Found school: ${school.schoolName}")
                    return Result.success(school)
                }
            }

            Log.d(TAG, "getSchoolById: School not found with id $schoolId")
            Result.success(null)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting school by id", e)
            Result.failure(e)
        }
    }

    suspend fun updateSchool(school: School): Result<Unit> {
        return try {
            Log.d(TAG, "updateSchool: Updating ${school.schoolId}")
            val docRef = getSchoolsCollection().document(school.schoolId)
            Log.d(TAG, "updateSchool: Saving to ${docRef.path}")
            docRef.set(school).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating school", e)
            Result.failure(e)
        }
    }

    suspend fun getSchoolCount(ownerUid: String? = null): Int {
        return try {
            val query = if (ownerUid != null) {
                getSchoolsCollection()
                    .whereEqualTo("addedBy", ownerUid)
            } else {
                getSchoolsCollection()
            }
            val snapshot = query.get().await()
            snapshot.size()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting school count", e)
            0
        }
    }
}
