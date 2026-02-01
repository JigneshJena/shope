package com.example.shope.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shope.data.models.Employee
import com.example.shope.data.models.School
import com.example.shope.data.repository.EmployeeRepository
import com.example.shope.data.repository.OrderRepository
import com.example.shope.data.repository.SchoolRepository
import com.example.shope.data.repository.InventoryRepository
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

    private val _stats = MutableLiveData<OwnerStats>()
    val stats: LiveData<OwnerStats> = _stats

    private val _employees = MutableLiveData<List<Employee>>()
    val employees: LiveData<List<Employee>> = _employees

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadDashboardStats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val employeesResult = employeeRepo.getAllEmployees().getOrDefault(emptyList())
                val schoolsCount = schoolRepo.getSchoolCount()
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
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadEmployees() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = employeeRepo.getAllEmployees()
            _employees.value = result.getOrDefault(emptyList())
            _isLoading.value = false
        }
    }
    private val _schools = MutableLiveData<List<School>>()
    val schools: LiveData<List<School>> = _schools

    fun loadSchools() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = schoolRepo.getAllSchools()
            _schools.value = result.getOrDefault(emptyList())
            _isLoading.value = false
        }
    }

    fun addSchool(school: School) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = schoolRepo.addSchool(school)
            if (result.isSuccess) {
                loadSchools() // Refresh list
                loadDashboardStats() // Refresh stats
            }
            _isLoading.value = false
        }
    }

    fun addEmployee(employee: Employee, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = employeeRepo.addEmployee(employee, password)
            if (result.isSuccess) {
                loadEmployees() // Refresh list
                loadDashboardStats() // Refresh stats
            }
            _isLoading.value = false
        }
    }
}
