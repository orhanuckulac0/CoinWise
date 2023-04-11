package com.example.investmenttracker.presentation

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.databinding.FragmentSettingsBinding
import com.example.investmenttracker.domain.use_case.util.Constants
import com.example.investmenttracker.domain.use_case.util.changeAppTheme
import com.example.investmenttracker.domain.use_case.util.customGetSerializable
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var binding: FragmentSettingsBinding? = null
    private var navigation: BottomNavigationView? = null
    private var userData: UserData? = null
    private var constraintLayout: ConstraintLayout? = null
    private var ibAboutUs: ImageButton? = null
    private var ibSupport: ImageButton? = null
    private var appBarLayout: AppBarLayout? = null
    private var toolbar: Toolbar? = null
    private var actionBar: ActionBar? = null
    private var sharedPref: SharedPreferences? = null
    private var switchButton: SwitchCompat? = null
    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)
        constraintLayout = binding?.settingsFragmentCL
        switchButton = binding?.customSwitch
        ibAboutUs = binding?.ibAboutUs
        ibSupport = binding?.ibSupport
        toolbar = binding?.toolbarSettingsFragment
        appBarLayout = binding?.appBarLayoutSettingsFragment
        // set back pressed and nav
        navigation = activity?.findViewById(R.id.bottom_navigation) as BottomNavigationView

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
                navigation?.selectedItemId = R.id.home
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback!!)

        // set the user
        arguments?.let {
            userData = it.customGetSerializable(Constants.PASSED_USER)
        }

        // set sharedPref for theme
        sharedPref = requireContext().getSharedPreferences(Constants.THEME_PREF, MODE_PRIVATE)
        val theme = sharedPref!!.getBoolean(Constants.SWITCH_STATE_KEY, true)
        switchButton?.isChecked = theme

        setupActionBar()

        switchButton?.setOnCheckedChangeListener{ _, isChecked->
            val editor = sharedPref?.edit()
            editor?.putBoolean(Constants.SWITCH_STATE_KEY, isChecked)
            editor?.apply()

            changeAppTheme(sharedPref!!.getBoolean(Constants.SWITCH_STATE_KEY, true))
        }
    }

    private fun setupActionBar() {
        val theme = sharedPref?.getBoolean(Constants.SWITCH_STATE_KEY, true)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (actionBar != null){
            actionBar?.title = "Settings"
            actionBar?.setDisplayHomeAsUpEnabled(true)
            if (theme!!){
                actionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            }else{
                actionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_black)
            }
            toolbar?.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
                navigation?.selectedItemId = R.id.home
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        constraintLayout = null
        switchButton = null
        ibAboutUs = null
        ibSupport = null
        actionBar = null
        appBarLayout = null
        toolbar = null
        navigation = null
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
        sharedPref = null
        userData = null
    }
}