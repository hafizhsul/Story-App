package com.example.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.response.StoryResponse
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {
    val isLoading: LiveData<Boolean> = repository.isLoading
    val listStory: LiveData<StoryResponse> = repository.listStory

    fun getSession() = repository.getSession()

    fun getStoriesWithLocation(token: String) {
        viewModelScope.launch {
            repository.getStoriesWithLocation(token)
        }
    }
}