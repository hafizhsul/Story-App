package com.example.storyapp.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: StoryRepository): ViewModel() {
    val isLoading: LiveData<Boolean> = repository.isLoading
    val registerResponse: LiveData<RegisterResponse> = repository.registerResponse

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            repository.register(name, email, password)
        }
    }
}