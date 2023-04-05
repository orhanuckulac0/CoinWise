package com.example.investmenttracker.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.investmenttracker.R
import com.example.investmenttracker.databinding.ActivityMainBinding
import com.example.investmenttracker.domain.use_case.util.Constants
import com.example.investmenttracker.domain.use_case.util.changeAppTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var lastMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the default night mode to follow the system's theme preference
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val sharedPref = getSharedPreferences(Constants.THEME_PREF, MODE_PRIVATE)
        val theme = sharedPref.getBoolean(Constants.SWITCH_STATE_KEY, true)
        changeAppTheme(theme)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (theme){
            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        }else{
            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.mainFragment)
                    item.isEnabled = false
                    lastMenuItem?.isEnabled = true
                    lastMenuItem = item
                    true
                }
                R.id.analytics -> {
                    navController.navigate(R.id.analyticsFragment)
                    item.isEnabled = false
                    lastMenuItem?.isEnabled = true
                    lastMenuItem = item
                    true
                }
                R.id.search -> {
                    navController.navigate(R.id.searchCoinFragment)
                    item.isEnabled = false
                    lastMenuItem?.isEnabled = true
                    lastMenuItem = item
                    true
                }
                R.id.invisibleItem -> {
                    item.isEnabled = false
                    lastMenuItem?.isEnabled = true
                    lastMenuItem = item
                    true
                }
                else -> false
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        lastMenuItem = null
    }
}