package com.example.storyapp.ui.story

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.UserPreference
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.data.response.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository): ViewModel() {
    val isLoading: LiveData<Boolean> = repository.isLoading
    val postResponse: LiveData<AddStoryResponse> = repository.postResponse

    fun postStory(token: String, file: File, description: String): LiveData<AddStoryResponse> {
        viewModelScope.launch {
            repository.postStory(token, file, description)
        }
        return postResponse
    }
}