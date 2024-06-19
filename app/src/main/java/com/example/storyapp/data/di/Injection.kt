package com.example.storyapp.data.di

import android.content.Context
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.UserPreference
import com.example.storyapp.data.api.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val userPreferences = UserPreference.getInstance(context)
        return StoryRepository.getInstance(apiService, userPreferences)
    }
}