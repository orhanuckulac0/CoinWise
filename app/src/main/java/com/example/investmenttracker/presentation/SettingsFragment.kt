package com.example.investmenttracker.presentation

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.databinding.FragmentSettingsBinding
import com.example.investmenttracker.domain.use_case.util.Constants
import com.example.investmenttracker.domain.use_case.util.changeAppTheme
import com.example.investmenttracker.domain.use_case.util.customGetSerializable
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var binding: FragmentSettingsBinding? = null
    private var navigation: BottomNavigationView? = null
    private var userData: UserData? = null
    private var constraintLayout: ConstraintLayout? = null
    private lateinit var sharedPref: SharedPreferences
    private var switchButton: SwitchCompat? = null

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

        // set back pressed and nav
        navigation = activity?.findViewById(R.id.bottom_navigation) as BottomNavigationView

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
                navigation?.selectedItemId = R.id.home
            }
        })

        // set the user
        arguments?.let {
            userData = it.customGetSerializable(Constants.PASSED_USER)
        }

        // set sharedPref for theme
        sharedPref = requireContext().getSharedPreferences(Constants.THEME_PREF, MODE_PRIVATE)
        switchButton?.isChecked = sharedPref.getBoolean(Constants.SWITCH_STATE_KEY, true)

        switchButton?.setOnCheckedChangeListener{ _, isChecked->
            val editor = sharedPref.edit()
            editor.putBoolean(Constants.SWITCH_STATE_KEY, isChecked)
            editor.apply()

            changeAppTheme(sharedPref.getBoolean(Constants.SWITCH_STATE_KEY, true))
        }

        setupActionBar()
    }

    private fun setupActionBar() {
        val sharedPref = requireContext().getSharedPreferences(Constants.THEME_PREF, MODE_PRIVATE)
        val theme = sharedPref.getBoolean(Constants.SWITCH_STATE_KEY, true)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding!!.toolbarSettingsFragment)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (actionBar != null){
            actionBar.title = "Settings"
            actionBar.setDisplayHomeAsUpEnabled(true)
            if (theme){
                actionBar.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            }else{
                actionBar.setHomeAsUpIndicator(R.drawable.back_arrow_black)
            }
            binding!!.toolbarSettingsFragment.setNavigationOnClickListener {
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
        navigation = null
        userData = null
    }
}