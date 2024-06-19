package com.example.storyapp.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository): ViewModel() {
    val isLoading: LiveData<Boolean> = repository.isLoading
    val loginResponse: LiveData<LoginResponse> = repository.loginResponse

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password)
        }
    }

    fun getSession() = repository.getSession()

    fun saveSession(name: String, userId: String, token: String) {
        viewModelScope.launch {
            repository.saveSession(name, userId, token)
        }
    }

    fun userLogout() {
        viewModelScope.launch {
            repository.userLogout()
        }
    }
}