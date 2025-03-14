package com.example.mapdemo

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mapdemo.databinding.ActivityMainBinding
import com.example.mapdemo.ui.fragment.LocationListFragment
import com.example.mapdemo.ui.fragment.MapFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolbar

        setupNavigation()
    }

    private fun setupNavigation() {

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_list -> {
                    loadFragment(LocationListFragment())
                    toolbar.title = "Locations"
                    true
                }
                R.id.navigation_map -> {
                    loadFragment(MapFragment())
                    toolbar.title = "Map"
                    true
                }
                else -> false
            }
        }

        // Set initial fragment and title
        loadFragment(LocationListFragment())
        toolbar.title = "Locations"
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}