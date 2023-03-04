package com.example.investmenttracker.presentation

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.data.util.Constants
import com.example.investmenttracker.data.util.formatTotalBalanceValue
import com.example.investmenttracker.databinding.FragmentMainBinding
import com.example.investmenttracker.presentation.adapter.MainFragmentAdapter
import com.example.investmenttracker.presentation.view_model.CoinViewModel
import com.example.investmenttracker.presentation.view_model.CoinViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var binding: FragmentMainBinding? = null
    @Inject
    lateinit var factory: CoinViewModelFactory
    lateinit var viewModel: CoinViewModel
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

        viewModel = ViewModelProvider(this, factory)[CoinViewModel::class.java]
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
        setupView()
    }

    private fun setupView() {
        binding!!.rvTokens.apply {
            adapter = walletAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        viewModel.getTokensFromWallet().observe(viewLifecycleOwner) { coinsList ->
            walletAdapter?.differ?.submitList(coinsList)
        }

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


    private fun updateUserData() {
        viewModel.getTokensFromWallet().observe(viewLifecycleOwner) { coinsList ->
            if (!coinsList.isNullOrEmpty()) {
                var totalInvestment: Double = 0.0
                var userTotalBalanceWorth: Double = 0.0
                var totalInvestmentWorth: Double = 0.0

                for (coin in coinsList){
                    totalInvestment += coin.totalInvestmentAmount
                    totalInvestmentWorth += coin.price*coin.totalTokenHeldAmount
                }
                userTotalBalanceWorth = formatTotalBalanceValue(totalInvestmentWorth).toDouble()
                viewModel.getUserData(1).observe(viewLifecycleOwner) {
                    it.userTotalLoss = 0.0 // dummy for now
                    it.userTotalInvestment = totalInvestment
                    it.userTotalBalanceWorth = String.format("%.2f",userTotalBalanceWorth).toDouble()
                    it.userTotalCoinInvestedQuantity = coinsList.size

                    binding!!.tvTotalBalance.text = "$${String.format("%,.2f", userTotalBalanceWorth)}"

                    viewModel.userData = it
                    viewModel.updateUserdata()
                }
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
        }
    }

    private fun setupActionBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding!!.toolbarMainActivity)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (actionBar != null){
            actionBar.title = "InvestmentTracker"
        }
    }

    override fun onResume() {
        super.onResume()
        updateUserData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (binding != null){
            binding = null
        }
        if (walletAdapter != null){
            walletAdapter = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null){
            binding = null
        }
        if (walletAdapter != null){
            walletAdapter = null
        }
    }
}
