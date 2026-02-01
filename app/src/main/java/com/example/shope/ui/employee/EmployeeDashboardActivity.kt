package com.example.shope.ui.employee

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shope.R

/**
 * Employee Dashboard Activity
 * 
 * Features (To be implemented):
 * - Bottom Navigation with 6 tabs
 * - Tab 1: Home (Quick Actions, Recent Activity, Quick Stats)
 * - Tab 2: Customers (List, Add, Edit, View Details)
 * - Tab 3: Orders (List by status, Create, Edit, Update Status)
 * - Tab 4: Inventory (List, Add, Edit, Adjust Stock)
 * - Tab 5: Billing (Create Invoice, Payment History)
 * - Tab 6: More (Measurements, Reports, Profile)
 */
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.shope.databinding.ActivityEmployeeDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class EmployeeDashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityEmployeeDashboardBinding
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        navController.setGraph(R.navigation.nav_employee)
        
        binding.bottomNavigation.setupWithNavController(navController)
    }
}

