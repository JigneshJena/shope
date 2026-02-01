package com.example.shope.data.repository

import android.util.Log
import com.example.shope.data.models.Customer
import com.example.shope.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class CustomerRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "CustomerRepository"
    }
    
    /**
     * Add new customer - FIX for customer not saving issue
     */
    suspend fun addCustomer(customer: Customer): Result<String> {
        return try {
            // Validate phone doesn't exist
            val exists = checkPhoneExists(customer.phone)
            if (exists) {
                throw Exception("Phone number already exists")
            }
            
            // Create document with auto-generated ID
            val docRef = firestore.collection(Constants.COLLECTION_CUSTOMERS)
                .document()
            
            // Set customer ID
            customer.customerId = docRef.id
            
            // Save to Firestore
            docRef.set(customer).await()
            
            Log.d(TAG, "Customer saved successfully: ${customer.customerId}")
            Result.success(customer.customerId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add customer", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get all customers
     */
    suspend fun getAllCustomers(): Result<List<Customer>> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_CUSTOMERS)
                .whereEqualTo("status", Constants.STATUS_ACTIVE)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val customers = snapshot.toObjects(Customer::class.java)
            Result.success(customers)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get customers", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get customer by ID
     */
    suspend fun getCustomer(customerId: String): Result<Customer> {
        return try {
            val doc = firestore.collection(Constants.COLLECTION_CUSTOMERS)
                .document(customerId)
                .get()
                .await()
            
            val customer = doc.toObject(Customer::class.java) ?: throw Exception("Customer not found")
            Result.success(customer)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get customer", e)
            Result.failure(e)
        }
    }
    
    /**
     * Update customer
     */
    suspend fun updateCustomer(customer: Customer): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_CUSTOMERS)
                .document(customer.customerId)
                .set(customer)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update customer", e)
            Result.failure(e)
        }
    }
    
    /**
     * Delete customer (soft delete - set status to inactive)
     */
    suspend fun deleteCustomer(customerId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_CUSTOMERS)
                .document(customerId)
                .update("status", Constants.STATUS_INACTIVE)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete customer", e)
            Result.failure(e)
        }
    }
    
    /**
     * Search customers by name or phone
     */
    suspend fun searchCustomers(query: String): Result<List<Customer>> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_CUSTOMERS)
                .whereEqualTo("status", Constants.STATUS_ACTIVE)
                .get()
                .await()
            
            val customers = snapshot.toObjects(Customer::class.java)
                .filter { 
                    it.name.contains(query, ignoreCase = true) || 
                    it.phone.contains(query)
                }
            
            Result.success(customers)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to search customers", e)
            Result.failure(e)
        }
    }
    
    /**
     * Check if phone number already exists
     */
    private suspend fun checkPhoneExists(phone: String): Boolean {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_CUSTOMERS)
                .whereEqualTo("phone", phone)
                .whereEqualTo("status", Constants.STATUS_ACTIVE)
                .get()
                .await()
            
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}
