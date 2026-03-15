package com.example.shope.data.repository

import android.util.Log
import com.example.shope.data.models.Employee
import com.example.shope.data.models.User
import com.example.shope.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class EmployeeRepository {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "EmployeeRepository"
    }
    
    /**
     * Get employees subcollection reference for a specific owner
     */
    private fun getEmployeesCollection(ownerUid: String) = 
        firestore.collection(Constants.COLLECTION_OWNERS)
            .document(ownerUid)
            .collection(Constants.COLLECTION_EMPLOYEES)

    /**
     * Add employee (Owner only)
     * Create account in Firebase Auth and then save to Firestore
     */
    suspend fun addEmployee(employee: Employee, password: String): Result<String> {
        return try {
            val ownerUid = auth.currentUser?.uid ?: throw Exception("Not logged in")
            
            // 1. Create entry in Firebase Auth
            // We use a secondary app approach to avoid signing out the current owner
            val appName = "EmployeeCreationApp"
            val secondaryApp = try {
                com.google.firebase.FirebaseApp.getInstance(appName)
            } catch (e: IllegalStateException) {
                val options = com.google.firebase.FirebaseApp.getInstance().options
                com.google.firebase.FirebaseApp.initializeApp(
                    com.google.firebase.FirebaseApp.getInstance().applicationContext,
                    options,
                    appName
                )
            }
            
            val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)
            val authResult = secondaryAuth.createUserWithEmailAndPassword(employee.email, password).await()
            val employeeId = authResult.user?.uid ?: throw Exception("Failed to create auth account")
            secondaryAuth.signOut()
            
            // 2. Prepare Employee data
            val newEmployee = employee.copy(
                employeeId = employeeId,
                addedBy = ownerUid
            )
            
            // 3. Save to /owners/{ownerUid}/employees/{employeeId}
            getEmployeesCollection(ownerUid)
                .document(employeeId)
                .set(newEmployee)
                .await()
                
            // 4. Also save to the global /users collection for role-based login
            val user = User(
                userId = employeeId,
                name = employee.name,
                email = employee.email,
                phone = employee.phone,
                role = Constants.ROLE_EMPLOYEE,
                profilePicture = employee.profilePicture,
                status = employee.status
            )
            firestore.collection(Constants.COLLECTION_USERS)
                .document(employeeId)
                .set(user)
                .await()
                
            Result.success(employeeId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add employee", e)
            Result.failure(e)
        }
    }

    /**
     * Get all employees for the current owner in real-time
     */
    fun getEmployeesRealtime(ownerUid: String, onUpdate: (List<Employee>) -> Unit): ListenerRegistration {
        return getEmployeesCollection(ownerUid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to employees", error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Employee::class.java) } ?: emptyList()
                onUpdate(list)
            }
    }

    /**
     * Update employee details
     */
    suspend fun updateEmployee(ownerUid: String, employee: Employee): Result<Unit> {
        return try {
            // Update in owners subcollection
            getEmployeesCollection(ownerUid)
                .document(employee.employeeId)
                .set(employee)
                .await()
                
            // Update in global users collection
            firestore.collection(Constants.COLLECTION_USERS)
                .document(employee.employeeId)
                .update(mapOf(
                    "name" to employee.name,
                    "phone" to employee.phone,
                    "profilePicture" to employee.profilePicture,
                    "status" to employee.status
                ))
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update employee", e)
            Result.failure(e)
        }
    }

    /**
     * Delete employee
     */
    suspend fun deleteEmployee(ownerUid: String, employeeId: String): Result<Unit> {
        return try {
            // 1. Delete from owners subcollection
            getEmployeesCollection(ownerUid).document(employeeId).delete().await()
            
            // 2. Delete from global users collection
            firestore.collection(Constants.COLLECTION_USERS).document(employeeId).delete().await()
            
            // Note: Auth account deletion requires Admin SDK or User to re-authenticate.
            // In a real app, we usually just de-activate or use a Cloud Function.
            // For now, we'll suggest deactivating or marking as deleted.
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete employee", e)
            Result.failure(e)
        }
    }

    /**
     * Reset employee password
     */
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
