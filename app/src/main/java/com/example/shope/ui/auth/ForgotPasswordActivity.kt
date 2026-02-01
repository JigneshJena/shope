package com.example.shope.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.shope.databinding.ActivityForgotPasswordBinding
import com.example.shope.utils.ValidationUtils
import com.example.shope.utils.hide
import com.example.shope.utils.show
import com.example.shope.viewmodel.AuthState
import com.example.shope.viewmodel.AuthViewModel

class ForgotPasswordActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnSubmit.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            
            if (ValidationUtils.isValidEmail(email)) {
                viewModel.sendPasswordResetEmail(email)
            } else {
                binding.tilEmail.error = "Please enter a valid email"
            }
        }
        
        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }
    
    private fun setupObservers() {
        viewModel.resetPasswordState.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> showLoading(true)
                is AuthState.PasswordResetSent -> {
                    showLoading(false)
                    Toast.makeText(this, "Password reset email sent! Check your inbox.", Toast.LENGTH_LONG).show()
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
    
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.show()
            binding.btnSubmit.isEnabled = false
        } else {
            binding.progressBar.hide()
            binding.btnSubmit.isEnabled = true
        }
    }
}
