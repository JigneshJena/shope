package com.example.shope.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.shope.databinding.ActivitySignupBinding
import com.example.shope.utils.ValidationUtils
import com.example.shope.utils.hide
import com.example.shope.utils.show
import com.example.shope.viewmodel.AuthState
import com.example.shope.viewmodel.AuthViewModel

class SignupActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySignupBinding
    private val viewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            
            if (validateInputs(name, email, phone, password, confirmPassword)) {
                viewModel.signupWithEmail(name, email, phone, password)
            }
        }
        
        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
    
    private fun setupObservers() {
        viewModel.signupState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> showLoading(true)
                is AuthState.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Account created successfully! Please login.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                is AuthState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }
    
    private fun validateInputs(name: String, email: String, phone: String, 
                               password: String, confirmPassword: String): Boolean {
        binding.tilName.error = ValidationUtils.getNameError(name)
        binding.tilEmail.error = ValidationUtils.getEmailError(email)
        binding.tilPhone.error = ValidationUtils.getPhoneError(phone)
        binding.tilPassword.error = ValidationUtils.getPasswordError(password)
        
        binding.tilConfirmPassword.error = when {
            confirmPassword.isEmpty() -> "Confirm password is required"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
        
        return binding.tilName.error == null && binding.tilEmail.error == null &&
                binding.tilPhone.error == null && binding.tilPassword.error == null &&
                binding.tilConfirmPassword.error == null
    }
    
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.show()
            binding.btnSignup.isEnabled = false
        } else {
            binding.progressBar.hide()
            binding.btnSignup.isEnabled = true
        }
    }
}
