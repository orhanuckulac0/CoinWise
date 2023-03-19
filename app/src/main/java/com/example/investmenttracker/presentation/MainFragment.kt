package com.example.investmenttracker.presentation

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
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
import com.example.investmenttracker.presentation.view_model.MainViewModelFactory
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

    private var mProgressDialog: Dialog? = null
    private lateinit var sharedPref: SharedPreferences
    private var lastApiRequestTime: Long = 0
    private var populated = false
    private var sorted = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        constraintLayout = view?.findViewById(R.id.mainFragmentCL)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)

        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        walletAdapter = MainFragmentAdapter(requireContext())
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_settings, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.actionSettings -> {
                        // TODO create a settings fragment
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setupActionBar()
        mProgressDialog = showProgressDialog(requireContext())
        navigation = activity?.findViewById(R.id.bottom_navigation) as BottomNavigationView

        // get current user and set it in viewModel to later use in Fragment
        viewModel.getUserData(1)

        sharedPref = requireContext().getSharedPreferences(Constants.REFRESH_STATE, Context.MODE_PRIVATE)
        lastApiRequestTime = sharedPref.getLong(Constants.LAST_API_REQUEST_TIME, 0)

        triggerAppLaunch()

        binding!!.tvSortTokensByValue.setOnClickListener {
            if (!sorted){
                val sortedList = sortCoinsByAscending(walletAdapter!!.differ.currentList)
                walletAdapter!!.differ.submitList(sortedList)
                sorted = !sorted
            }else{
                val sortedList = sortCoinsByDescending(walletAdapter!!.differ.currentList)
                walletAdapter!!.differ.submitList(sortedList)
                sorted = !sorted
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

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
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
                withContext(Dispatchers.IO) {
                    val editor = sharedPref.edit()
                    editor.putLong(Constants.LAST_API_REQUEST_TIME, lastApiRequestTime)
                    editor.apply()
                }
            }
        }else{
            mProgressDialog!!.show()
            populateFromCache()
        }
    }

    private fun populateFromCache(){
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getTokensFromWallet()
            delay(250)

            withContext(Dispatchers.Main){
                setupView(viewModel.currentWalletCoins)
            }
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


        // update UI only, in 60 seconds db user data will update itself
        var totalInvestmentWorth = 0.0
        for (coin in coinsList){
            totalInvestmentWorth += coin.price*coin.totalTokenHeldAmount
        }

        if (viewModel.userData?.userTotalBalanceWorth == null) {
            binding!!.tvTotalBalance.text = ""
            binding!!.tvInvestmentPercentageChange.text = ""
        } else if (viewModel.userData?.userTotalProfit == null) {
            binding!!.tvTotalBalance.text = "$" + String.format("%,.2f", totalInvestmentWorth)
            binding!!.tvInvestmentPercentageChange.text = ""
        } else {
            binding!!.tvTotalBalance.text = "$" + String.format("%,.2f", totalInvestmentWorth)

            val percentage = calculateProfitLossPercentage(totalInvestmentWorth, viewModel.userData!!.userTotalInvestment)
            if (percentage == 0.0.toString()){
                binding!!.tvInvestmentPercentageChange.setTextColor(requireContext().getColor(R.color.white))
            }else if (percentage.contains("-")){
                binding!!.tvInvestmentPercentageChange.setTextColor(requireContext().getColor(R.color.redColorPercentage))
            }else {
                binding!!.tvInvestmentPercentageChange.setTextColor(requireContext().getColor(R.color.greenColorPercentage))
            }

            binding!!.tvInvestmentPercentageChange.text = percentage
        }


        cancelProgressDialog(mProgressDialog!!)

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
        viewModel.getTokensFromWallet()

        // switch to Main Thread because I will be observing a live data
        withContext(Dispatchers.Main){
            delay(500)
            Log.i("MYTAG", "joined names list ${listOf(viewModel.slugNames)}")

            // make the request
            viewModel.getMultipleCoinsBySlug(listOf(viewModel.slugNames))

            // observe the result
            viewModel.multipleCoinsListResponse.observe(viewLifecycleOwner){ resource ->

                when (resource) {
                    is Resource.Success -> {
                        val response = resource.data
                        if (response != null){
                            // format the api response to get coins in it
                            viewModel.parseAPIResponse(response)
                            Log.i("MYTAG", "api call success: ${viewModel.newTokensDataResponse}")

                            updateDB()

                        } else {
                            Log.e("MYTAG", "Response object is null")
                        }
                    }

                    is Resource.Error -> {
                        Log.e("MYTAG", "Error fetching data: ${resource.message}")
                        cancelProgressDialog(mProgressDialog!!)
                    }
                    is Resource.Loading -> {
                        mProgressDialog!!.show()
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
        var totalInvestment = 0.0
        val userTotalBalanceWorth: Double
        var totalInvestmentWorth = 0.0

        val currentUser = viewModel.userData

        if (viewModel.walletTokensToUpdateDB.isNotEmpty() && currentUser != null) {

            for (coin in viewModel.walletTokensToUpdateDB){
                totalInvestment += coin.totalInvestmentAmount
                totalInvestmentWorth += coin.price*coin.totalTokenHeldAmount
            }

            userTotalBalanceWorth = formatTotalBalanceValue(totalInvestmentWorth).toDouble()

            currentUser.userTotalLoss = 0.0
            currentUser.userTotalInvestment = totalInvestment
            currentUser.userTotalBalanceWorth = String.format("%.2f", userTotalBalanceWorth).toDouble()
            currentUser.userTotalCoinInvestedQuantity = viewModel.walletTokensToUpdateDB.size
            Log.i("MYTAG", "USER DATA FRAGMENT: ${currentUser.id}")
            Log.i("MYTAG", "total investment : $totalInvestment")
            Log.i("MYTAG", "totalInvestmentWorth: $totalInvestmentWorth")
            Log.i("MYTAG", "userTotalBalanceWorth: $userTotalBalanceWorth")

            viewModel.updateUserdata(currentUser)
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
                    0.0,
                    0.0,
                    0
                )
            )
        }
        // switch to Main to update UI
        lifecycleScope.launch(Dispatchers.Main){
            setupView(viewModel.walletTokensToUpdateDB)
        }
    }

    private fun setupActionBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding!!.toolbarMainFragment)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.title = "InvestmentTracker"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        walletAdapter = null
        mProgressDialog?.dismiss()
        mProgressDialog = null
        constraintLayout = null
        navigation = null
        viewModel.multipleCoinsListResponse.removeObservers(viewLifecycleOwner)
    }

}
