package com.example.storyapp.ui.auth.login

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.animation.AnimatorSet
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.UserPreference
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: LoginViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.let { response ->
            response.loginResponse.observe(this) {
                response.saveSession(
                    it.loginResult.name,
                    it.loginResult.userId,
                    it.loginResult.token
                )
            }
        }

        playAnimation()
        observeAction()
    }

    private fun observeAction() {
        binding.loginButton.setOnClickListener {
            if (binding.edLoginEmail.text.toString()
                    .isEmpty() && binding.edLoginPassword.text.toString().isEmpty()
            ) {
                binding.edLoginEmail.error = getString(R.string.required_field)
                binding.edLoginPassword.error = getString(R.string.required_field)
            } else {
                showLoading()
                login()
                intentToMain()
            }
        }
    }

    private fun login() {
        viewModel.login(
            binding.edLoginEmail.text.toString(),
            binding.edLoginPassword.text.toString()
        )
    }

    private fun intentToMain() {
        viewModel.loginResponse.observe(this@LoginActivity) {
            if (it.error == false) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
                showToast()
            }
        }
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this@LoginActivity) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun showToast() {
        Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvLogin = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val tvDesc =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val edEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val edEmailLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val tvPassword =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val edPasswordLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        val togetherEmail = AnimatorSet().apply {
            playTogether(tvEmail, edEmail, edEmailLayout)
        }

        val togetherPassword = AnimatorSet().apply {
            playTogether(tvPassword, edPassword, edPasswordLayout)
        }

        AnimatorSet().apply {
            playSequentially(tvLogin, tvDesc, togetherEmail, togetherPassword, btnLogin)
        }.start()
    }
}