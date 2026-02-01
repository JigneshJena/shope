package com.example.shope.data.repository

import android.util.Log
import com.example.shope.data.models.Employee
import com.example.shope.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class EmployeeRepository {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "EmployeeRepository"
    }
    
    /**
     * Add employee (Owner only)
     * Uses a secondary FirebaseApp to avoid signing out the currently logged-in owner.
     */
    suspend fun addEmployee(employee: Employee, password: String): Result<String> {
        return try {
            // Check email doesn't exist (basic check)
            if (checkEmailExists(employee.email)) {
                throw Exception("Email already exists")
            }

            // Create a secondary FirebaseApp to handle the new user creation without signing out the internal user
            val appName = "SecondaryApp"
            val firebaseApp = try {
                com.google.firebase.FirebaseApp.getInstance(appName)
            } catch (e: IllegalStateException) {
                // Initialize if not exists
                val options = com.google.firebase.FirebaseApp.getInstance().options
                com.google.firebase.FirebaseApp.initializeApp(
                    com.google.firebase.FirebaseApp.getInstance().applicationContext,
                    options,
                    appName
                )
            }

            // Get Auth instance for the secondary app
            val secondaryAuth = FirebaseAuth.getInstance(firebaseApp)

            // Create the user
            val result = secondaryAuth.createUserWithEmailAndPassword(employee.email, password).await()
            val employeeId = result.user?.uid ?: throw Exception("Failed to create employee auth account")

            // Sign out from the secondary app (though it doesn't affect the main app, good practice)
            secondaryAuth.signOut()

            // Set employee ID
            employee.employeeId = employeeId

            // Save to Firestore employees collection (Using main Firestore instance which has Owner auth)
            firestore.collection(Constants.COLLECTION_EMPLOYEES)
                .document(employeeId)
                .set(employee)
                .await()

            // Also save to users collection for authentication lookup
            val user = com.example.shope.data.models.User(
                userId = employeeId,
                name = employee.name,
                email = employee.email,
                phone = employee.phone,
                role = Constants.ROLE_EMPLOYEE,
                profilePicture = employee.profilePicture,
                status = employee.status,
                createdAt = java.util.Date() // Ensure date is set
            )

            firestore.collection(Constants.COLLECTION_USERS)
                .document(employeeId)
                .set(user)
                .await()

            Log.d(TAG, "Employee added successfully: $employeeId")
            Result.success(employeeId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add employee", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get all employees
     */
    suspend fun getAllEmployees(): Result<List<Employee>> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_EMPLOYEES)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val employees = snapshot.toObjects(Employee::class.java)
            Result.success(employees)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get employees", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get active employees count
     */
    suspend fun getActiveEmployeesCount(): Int {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_EMPLOYEES)
                .whereEqualTo("status", Constants.STATUS_ACTIVE)
                .get()
                .await()
            
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Update employee
     */
    suspend fun updateEmployee(employee: Employee): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_EMPLOYEES)
                .document(employee.employeeId)
                .set(employee)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update employee", e)
            Result.failure(e)
        }
    }
    
    /**
     * Deactivate employee
     */
    suspend fun deactivateEmployee(employeeId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_EMPLOYEES)
                .document(employeeId)
                .update("status", Constants.STATUS_INACTIVE)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to deactivate employee", e)
            Result.failure(e)
        }
    }
    
    private suspend fun checkEmailExists(email: String): Boolean {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_EMPLOYEES)
                .whereEqualTo("email", email)
                .get()
                .await()
            
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}
