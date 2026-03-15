package com.example.shope.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shope.data.models.User
import com.example.shope.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    
    private val authRepository = AuthRepository()
    
    private val _loginState = MutableLiveData<AuthState>()
    val loginState: LiveData<AuthState> = _loginState
    
    private val _signupState = MutableLiveData<AuthState>()
    val signupState: LiveData<AuthState> = _signupState
    
    private val _resetPasswordState = MutableLiveData<AuthState>()
    val resetPasswordState: LiveData<AuthState> = _resetPasswordState
    
    /**
     * Login with email and password
     */
    fun loginWithEmail(email: String, password: String) {
        _loginState.value = AuthState.Loading
        
        viewModelScope.launch {
            val result = authRepository.loginWithEmail(email, password)
            
            result.onSuccess { user ->
                _loginState.value = AuthState.Success(user)
            }.onFailure { e ->
                _loginState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }
    
    /**
     * Sign up with email and password
     */
    fun signupWithEmail(name: String, email: String, phone: String, password: String, role: String) {
        _signupState.value = AuthState.Loading
        
        viewModelScope.launch {
            val result = authRepository.signupWithEmail(name, email, phone, password, role)
            
            result.onSuccess { user ->
                _signupState.value = AuthState.Success(user)
            }.onFailure { e ->
                _signupState.value = AuthState.Error(e.message ?: "Signup failed")
            }
        }
    }
    
    /**
     * Sign in with Google
     */
    fun signInWithGoogle(account: GoogleSignInAccount) {
        _loginState.value = AuthState.Loading
        
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(account)
            
            result.onSuccess { user ->
                _loginState.value = AuthState.Success(user)
            }.onFailure { e ->
                _loginState.value = AuthState.Error(e.message ?: "Google sign in failed")
            }
        }
    }
    
    /**
     * Send password reset email
     */
    fun sendPasswordResetEmail(email: String) {
        _resetPasswordState.value = AuthState.Loading
        
        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(email)
            
            result.onSuccess {
                _resetPasswordState.value = AuthState.PasswordResetSent
            }.onFailure { e ->
                _resetPasswordState.value = AuthState.Error(e.message ?: "Failed to send reset email")
            }
        }
    }
    
    /**
     * Get user role
     */
    suspend fun getUserRole(userId: String): String {
        return authRepository.getUserRole(userId)
    }
    
    /**
     * Logout
     */
    fun logout() {
        authRepository.logout()
    }
}

/**
 * Authentication state sealed class
 */
sealed class AuthState {
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
    object PasswordResetSent : AuthState()
}
