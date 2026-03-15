package com.example.shope.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.shope.R
import com.example.shope.databinding.ActivityLoginBinding
import com.example.shope.ui.admin.AdminDashboardActivity
import com.example.shope.ui.customer.CustomerDashboardActivity
import com.example.shope.ui.employee.EmployeeDashboardActivity
import com.example.shope.ui.owner.OwnerDashboardActivity
import com.example.shope.utils.Constants
import com.example.shope.utils.PreferenceManager
import com.example.shope.utils.ValidationUtils
import com.example.shope.utils.hide
import com.example.shope.utils.show
import com.example.shope.viewmodel.AuthState
import com.example.shope.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var prefManager: PreferenceManager
    private lateinit var googleSignInClient: GoogleSignInClient
    
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let { viewModel.signInWithGoogle(it) }
        } catch (e: ApiException) {
            Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefManager = PreferenceManager(this)
        
        // Check if user is already logged in
        if (prefManager.isLoggedIn()) {
            val email = prefManager.getUserEmail() ?: ""
            val role = when {
                email.contains("admin@", ignoreCase = true) || prefManager.getUserRole() == Constants.ROLE_ADMIN -> Constants.ROLE_ADMIN
                email.contains("@owner", ignoreCase = true) -> Constants.ROLE_OWNER
                email.contains("@employee", ignoreCase = true) -> Constants.ROLE_EMPLOYEE
                else -> prefManager.getUserRole() ?: Constants.ROLE_CUSTOMER
            }
            navigateToDashboard(role)
            return
        }
        
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (validateInputs(email, password)) {
                viewModel.loginWithEmail(email, password)
            }
        }
        
        binding.btnGoogleSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
        
        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
        
        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
    
    private fun setupObservers() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    showLoading(true)
                }
                is AuthState.Success -> {
                    showLoading(false)
                    val user = state.user
                    
                    // Determine role using User model logic (handles @owner convention)
                    val effectiveRole = when {
                        user.isAdmin() -> Constants.ROLE_ADMIN
                        user.isOwner() -> Constants.ROLE_OWNER
                        user.isEmployee() -> Constants.ROLE_EMPLOYEE
                        else -> user.role.ifEmpty { Constants.ROLE_CUSTOMER }
                    }
                    
                    // Save session with effective role
                    prefManager.saveUserSession(user.userId, user.name, user.email, effectiveRole)
                    
                    // Navigate to appropriate dashboard
                    navigateToDashboard(effectiveRole)
                }
                is AuthState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }
    
    private fun validateInputs(email: String, password: String): Boolean {
        binding.tilEmail.error = ValidationUtils.getEmailError(email)
        binding.tilPassword.error = ValidationUtils.getPasswordError(password)
        
        return binding.tilEmail.error == null && binding.tilPassword.error == null
    }
    
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.show()
            binding.btnLogin.isEnabled = false
            binding.btnGoogleSignIn.isEnabled = false
        } else {
            binding.progressBar.hide()
            binding.btnLogin.isEnabled = true
            binding.btnGoogleSignIn.isEnabled = true
        }
    }
    
    private fun navigateToDashboard(role: String) {
        val intent = when (role) {
            Constants.ROLE_ADMIN -> Intent(this, AdminDashboardActivity::class.java)
            Constants.ROLE_OWNER -> Intent(this, OwnerDashboardActivity::class.java)
            Constants.ROLE_EMPLOYEE -> Intent(this, EmployeeDashboardActivity::class.java)
            else -> Intent(this, CustomerDashboardActivity::class.java)
        }
        
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
