package com.example.shope.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shope.data.models.Employee
import com.example.shope.data.models.School
import com.example.shope.data.models.UniformItem
import com.example.shope.data.repository.EmployeeRepository
import com.example.shope.data.repository.OrderRepository
import com.example.shope.data.repository.SchoolRepository
import com.example.shope.data.repository.InventoryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

data class OwnerStats(
    val totalEmployees: Int = 0,
    val activeEmployees: Int = 0,
    val totalSchools: Int = 0,
    val todaysSales: Double = 0.0,
    val lowStockCount: Int = 0
)

class OwnerViewModel : ViewModel() {
    private val employeeRepo = EmployeeRepository()
    private val schoolRepo = SchoolRepository()
    private val orderRepo = OrderRepository()
    private val inventoryRepo = InventoryRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _stats = MutableLiveData<OwnerStats>()
    val stats: LiveData<OwnerStats> = _stats

    private val _employees = MutableLiveData<List<Employee>>()
    val employees: LiveData<List<Employee>> = _employees

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var employeesListener: ListenerRegistration? = null
    
    // Get current owner UID
    private val ownerUid: String?
        get() = auth.currentUser?.uid

    /**
     * Start listening to employees in real-time
     */
    fun startEmployeesListener() {
        val uid = ownerUid ?: return
        employeesListener?.remove()
        employeesListener = employeeRepo.getEmployeesRealtime(uid) { list ->
            _employees.value = list
        }
    }

    fun loadDashboardStats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch stats from repositories
                val uid = ownerUid ?: return@launch
                val employeesResult = _employees.value ?: emptyList()
                val schoolsCount = schoolRepo.getSchoolCount(uid)
                val lowStock = inventoryRepo.getLowStockItemsCount()
                val todaysSalesResult = orderRepo.getTodaysTotalSales()

                _stats.value = OwnerStats(
                    totalEmployees = employeesResult.size,
                    activeEmployees = employeesResult.count { it.status == "active" },
                    totalSchools = schoolsCount,
                    todaysSales = todaysSalesResult.getOrDefault(0.0),
                    lowStockCount = lowStock
                )
            } catch (e: Exception) {
                _error.value = "Failed to load dashboard stats"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addEmployee(employee: Employee, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = employeeRepo.addEmployee(employee, password)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to add employee"
            }
            _isLoading.value = false
        }
    }

    fun updateEmployee(employee: Employee) {
        val uid = ownerUid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = employeeRepo.updateEmployee(uid, employee)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to update employee"
            }
            _isLoading.value = false
        }
    }

    fun deleteEmployee(employeeId: String) {
        val uid = ownerUid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = employeeRepo.deleteEmployee(uid, employeeId)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to delete employee"
            }
            _isLoading.value = false
        }
    }

    fun resetEmployeePassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = employeeRepo.resetPassword(email)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to send reset email"
            }
            _isLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        employeesListener?.remove()
    }
    
    // Schools management
    private val _schools = MutableLiveData<List<School>>()
    val schools: LiveData<List<School>> = _schools

    private val _currentSchool = MutableLiveData<School?>()
    val currentSchool: LiveData<School?> = _currentSchool

    fun loadSchools() {
        viewModelScope.launch {
            val ownerUid = auth.currentUser?.uid ?: return@launch
            _isLoading.value = true
            val result = schoolRepo.getAllSchools(ownerUid)
            _schools.value = result.getOrDefault(emptyList())
            _isLoading.value = false
        }
    }

    fun loadSchoolById(schoolId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = schoolRepo.getSchoolById(schoolId)
            _currentSchool.value = result.getOrDefault(null)
            _isLoading.value = false
        }
    }

    fun addSchool(school: School) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = schoolRepo.addSchool(school)
            if (result.isSuccess) {
                loadSchools()
            }
            _isLoading.value = false
        }
    }

    fun updateProductsForSchool(schoolId: String, items: List<com.example.shope.data.models.UniformItem>) {
        viewModelScope.launch {
            val school = _currentSchool.value ?: return@launch
            school.uniformItems = items
            val result = schoolRepo.updateSchool(school)
            if (result.isSuccess) {
                _currentSchool.value = school
            }
            _isLoading.value = false
        }
    }
}
