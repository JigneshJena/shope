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
                firestore.collectionGroup(Constants.COLLECTION_SCHOOLS)
                    .whereEqualTo("addedBy", ownerUid)
            } else {
                firestore.collectionGroup(Constants.COLLECTION_SCHOOLS)
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
            
            // Try direct fetch first (most efficient and doesn't require index)
            val directDoc = firestore.collection(Constants.COLLECTION_SCHOOLS).document(schoolId).get().await()
            if (directDoc.exists()) {
                val school = directDoc.toObject(School::class.java)
                if (school != null) {
                    if (school.schoolId.isEmpty()) school.schoolId = directDoc.id
                    return Result.success(school)
                }
            }

            // Fallback 1: search by the 'schoolId' field across all collections (requires index)
            var school: School? = try {
                val snapshot = firestore.collectionGroup(Constants.COLLECTION_SCHOOLS)
                    .whereEqualTo("schoolId", schoolId)
                    .limit(1)
                    .get()
                    .await()
                
                snapshot.documents.firstOrNull()?.let { doc ->
                    val s = doc.toObject(School::class.java)
                    if (s != null && s.schoolId.isEmpty()) s.schoolId = doc.id
                    s
                }
            } catch (e: Exception) {
                Log.w(TAG, "Filtered search failed - missing index? Using in-memory fallback next.")
                null
            }
            
            // If not found by field, it might be an older record where schoolId is only the Doc ID
            if (school == null) {
                Log.d(TAG, "getSchoolById: Not found by field, searching all...")
                val allSchoolsResult = getAllSchools()
                if (allSchoolsResult.isSuccess) {
                    school = allSchoolsResult.getOrNull()?.find { it.schoolId == schoolId }
                }
            }
            
            Log.d(TAG, "getSchoolById: Result found: ${school != null}")
            Result.success(school)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting school by id", e)
            Result.failure(e)
        }
    }

    suspend fun updateSchool(school: School): Result<Unit> {
        return try {
            Log.d(TAG, "updateSchool: Updating ${school.schoolId}")
            
            // Try to find by field first
            var docRef = try {
                val snapshot = firestore.collectionGroup(Constants.COLLECTION_SCHOOLS)
                    .whereEqualTo("schoolId", school.schoolId)
                    .get()
                    .await()
                snapshot.documents.firstOrNull()?.reference
            } catch (e: Exception) {
                Log.w(TAG, "updateSchool: Filtered search failed - missing index? Using in-memory fallback next.")
                null
            }
            
            if (docRef == null) {
                Log.d(TAG, "updateSchool: Document reference not found by field, searching all...")
                // Fallback: search all to find the path
                val query = firestore.collectionGroup(Constants.COLLECTION_SCHOOLS).get().await()
                docRef = query.documents.find { it.id == school.schoolId }?.reference
            }
            
            if (docRef == null) {
                Log.d(TAG, "updateSchool: Creating new document in top-level schools")
                docRef = getSchoolsCollection().document(school.schoolId)
            }
            
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
                firestore.collectionGroup(Constants.COLLECTION_SCHOOLS)
                    .whereEqualTo("addedBy", ownerUid)
            } else {
                firestore.collectionGroup(Constants.COLLECTION_SCHOOLS)
            }
            val snapshot = query.get().await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }
}
