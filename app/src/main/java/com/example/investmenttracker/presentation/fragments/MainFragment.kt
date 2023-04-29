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
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.databinding.FragmentMainBinding
import com.example.investmenttracker.domain.use_case.util.*
import com.example.investmenttracker.presentation.adapter.MainFragmentAdapter
import com.example.investmenttracker.presentation.events.UiEvent
import com.example.investmenttracker.presentation.events.UiEventActions
import com.example.investmenttracker.presentation.view_model.MainViewModel
import com.example.investmenttracker.presentation.view_model_factory.MainViewModelFactory
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
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

    private var sortGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

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

        //get db coins first at all times
        viewModel.getTokensFromWallet()
        // if user data is empty, add a dummy user
        viewModel.userData.observe(viewLifecycleOwner){
            if (it == null){
                Log.i("MYTAG", "EMPTY USER, creating a dummy data")
                viewModel.insertUserData(
                    UserData(
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        Constants.USD,
                        Constants.USD,
                        0,
                    )
                )
            }
        }

        // if internet is off, populate from cache and show snackbar
        if (!viewModel.isNetworkAvailable(requireContext())){
            populateFromCache()
            viewModel.triggerUiEvent(UiEventActions.NO_INTERNET_CONNECTION, UiEventActions.NO_INTERNET_CONNECTION)
        }else{
            // observe slug names for making api call
            viewModel.slugNames.observe(viewLifecycleOwner){resource->
                when (resource) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (resource.data.isNullOrEmpty()){
                            viewModel.getMultipleCoinsBySlug(emptyList())
                        }else{
                            viewModel.getMultipleCoinsBySlug(listOf(resource.data))
                        }
                        triggerAppLaunch()
                    }
                    is Resource.Error -> {
                        Log.i("MYTAG", resource.message.toString())
                        Log.i("MYTAG", "viewModel.getTokensFromWallet() error somehow.")
                        // maybe show snackbar
                    }
                }
            }
        }

        // to show snackbar when internet connection is off
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collect {event->
                    when(event) {
                        is UiEvent.ShowErrorSnackbar -> {
                            Snackbar.make(binding!!.root, event.message, Snackbar.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            }
        }

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

            sortGlobalLayoutListener = object: ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Remove the listener to avoid infinite loops
                    binding!!.rvTokens.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    // Scroll to the top of the list
                    binding!!.rvTokens.scrollToPosition(0)
                }
            }
            binding!!.rvTokens.viewTreeObserver.addOnGlobalLayoutListener(sortGlobalLayoutListener)
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

                // make request for currency api
                requestCurrencyData()

                // make request for coin api
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

        var updatedCoinsList: List<CoinModel>
        var user: UserData? = null

        try {
            binding!!.rvTokens.apply {
                adapter = walletAdapter
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
            }
        }catch (e: NullPointerException){
            e.printStackTrace()
        }

        if (coinsList.isEmpty() && !populated){
            populated = true
            populateFromCache()
        }

        if (coinsList.isEmpty()){
            binding!!.rvTokens.visibility = View.GONE
            binding!!.tvEmptyWalletText.visibility = View.VISIBLE
        }else{
            binding!!.rvTokens.visibility = View.VISIBLE
            binding!!.tvEmptyWalletText.visibility = View.GONE
        }

        // update UI only, in 60 seconds db user data will update itself
        if (viewModel.userData.value != null){
            user = viewModel.userData.value!!

            var userTotalBalanceWorth = 0.0
            val userTotalPercentageChange: Double
            var userTotalInvestment = 0.0

            for (coin in coinsList){
                userTotalBalanceWorth += coin.price * coin.totalTokenHeldAmount
                userTotalInvestment += coin.totalInvestmentAmount
            }

            // balance text
            val currencySymbol = user.userCurrentCurrency.substringBefore(" ").trim()
            val userCurrency = user.userCurrentCurrency.substringAfter(" ").trim()

            if (user.userCurrentCurrency == Constants.USD){
                binding!!.tvTotalBalance.text = currencySymbol+formatTotalBalanceValue(user.userTotalBalanceWorth)
                walletAdapter?.differ?.submitList(coinsList)

            }else{
                // get currency data from db
                viewModel.getCurrencyDataFromDB(userCurrency)

                // observe the user current currency data
                // update current wallet list with new currency data
                // set that new list to wallet
                try {
                    viewModel.currencyData.observe(viewLifecycleOwner){currency->

                        updatedCoinsList = coinsList.map { coin ->
                            coin.copy(
                                totalInvestmentAmount = formatToTwoDecimal(coin.totalInvestmentAmount*currency.currencyRate.toDouble()),
                                totalInvestmentWorth = formatToTwoDecimal(coin.totalInvestmentWorth * currency.currencyRate.toDouble()),
                                userCurrencySymbol = currencySymbol
                            )
                        }

                        if (userTotalBalanceWorth == 0.0){
                            binding!!.tvTotalBalance.text = currencySymbol+"0.00"
                            walletAdapter?.differ?.submitList(updatedCoinsList)
                        }else{
                            binding!!.tvTotalBalance.text = currencySymbol+formatTotalBalanceValue(
                                user.userTotalBalanceWorth*currency.currencyRate.toDouble()
                            )
                            walletAdapter?.differ?.submitList(updatedCoinsList)
                        }

                    }
                }catch (e: java.lang.NullPointerException){
                    e.printStackTrace()
                    walletAdapter?.differ?.submitList(coinsList)
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
            walletAdapter?.differ?.submitList(emptyList())
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
                    putSerializable(Constants.BASE_COIN, coinsList[position])
                    putSerializable(Constants.PASSED_USER, user)
                }
                findNavController().navigate(
                    R.id.action_mainFragment_to_tokenDetailsFragment,
                    bundle
                )
                navigation?.selectedItemId = R.id.invisibleItem
            }
        })
    }

    private fun requestCurrencyData(){
        viewModel.getNewCurrencyValuesAPIRequest()

        // observe api result for conversion
        lifecycleScope.launch(Dispatchers.Main){
            viewModel.currencyRequestResult.observe(viewLifecycleOwner){
                    resource->
                when (resource) {
                    is Resource.Success -> {
                        Log.e("MYTAG", "Success fetching currency data: ${resource.data}")
                    }
                    is Resource.Error -> {
                        Log.e("MYTAG", "Error fetching currency data: ${resource.message}")
                    }

                    is Resource.Loading -> {
                        mProgressDialog!!.show()
                        Log.e("MYTAG", "Currency data loading...")
                    }
                }
            }
        }
    }

    private fun requestNewDataForWalletCoins(){
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.multipleCoinsListResponse.observe(viewLifecycleOwner){ resource ->

                when (resource) {
                    is Resource.Success -> {
                        val response = resource.data
                        if (response != null){
                            // format the api response to get coins in it
                            viewModel.parseCoinAPIResponse(response)
                            Log.i("MYTAG", "coin api call success: ${viewModel.newTokensDataResponse}")

                            updateDB()

                        } else {
                            Log.e("MYTAG", "Response object is null")
                        }
                    }

                    is Resource.Error -> {
                        Log.e("MYTAG", "Error fetching coin data: ${resource.message}")
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
                    is Resource.Loading -> {
                        Log.e("MYTAG", "Coin data loading...")
                        // do nothing since progress dialog is showing
                    }
                }
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
        val updatedUser = setUserValues(viewModel.userData.value!!, viewModel.temporaryTokenListToUseOnFragment)
        viewModel.updateUserdata(updatedUser)

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
                            putSerializable(Constants.PASSED_USER, viewModel.userData.value!!)
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
        viewModel.userData.removeObservers(viewLifecycleOwner)
        viewModel.currencyData.removeObservers(viewLifecycleOwner)
        viewModel.multipleCoinsListResponse.removeObservers(viewLifecycleOwner)
        viewModel.currencyRequestResult.removeObservers(viewLifecycleOwner)
        viewModel.slugNames.removeObservers(viewLifecycleOwner)
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
        binding!!.tvSortTokensByValue.setOnClickListener(null)
        binding!!.rvTokens.viewTreeObserver.removeOnGlobalLayoutListener(sortGlobalLayoutListener)
        sortGlobalLayoutListener = null
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
