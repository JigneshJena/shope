package com.example.shope.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shope.data.models.Customer
import com.example.shope.data.models.Employee
import com.example.shope.data.models.User
import com.example.shope.data.repository.AdminRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val adminRepository = AdminRepository()
    
    private val _owners = MutableLiveData<List<User>>()
    val owners: LiveData<List<User>> = _owners
    
    private val _employees = MutableLiveData<List<Employee>>()
    val employees: LiveData<List<Employee>> = _employees
    
    private val _customers = MutableLiveData<List<Customer>>()
    val customers: LiveData<List<Customer>> = _customers
    
    private val _adminState = MutableLiveData<AdminState>()
    val adminState: LiveData<AdminState> = _adminState
    
    private var ownersListener: ListenerRegistration? = null
    private var employeesListener: ListenerRegistration? = null
    private var customersListener: ListenerRegistration? = null

    init {
        listenToOwners()
        listenToEmployees()
        listenToCustomers()
    }

    private fun listenToOwners() {
        ownersListener = adminRepository.getAllOwnersRealtime { list ->
            _owners.value = list
        }
    }

    private fun listenToEmployees() {
        employeesListener = adminRepository.getAllEmployeesRealtime { list ->
            _employees.value = list
        }
    }

    private fun listenToCustomers() {
        customersListener = adminRepository.getAllCustomersRealtime { list ->
            _customers.value = list
        }
    }

    fun addOwner(user: User, password: String) {
        _adminState.value = AdminState.Loading
        viewModelScope.launch {
            val result = adminRepository.addOwner(user, password)
            result.onSuccess {
                _adminState.value = AdminState.Success("Owner added successfully")
            }.onFailure { e ->
                _adminState.value = AdminState.Error(e.message ?: "Failed to add owner")
            }
        }
    }

    fun updateOwner(user: User) {
        _adminState.value = AdminState.Loading
        viewModelScope.launch {
            val result = adminRepository.updateOwner(user)
            result.onSuccess {
                _adminState.value = AdminState.Success("Owner updated successfully")
            }.onFailure { e ->
                _adminState.value = AdminState.Error(e.message ?: "Failed to update owner")
            }
        }
    }

    fun deleteOwner(ownerUid: String) {
        _adminState.value = AdminState.Loading
        viewModelScope.launch {
            val result = adminRepository.deleteOwner(ownerUid)
            result.onSuccess {
                _adminState.value = AdminState.Success("Owner deleted successfully")
            }.onFailure { e ->
                _adminState.value = AdminState.Error(e.message ?: "Failed to delete owner")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ownersListener?.remove()
        employeesListener?.remove()
        customersListener?.remove()
    }
}

sealed class AdminState {
    object Loading : AdminState()
    data class Success(val message: String) : AdminState()
    data class Error(val message: String) : AdminState()
}
