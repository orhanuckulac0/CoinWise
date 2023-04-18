package com.example.investmenttracker.presentation

import android.content.SharedPreferences
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var navController: NavController? = null
    private var lastMenuItem: MenuItem? = null
    private var sharedPref: SharedPreferences? = null

    private var navHostFragment: NavHostFragment? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // Set the default night mode to follow the system's theme preference
        sharedPref = getSharedPreferences(Constants.THEME_PREF, MODE_PRIVATE)
        val theme = sharedPref!!.getBoolean(Constants.SWITCH_STATE_KEY, true)
        val currentTheme = AppCompatDelegate.getDefaultNightMode()
        val newTheme = if (theme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        if (currentTheme != newTheme) {
            AppCompatDelegate.setDefaultNightMode(newTheme)
        }


        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment?.navController

        bottomNavigationView = binding?.bottomNavigation
        if (theme){
            bottomNavigationView?.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        }else{
            bottomNavigationView?.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }

        bottomNavigationView?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    navController?.navigate(R.id.mainFragment)
                    item.isEnabled = false
                    lastMenuItem?.isEnabled = true
                    lastMenuItem = item
                    true
                }
                R.id.analytics -> {
                    navController?.navigate(R.id.analyticsFragment)
                    item.isEnabled = false
                    lastMenuItem?.isEnabled = true
                    lastMenuItem = item
                    true
                }
                R.id.search -> {
                    navController?.navigate(R.id.searchCoinFragment)
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
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

        override fun onDestroy() {
        super.onDestroy()
        sharedPref = null
        lastMenuItem = null
        navHostFragment = null
        navController = null
        bottomNavigationView?.setOnItemSelectedListener(null)
        bottomNavigationView?.removeAllViews()
        bottomNavigationView = null
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
        binding!!.mainActivityLL.removeAllViews()
        if (binding != null){
            binding = null
        }
    }
}