package com.example.storyapp.ui.maps

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.ui.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var nMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: MapsViewModel by viewModels { factory }
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        nMap = googleMap
        nMap.uiSettings.isZoomControlsEnabled = true
        nMap.uiSettings.isIndoorLevelPickerEnabled = true
        nMap.uiSettings.isCompassEnabled = true
        nMap.uiSettings.isMapToolbarEnabled = true


        observeUser()
        observeList()
        setMapStyle()
        getMyLocation()
    }

    private fun observeList() {
        viewModel.listStory.observe(this@MapsActivity) { listStory ->
            listStory?.listStory?.forEach { story ->
                nMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(story.lat, story.lon))
                        .title(story.name)
                        .snippet(story.id)
                )
            }
            if (listStory.listStory.isNotEmpty()) {
                val random = (0 until listStory.listStory.size).random()
                nMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            listStory.listStory[random].lat,
                            listStory.listStory[random].lon
                        ), 15f
                    )
                )
            } else {
                val lastLocation = nMap.myLocation.let { location: Location? ->
                    LatLng(
                        location?.latitude ?: 0.0,
                        location?.longitude ?: 0.0
                    )
                }
                nMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        lastLocation, 15f
                    )
                )
            }
        }
    }

    private fun observeUser() {
        viewModel.getSession().observe(this@MapsActivity) { response ->
            token = response.token
            getStories(token)
        }
    }

    private fun getStories(token: String) {
        viewModel.getStoriesWithLocation(token)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            nMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success = nMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(ContentValues.TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(ContentValues.TAG, "Can't find style. Error: ", exception)
        }
    }
}