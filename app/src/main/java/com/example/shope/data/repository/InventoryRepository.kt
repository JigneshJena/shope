package com.example.shope.data.repository

import android.util.Log
import com.example.shope.data.models.Inventory
import com.example.shope.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class InventoryRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "InventoryRepository"
    }
    
    /**
     * Add inventory item
     */
    suspend fun addInventoryItem(item: Inventory): Result<String> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_INVENTORY)
                .document()
            
            item.itemId = docRef.id
            
            // Calculate profit margin
            item.profitMargin = if (item.purchasePrice > 0) {
                ((item.sellingPrice - item.purchasePrice) / item.purchasePrice) * 100
            } else {
                0.0
            }
            
            docRef.set(item).await()
            
            Log.d(TAG, "Inventory item added: ${item.itemId}")
            Result.success(item.itemId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add inventory item", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get all inventory items
     */
    suspend fun getAllInventoryItems(): Result<List<Inventory>> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_INVENTORY)
                .whereEqualTo("status", Constants.STATUS_ACTIVE)
                .get()
                .await()
            
            val items = snapshot.toObjects(Inventory::class.java)
            Result.success(items)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get inventory", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get total stock value
     */
    suspend fun getTotalStockValue(): Double {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_INVENTORY)
                .whereEqualTo("status", Constants.STATUS_ACTIVE)
                .get()
                .await()
            
            snapshot.toObjects(Inventory::class.java)
                .sumOf { it.quantity * it.sellingPrice }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to calculate stock value", e)
            0.0
        }
    }
    
    /**
     * Get low stock items count
     */
    suspend fun getLowStockItemsCount(): Int {
        return try {
            val result = getLowStockItems()
            result.getOrDefault(emptyList()).size
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Get list of low stock items
     */
    suspend fun getLowStockItems(): Result<List<Inventory>> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_INVENTORY)
                .whereEqualTo("status", Constants.STATUS_ACTIVE)
                .get()
                .await()
            
            val allItems = snapshot.toObjects(Inventory::class.java)
            val lowStockItems = allItems.filter { it.isLowStock() || it.isOutOfStock() }
            Result.success(lowStockItems)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get low stock items", e)
            Result.failure(e)
        }
    }

    
    /**
     * Set absolute stock quantity
     */
    suspend fun setStockQuantity(itemId: String, newQuantity: Int): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_INVENTORY)
                .document(itemId)
                .update("quantity", newQuantity)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set stock quantity", e)
            Result.failure(e)
        }
    }

    /**
     * Adjust stock quantity
     */
    suspend fun adjustStock(itemId: String, quantityChange: Int, schoolId: String = ""): Result<Unit> {
        return try {
            if (schoolId.isNotEmpty()) {
                // Update nested school inventory
                val schoolRef = firestore.collection(Constants.COLLECTION_SCHOOLS)
                    .document(schoolId)
                
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(schoolRef)
                    if (snapshot.exists()) {
                        val school = snapshot.toObject(com.example.shope.data.models.School::class.java)
                        school?.let { s ->
                            val updatedItems = s.uniformItems.toMutableList()
                            val index = updatedItems.indexOfFirst { it.id == itemId }
                            if (index != -1) {
                                val item = updatedItems[index]
                                val currentQty = item.quantity
                                val newQty = (currentQty + quantityChange).coerceAtLeast(0)
                                updatedItems[index] = item.copy(quantity = newQty)
                                transaction.update(schoolRef, "uniformItems", updatedItems)
                            }
                        }
                    } else {
                        Log.w(TAG, "School $schoolId not found for nested stock adjustment")
                    }
                }.await()
            } else {
                // Standard inventory update
                val docRef = firestore.collection(Constants.COLLECTION_INVENTORY)
                    .document(itemId)
                
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(docRef)
                    if (snapshot.exists()) {
                        val currentQty = snapshot.getLong("quantity")?.toInt() ?: 0
                        val newQty = (currentQty + quantityChange).coerceAtLeast(0)
                        transaction.update(docRef, "quantity", newQty)
                    } else {
                        Log.w(TAG, "Item $itemId not found in inventory for stock adjustment")
                    }
                }.await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to adjust stock", e)
            Result.failure(e)
        }
    }
    
    /**
     * Update inventory item
     */
    suspend fun updateInventoryItem(item: Inventory): Result<Unit> {
        return try {
            // Recalculate profit margin
            item.profitMargin = if (item.purchasePrice > 0) {
                ((item.sellingPrice - item.purchasePrice) / item.purchasePrice) * 100
            } else {
                0.0
            }
            
            firestore.collection(Constants.COLLECTION_INVENTORY)
                .document(item.itemId)
                .set(item)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update inventory", e)
            Result.failure(e)
        }
    }
    
    /**
     * Delete inventory item (soft delete)
     */
    suspend fun deleteInventoryItem(itemId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_INVENTORY)
                .document(itemId)
                .update("status", Constants.STATUS_INACTIVE)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete inventory", e)
            Result.failure(e)
        }
    }
}
