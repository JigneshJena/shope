package com.example.shope.ui.owner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shope.R

/**
 * Owner Dashboard Activity
 * 
 * Features (To be implemented):
 * - Bottom Navigation with 5 tabs
 * - Tab 1: Home/Overview (Statistics, Quick Stats, Recent Activity)
 * - Tab 2: Employees (List, Add, Edit, Manage)
 * - Tab 3: Schools (List, Add, Edit, View Orders)
 * - Tab 4: Reports (Inventory, Profit & Loss, Sales History)
 * - Tab 5: Settings (Shop Profile, Owner Profile, Business Settings)
 */
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.shope.databinding.ActivityOwnerDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class OwnerDashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOwnerDashboardBinding
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Fix for nav graph assignment in code
        navController.setGraph(R.navigation.nav_owner)
        
        binding.bottomNavigation.setupWithNavController(navController)
        
        setupToolbar()
    }

    private fun setupToolbar() {
        val prefManager = com.example.shope.utils.PreferenceManager(this)
        binding.tvUserName.text = prefManager.getUserName() ?: "Shop Owner"
        
        binding.ivUserProfile.setOnClickListener {
            // Navigate to profile/settings
            navController.navigate(R.id.navigation_settings)
        }
    }
}

