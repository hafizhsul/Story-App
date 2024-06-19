package com.example.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.response.ListStoryItem

class MainViewModel(private val repository: StoryRepository): ViewModel() {
    val isLoading: LiveData<Boolean> = repository.isLoading
    val storyUpdated: LiveData<Boolean> = repository.storyUpdate

    fun getListStories(token: String): LiveData<PagingData<ListStoryItem>> = repository.getListStories(token).cachedIn(viewModelScope)
}