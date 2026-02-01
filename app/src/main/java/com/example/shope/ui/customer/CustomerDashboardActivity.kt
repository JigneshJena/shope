package com.example.shope.ui.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shope.R

/**
 * Customer Dashboard Activity
 * 
 * Features (To be implemented):
 * - Bottom Navigation with 4 tabs
 * - Tab 1: Shop/Browse (Product Grid, Search, Filters, Add to Cart)
 * - Tab 2: My Orders (Active, Completed, Cancelled, Track Orders)
 * - Tab 3: Cart (Cart Items, Checkout Flow)
 * - Tab 4: Profile (Personal Info, Addresses, Measurements, Settings)
 */
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.shope.databinding.ActivityCustomerDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomerDashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCustomerDashboardBinding
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        navController.setGraph(R.navigation.nav_customer)
        
        binding.bottomNavigation.setupWithNavController(navController)
    }
}

