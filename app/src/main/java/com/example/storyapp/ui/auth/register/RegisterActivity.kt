package com.example.storyapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.UserPreference
import androidx.datastore.preferences.core.Preferences
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.auth.login.LoginActivity
import com.example.storyapp.ui.auth.login.LoginViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: RegisterViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        observeAction()
    }

    private fun observeAction() {
        binding.signupButton.setOnClickListener {
            if (binding.edRegisterName.text.toString()
                    .isEmpty() && binding.edRegisterEmail.text.toString()
                    .isEmpty() && binding.edRegisterPassword.text.toString().isEmpty()
            ) {
                binding.edRegisterName.error = getString(R.string.required_field)
                binding.edRegisterEmail.error = getString(R.string.required_field)
                binding.edRegisterPassword.setError(getString(R.string.required_field), null)
            } else {
                showLoading()
                register()
                intentToLogin()
            }
        }
    }

    private fun register() {
        viewModel.register(
            binding.edRegisterName.text.toString(),
            binding.edRegisterEmail.text.toString(),
            binding.edRegisterPassword.text.toString()
        )
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this@RegisterActivity) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun intentToLogin() {
        viewModel.registerResponse.observe(this@RegisterActivity) {
            if (it.error == false) {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
                showToast()
            }
        }
    }

    private fun showToast() {
        Toast.makeText(this@RegisterActivity, "Register Success", Toast.LENGTH_SHORT).show()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvRegister =
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val tvName = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val edName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val edNameLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val tvEmail =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val edEmail =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val edEmailLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val tvPassword =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val edPasswordLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val btnRegister =
            ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)

        val togetherName = AnimatorSet().apply {
            playTogether(tvName, edName, edNameLayout)
        }

        val togetherEmail = AnimatorSet().apply {
            playTogether(tvEmail, edEmail, edEmailLayout)
        }

        val togetherPassword = AnimatorSet().apply {
            playTogether(tvPassword, edPassword, edPasswordLayout)
        }

        AnimatorSet().apply {
            playSequentially(tvRegister, togetherName, togetherEmail, togetherPassword, btnRegister)
        }.start()
    }
}