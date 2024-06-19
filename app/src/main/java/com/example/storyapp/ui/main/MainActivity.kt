package com.example.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.auth.login.LoginViewModel
import com.example.storyapp.ui.maps.MapsActivity
import com.example.storyapp.ui.story.AddStoryActivity
import com.example.storyapp.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: MainViewModel by viewModels { factory }
    private val loginViewModel: LoginViewModel by viewModels { factory }
    private val adapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.show()

        observeAdapter()
        observeUser()
        updateListStory()
        observeAction()
    }

    private fun observeAdapter() {
        binding.apply {
            val layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.layoutManager = layoutManager
            val itemDecoration = DividerItemDecoration(this@MainActivity, layoutManager.orientation)
            rvStory.addItemDecoration(itemDecoration)
        }
    }

    private fun observeUser() {
        loginViewModel.getSession().observe(this) { session ->
            if (session.userId.isEmpty()) {
                intentToWelcome()
            } else {
                binding.rvStory.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )

                viewModel.getListStories(session.token).observe(this) {
                    adapter.submitData(lifecycle, it)
                }
                showLoading()
            }
        }
    }

    private fun updateListStory() {
        viewModel.storyUpdated.observe(this@MainActivity) { isUpdated ->
            if (isUpdated) {
                observeUser()
                adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        if (positionStart == 0) {
                            binding.rvStory.smoothScrollToPosition(0)
                        }
                    }
                })
            }
        }
    }

    private fun observeAction() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }

        binding.fabMaps.setOnClickListener {
            startActivity(Intent(this@MainActivity, MapsActivity::class.java))
        }

        binding.swipeRefresh.apply {
            setOnRefreshListener {
                observeUser()
                isRefreshing = false
            }
        }
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this@MainActivity) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun intentToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                loginViewModel.userLogout()
                intentToWelcome()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}