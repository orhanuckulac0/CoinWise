package com.example.investmenttracker.presentation.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.databinding.FragmentMainBinding
import com.example.investmenttracker.domain.use_case.util.*
import com.example.investmenttracker.presentation.adapter.MainFragmentAdapter
import com.example.investmenttracker.presentation.view_model.MainViewModel
import com.example.investmenttracker.presentation.view_model_factory.MainViewModelFactory
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var binding: FragmentMainBinding? = null
    @Inject
    lateinit var factory: MainViewModelFactory
    lateinit var viewModel: MainViewModel
    private var walletAdapter: MainFragmentAdapter? = null
    private var constraintLayout: ConstraintLayout? = null
    private var navigation: BottomNavigationView? = null

    private var menuProvider: MenuProvider? = null
    private var menuHost: MenuHost? = null
    private var menuItem: MenuItem? = null

    private var toolbar: Toolbar? = null
    private var appBarLayout: AppBarLayout? = null
    private var actionBar: ActionBar? = null

    private var mProgressDialog: Dialog? = null
    private var sharedPrefRefresh: SharedPreferences? = null
    private var sharedPrefTheme: SharedPreferences? = null

    private var onBackPressedCallback: OnBackPressedCallback? = null
    private var lastApiRequestTime: Long = 0
    private var populated = false
    private var sorted = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        constraintLayout = binding?.mainFragmentCL
        toolbar = binding?.toolbarMainFragment
        appBarLayout = binding?.appBarLayoutMainFragment
        menuHost = requireActivity()

        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        walletAdapter = MainFragmentAdapter(requireContext())

        sharedPrefTheme = requireContext().getSharedPreferences(Constants.THEME_PREF, MODE_PRIVATE)

        mProgressDialog = showProgressDialog(requireContext())
        navigation = activity?.findViewById(R.id.bottom_navigation) as BottomNavigationView

        sharedPrefRefresh = requireContext().getSharedPreferences(Constants.REFRESH_STATE, MODE_PRIVATE)
        lastApiRequestTime = sharedPrefRefresh!!.getLong(Constants.LAST_API_REQUEST_TIME, 0)

        viewModel.getTokensFromWallet()
        triggerAppLaunch()

        binding!!.tvSortTokensByValue.setOnClickListener {
            sorted = if (!sorted){
                val sortedList = sortCoinsByAscending(walletAdapter!!.differ.currentList)
                walletAdapter!!.differ.submitList(sortedList)
                !sorted
            }else{
                val sortedList = sortCoinsByDescending(walletAdapter!!.differ.currentList)
                walletAdapter!!.differ.submitList(sortedList)
                !sorted
            }
            binding!!.rvTokens.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Remove the listener to avoid infinite loops
                    binding!!.rvTokens.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    // Scroll to the top of the list
                    binding!!.rvTokens.scrollToPosition(0)
                }
            })
        }

        onBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback!!)

        setupActionBar()
        setupMenu()
    }

    private fun triggerAppLaunch() {
        if (shouldMakeApiRequest(lastApiRequestTime)) {
            lifecycleScope.launch(Dispatchers.IO){

                // reset current values
                viewModel.currentWalletCoins.clear()
                viewModel.slugNames = ""
                viewModel.newTokensDataResponse.clear()
                withContext(Dispatchers.Main){
                    viewModel.multipleCoinsListResponse.value = null
                }
                // make request
                requestNewDataForWalletCoins()

                // Update lastApiRequestTime
                lastApiRequestTime = System.currentTimeMillis()

                // Save lastApiRequestTime in shared preferences
                lifecycleScope.launch(Dispatchers.IO) {
                    val editor = sharedPrefRefresh?.edit()
                    editor?.putLong(Constants.LAST_API_REQUEST_TIME, lastApiRequestTime)
                    editor?.apply()
                }
            }
        }else{
            populateFromCache()
        }
    }

    private fun populateFromCache(){
        lifecycleScope.launch(Dispatchers.Main) {
            mProgressDialog!!.show()
            setupView(viewModel.currentWalletCoins)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupView(coinsList: MutableList<CoinModel>) {

        binding!!.rvTokens.apply {
            adapter = walletAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        if (coinsList.isEmpty() && !populated){
            populated = true
            populateFromCache()
        }

        walletAdapter?.differ?.submitList(coinsList)
        if (coinsList.isEmpty()){
            binding!!.rvTokens.visibility = View.GONE
            binding!!.tvEmptyWalletText.visibility = View.VISIBLE
        }else{
            binding!!.rvTokens.visibility = View.VISIBLE
            binding!!.tvEmptyWalletText.visibility = View.GONE
        }

        // update UI only, in 60 seconds db user data will update itself
        if (viewModel.userData != null){
            val user = viewModel.userData!!

            var userTotalBalanceWorth = 0.0
            val userTotalPercentageChange: Double
            var userTotalInvestment = 0.0

            for (coin in coinsList){
                userTotalBalanceWorth += coin.price * coin.totalTokenHeldAmount
                userTotalInvestment += coin.totalInvestmentAmount
            }
            // balance text
            if (userTotalBalanceWorth == 0.0) {
                binding!!.tvTotalBalance.text = "$0.00"
                if (sharedPrefTheme!!.getBoolean(Constants.SWITCH_STATE_KEY, true)){
                    binding!!.tvTotalBalance.setTextColor(requireContext().getColor(R.color.white))
                }else{
                    binding!!.tvTotalBalance.setTextColor(requireContext().getColor(R.color.black))
                }
            }else{
                if (user.userCurrentCurrency != Constants.USD){
                    val currencySymbol = user.userCurrentCurrency.substringBefore(" ").trim()

                    if (user.userCurrentCurrency != user.userPreviousCurrency){
                        // call the api request for currency conversion
                        // TODO limit the time for user to change currency or put daily limit to currency change amount
                        viewModel.getNewCurrencyValue()

                        // observe api result for conversion
                        viewModel.convertedBalance.observe(viewLifecycleOwner){
                                result->
                            if (result != null){
                                viewModel.updateUserdata(user.copy(userConvertedTotalBalanceWorth = formatToTwoDecimal(result.toDouble())))
                                binding!!.tvTotalBalance.text = currencySymbol+formatTotalBalanceValue(result.toDouble())
                            }else{
                                binding!!.tvTotalBalance.text = currencySymbol+formatTotalBalanceValue(user.userConvertedTotalBalanceWorth)
                            }
                        }
                    }else{
                        // show previous converted result because user didn't change it
                        binding!!.tvTotalBalance.text = currencySymbol+formatTotalBalanceValue(user.userConvertedTotalBalanceWorth)
                    }
                }else{
                    // show base balance which is in $, no need for conversion api request
                    binding!!.tvTotalBalance.text = "$"+formatTotalBalanceValue(userTotalBalanceWorth)
                }
            }

            // percentage text
            userTotalPercentageChange = calculateProfitLossPercentage(userTotalBalanceWorth, userTotalInvestment).replace("%", "").toDouble()
            if (userTotalPercentageChange == 0.0 || userTotalPercentageChange.isNaN()){
                binding!!.tvInvestmentPercentageChange.text = "0.0%"
                binding!!.tvInvestmentPercentageChange.setTextColor(requireContext().getColor(R.color.sort_color))
            }else{
                binding!!.tvInvestmentPercentageChange.text = "$userTotalPercentageChange%"
                if (userTotalPercentageChange.toString().contains("-")){
                    binding!!.tvInvestmentPercentageChange.setTextColor(requireContext().getColor(R.color.red_color_percentage))
                }else {
                    binding!!.tvInvestmentPercentageChange.setTextColor(requireContext().getColor(R.color.green_color_percentage))
                }
            }
        }else{
            binding!!.tvTotalBalance.text = "$0.00"
            binding!!.tvTotalBalance.setTextColor(requireContext().getColor(R.color.white))
            binding!!.tvInvestmentPercentageChange.text = "0.0%"
            binding!!.tvInvestmentPercentageChange.setTextColor(requireContext().getColor(R.color.sort_color))
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (mProgressDialog != null){
                cancelProgressDialog(mProgressDialog!!)
            }
        }, 250)

        walletAdapter?.setOnClickListener(object: MainFragmentAdapter.OnClickListener {
            override fun onClick(position: Int, coinModel: CoinModel) {
                val bundle = Bundle().apply {
                    putSerializable(Constants.PASSED_COIN, coinModel)
                    putSerializable(Constants.PASSED_USER, viewModel.userData)
                }
                findNavController().navigate(
                    R.id.action_mainFragment_to_tokenDetailsFragment,
                    bundle
                )
                navigation?.selectedItemId = R.id.invisibleItem
            }
        })
    }

    private suspend fun requestNewDataForWalletCoins(){

        // switch to Main Thread because I will be observing a live data
        withContext(Dispatchers.Main){
            delay(500)
            Log.i("MYTAG", "joined names list ${listOf(viewModel.slugNames)}")

            // make the request
            viewModel.getMultipleCoinsBySlug(listOf(viewModel.slugNames))
            if (viewModel.slugNames.isNotEmpty()){
                // observe the result
                viewModel.multipleCoinsListResponse.observe(viewLifecycleOwner){ resource ->

                    when (resource) {
                        is Resource.Success -> {
                            val response = resource.data
                            if (response != null){
                                // format the api response to get coins in it
                                viewModel.parseCoinAPIResponse(response)
                                Log.i("MYTAG", "api call success: ${viewModel.newTokensDataResponse}")

                                updateDB()

                            } else {
                                Log.e("MYTAG", "Response object is null")
                            }
                        }

                        is Resource.Error -> {
                            Log.e("MYTAG", "Error fetching data: ${resource.message}")
                            cancelProgressDialog(mProgressDialog!!)
                            // pass empty list to adapter
                            setupView(mutableListOf())
                        }
                        is Resource.Loading -> {
                            mProgressDialog!!.show()
                        }
                    }
                }
            }else{
                Log.e("MYTAG", "Empty wallet, no request has been made.")
                // Update lastApiRequestTime
                lastApiRequestTime = 60

                // Save lastApiRequestTime in shared preferences
                lifecycleScope.launch(Dispatchers.IO) {
                    val editor = sharedPrefRefresh?.edit()
                    editor?.putLong(Constants.LAST_API_REQUEST_TIME, lastApiRequestTime)
                    editor?.apply()
                }

                // pass empty list to adapter
                setupView(mutableListOf())
                // set empty user data
                updateUserDataDB()
            }
        }
    }

    private fun updateDB(){
        // operate on background thread for db function
        lifecycleScope.launch(Dispatchers.IO){
            viewModel.updateMultipleCoinDetails()
            delay(1000)
            updateUserDataDB()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUserDataDB() {
        val currentUser = viewModel.userData

        if (viewModel.temporaryTokenListToUseOnFragment.isNotEmpty() && currentUser != null) {
            val updatedUser = setUserValues(currentUser, viewModel.temporaryTokenListToUseOnFragment)
            viewModel.updateUserdata(updatedUser)
        } else {
            // dummy data for new users opening the app for the first time
            viewModel.insertUserData(
                UserData(
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    Constants.USD,
                    Constants.USD
                    ,
                    0
                )
            )
        }
        // switch to Main to update UI
        lifecycleScope.launch(Dispatchers.Main){
            setupView(viewModel.temporaryTokenListToUseOnFragment)
        }
    }

    private fun setupActionBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar?.title = "CoinWise"
        }
    }

    private fun setupMenu(){
        menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_settings, menu)
                // change icon depending on the theme
                menuItem = menu.findItem(R.id.actionSettings)
                if (sharedPrefTheme?.getBoolean(Constants.SWITCH_STATE_KEY, true) == true) {
                    menuItem?.setIcon(R.drawable.ic_settings_gray_24)
                } else {
                    menuItem?.setIcon(R.drawable.ic_settings_blue_24)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.actionSettings -> {
                        val bundle = Bundle().apply {
                            putSerializable(Constants.PASSED_USER, viewModel.userData)
                        }
                        findNavController().navigate(
                            R.id.action_mainFragment_to_settingsFragment,
                            bundle
                        )
                        navigation?.selectedItemId = R.id.invisibleItem
                    }
                }
                return false
            }
        }
        menuHost?.addMenuProvider(menuProvider!!, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.multipleCoinsListResponse.removeObservers(viewLifecycleOwner)
        viewModel.convertedBalance.removeObservers(viewLifecycleOwner)
        walletAdapter = null
        navigation = null
        menuItem?.setOnMenuItemClickListener(null)
        menuItem = null
        menuProvider?.let { provider ->
            menuHost?.removeMenuProvider(provider)
            menuProvider = null
        }
        menuHost = null
        toolbar?.setOnClickListener(null)
        toolbar = null
        appBarLayout = null
        actionBar = null
        sharedPrefTheme = null
        sharedPrefRefresh = null
        mProgressDialog?.dismiss()
        mProgressDialog = null
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
        constraintLayout?.removeAllViews()
        constraintLayout = null
        binding = null
    }
}
