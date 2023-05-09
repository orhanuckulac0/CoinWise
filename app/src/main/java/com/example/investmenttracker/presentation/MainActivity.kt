package com.example.investmenttracker.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.investmenttracker.R
import com.example.investmenttracker.databinding.ActivityMainBinding
import com.example.investmenttracker.domain.use_case.util.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var navController: NavController? = null
    private var lastMenuItem: MenuItem? = null

    private var navHostFragment: NavHostFragment? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val sharedPref = getSharedPreferences(Constants.THEME_PREF, MODE_PRIVATE)
        val theme = sharedPref!!.getBoolean(Constants.SWITCH_STATE_KEY, true)

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

    private fun updateSharedPrefValues(){
        lifecycleScope.launch(Dispatchers.IO) {
            val sharedPrefIsFinished = applicationContext.getSharedPreferences(Constants.PREF_WORKER_RESULT, MODE_PRIVATE)
            val sharedPrefCount = applicationContext.getSharedPreferences(Constants.PREF_WORKER, MODE_PRIVATE)
            val isThemeChangedSharedPref = applicationContext.getSharedPreferences(Constants.IS_THEME_CHANGED, MODE_PRIVATE)

            sharedPrefIsFinished?.edit()?.putBoolean(Constants.WORKER_RESULT, true)?.apply()

            val isChanged = isThemeChangedSharedPref!!.getBoolean(Constants.THEME_CHANGED_PREF, false)
            if (isChanged){
                isThemeChangedSharedPref.edit().remove(Constants.THEME_CHANGED_PREF).apply()
            }else{
                sharedPrefCount?.edit()?.putInt(Constants.WORKER_COUNT, 100)?.apply()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        updateSharedPrefValues()
    }

    override fun onDestroy() {
        super.onDestroy()
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