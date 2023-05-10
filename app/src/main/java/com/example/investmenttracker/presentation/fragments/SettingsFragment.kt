package com.example.investmenttracker.presentation.fragments

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.databinding.FragmentSettingsBinding
import com.example.investmenttracker.domain.use_case.util.Constants
import com.example.investmenttracker.domain.use_case.util.customGetSerializable
import com.example.investmenttracker.domain.use_case.util.showAboutAppDialog
import com.example.investmenttracker.presentation.view_model.SettingsViewModel
import com.example.investmenttracker.presentation.view_model_factory.SettingsViewModelFactory
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var binding: FragmentSettingsBinding? = null
    @Inject
    lateinit var factory: SettingsViewModelFactory
    lateinit var viewModel: SettingsViewModel
    private var navigation: BottomNavigationView? = null
    private var userData: UserData? = null
    private var constraintLayout: ConstraintLayout? = null
    private var appBarLayout: AppBarLayout? = null
    private var toolbar: Toolbar? = null
    private var actionBar: ActionBar? = null
    private var sharedPref: SharedPreferences? = null
    private var switchButton: SwitchCompat? = null
    private var aboutAppButton: ImageButton? = null
    private var spinner: Spinner? = null
    private var arrayAdapter: ArrayAdapter<String>? = null
    private var onItemSelectedListener: OnItemSelectedListener? = null
    private var onBackPressedCallback: OnBackPressedCallback? = null

    private var aboutAppDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)
        constraintLayout = binding?.settingsFragmentCL
        appBarLayout = binding?.appBarLayoutSettingsFragment
        toolbar = binding?.toolbarSettingsFragment
        switchButton = binding?.customSwitch
        aboutAppButton = binding?.ibAboutApp
        spinner = binding?.spinnerCurrencyConverter
        aboutAppDialog = showAboutAppDialog(requireContext())

        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
        navigation = activity?.findViewById(R.id.bottom_navigation) as BottomNavigationView

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
                navigation?.selectedItemId = R.id.home
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback!!)

        arguments?.let {
            userData = it.customGetSerializable(Constants.PASSED_USER)
        }

        sharedPref = requireContext().getSharedPreferences(Constants.THEME_PREF, MODE_PRIVATE)
        val theme = sharedPref!!.getBoolean(Constants.SWITCH_STATE_KEY, true)


        switchButton?.let { switch ->
            switchButton?.isChecked = theme

            switch.setOnCheckedChangeListener { _, isChecked ->
                val editor = sharedPref?.edit()
                editor?.putBoolean(Constants.SWITCH_STATE_KEY, isChecked)
                editor?.apply()

                viewModel.changeTheme(isChecked)
            }
        }

        aboutAppButton?.setOnClickListener{
            aboutAppDialog?.show()
        }

        setupActionBar()
        setupArrayAdapter()
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

    private fun setupArrayAdapter(){

        val currencies = arrayOf(
            Constants.TRY,
            Constants.USD,
            Constants.CAD,
            Constants.AUD,
            Constants.EUR,
            Constants.NZD,
            Constants.SGD
        )
        arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, currencies)
        spinner?.adapter = arrayAdapter

        val defaultCurrencyIndex = currencies.indexOfFirst { it == (userData?.userCurrentCurrency ?: "") }
        spinner?.setSelection(defaultCurrencyIndex)

        val currencyBeforeChange = userData?.userCurrentCurrency
        onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val updatedUser = userData?.copy(
                    userCurrentCurrency = currencies[position],
                    userPreviousCurrency = currencyBeforeChange!!
                    )
                viewModel.updateUser(updatedUser!!)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner?.onItemSelectedListener = onItemSelectedListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toolbar?.setOnClickListener(null)
        toolbar = null
        appBarLayout = null
        actionBar = null
        switchButton?.setOnClickListener(null)
        switchButton = null
        aboutAppButton?.setOnClickListener(null)
        aboutAppButton = null
        spinner?.onItemSelectedListener = null
        spinner = null
        onItemSelectedListener = null
        arrayAdapter = null
        aboutAppDialog = null
        navigation = null
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
        sharedPref = null
        userData = null
        constraintLayout?.removeAllViews()
        constraintLayout = null
        binding = null
    }
}