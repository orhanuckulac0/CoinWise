package com.example.investmenttracker.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
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

        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        walletAdapter = MainFragmentAdapter(requireContext())
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_add_coin, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.actionAddCoin -> {
                        findNavController().navigate(R.id.action_mainFragment_to_searchCoinFragment)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setupActionBar()

        // get current user and set it in viewModel to later use in Fragment
        viewModel.getUserData(1)

        lifecycleScope.launch(Dispatchers.IO){
            requestNewDataForWalletCoins()
        }
    }

    private fun setupView() {
        binding!!.rvTokens.apply {
            adapter = walletAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        Log.i("MYTAG", "Adapter walletTokensToUpdateDB $viewModel.walletTokensToUpdateDB")
        walletAdapter?.differ?.submitList(viewModel.walletTokensToUpdateDB)

        walletAdapter?.setOnClickListener(object: MainFragmentAdapter.OnClickListener {
            override fun onClick(position: Int, coinModel: CoinModel) {
                val bundle = Bundle().apply {
                    putSerializable(Constants.PASSED_COIN, coinModel)
                }
                findNavController().navigate(
                    R.id.action_mainFragment_to_tokenDetailsFragment,
                    bundle
                )
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
                            val responseCoinsList = viewModel.formatAPIResponse(response)

                            for (coin in responseCoinsList){
                                viewModel.newTokensDataResponse.add(coin)
                            }
                            Log.i("MYTAG", "api call success: $viewModel.newTokensDataResponse")

                            // switch back to background thread because I will be operating db functions
                            lifecycleScope.launch(Dispatchers.IO){
                                delay(500)
                                lockThreadAndUpdateCoinDetailsToDB()
                            }

                        } else {
                            Log.e("MYTAG", "Response object is null")
                        }
                    }

                    is Resource.Error -> {
                        Log.e("MYTAG", "Error fetching data: ${resource.message}")
                    }
                    is Resource.Loading -> {
                        // do nothing
                    }
                }
            }

        }

    }

    private fun lockThreadAndUpdateCoinDetailsToDB(){
        // operate on background thread for db function
        lifecycleScope.launch(Dispatchers.IO) {
            // lock the current thread because
            // walletTokensToUpdateDB may be reached from another functions,
            // while updating db and this causes
            // viewModel.getTokensFromWallet() to trigger itself again
            viewModel.databaseUpdateLock.lock()
            try {
                viewModel.isDatabaseUpdateInProgress = true
                viewModel.updateMultipleCoinDetails()
                updateUserDataAndUserDataDB()
            } finally {
                viewModel.isDatabaseUpdateInProgress = false
                viewModel.databaseUpdateLock.unlock()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUserDataAndUserDataDB() {
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
            binding!!.tvTotalBalance.text = "$${String.format("%,.2f", formatTotalBalanceValue(totalInvestmentWorth).toDouble())}"
            setupView()
        }
    }

    private fun setupActionBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding!!.toolbarMainActivity)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (actionBar != null){
            actionBar.title = "InvestmentTracker"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (binding != null){
            binding = null
        }
        if (walletAdapter != null){
            walletAdapter = null
        }

        viewModel.multipleCoinsListResponse.removeObservers(viewLifecycleOwner)
    }
}
