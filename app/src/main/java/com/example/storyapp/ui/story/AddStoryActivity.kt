package com.example.storyapp.ui.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.UserPreference
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.auth.login.LoginViewModel
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.ui.main.MainViewModel
import com.example.storyapp.utils.createCustomTempFile
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.rotateBitmap
import com.example.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: AddStoryViewModel by viewModels { factory }
    private val loginViewModel: LoginViewModel by viewModels { factory }
    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )
            binding.ivAddStory.setImageBitmap(result)
        }
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.ivAddStory.setImageURI(selectedImg)
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val CAMERA_X_RESULT = 200
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this@AddStoryActivity,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        observeAction()
    }

    private fun observeAction() {
        binding.btnCamera.setOnClickListener { takePicture() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { postStories() }
    }

    private fun takePicture() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun postStories() {
        showLoading()
        if (getFile != null) {
            if (binding.edDescription.text.toString().isNotEmpty()) {
                val file = reduceFileImage(getFile as File)
                loginViewModel.getSession().observe(this) {
                    viewModel.postStory(it.token, file, binding.edDescription.text.toString())
                    showLoading()
                    intentToMain()
                    Toast.makeText(this@AddStoryActivity, "Story posted successfully", Toast.LENGTH_SHORT).show()
                }
            } else {
                viewModel.postResponse.observe(this) {
                    Toast.makeText(this@AddStoryActivity, "Failed to post stories, description is null", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            viewModel.postResponse.observe(this) {
                Toast.makeText(this@AddStoryActivity, "Failed to post stories, Image is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this@AddStoryActivity) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun intentToMain() {
        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
        showToast()
    }

    private fun showToast() {
        Toast.makeText(this@AddStoryActivity, "Berhasil Upload Story", Toast.LENGTH_SHORT).show()
    }
}