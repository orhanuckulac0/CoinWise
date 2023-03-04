package com.example.investmenttracker.presentation.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.data.util.*
import com.example.investmenttracker.databinding.ActivityMainBinding
import com.example.investmenttracker.presentation.adapter.MainActivityAdapter
import com.example.investmenttracker.presentation.view_model.CoinViewModel
import com.example.investmenttracker.presentation.view_model.CoinViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    @Inject lateinit var factory: CoinViewModelFactory
    lateinit var viewModel: CoinViewModel
    private lateinit var walletAdapter: MainActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel = ViewModelProvider(this, factory)[CoinViewModel::class.java]
        walletAdapter = MainActivityAdapter(this)

        setupActionBar()
        setupView()
    }


    private fun setupView(){
        binding!!.rvTokens.apply {
            adapter = walletAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        }

        viewModel.getTokensFromWallet().observe(this){
            walletAdapter.differ.submitList(it)
        }
        walletAdapter.setOnClickListener(object: MainActivityAdapter.OnClickListener {
            override fun onClick(position: Int, coinModel: CoinModel) {
                val intent = Intent(this@MainActivity, TokenDetailsActivity::class.java)
                intent.putExtra(Constants.PASSED_COIN, coinModel)
                startActivity(intent)
            }
        })
    }

    private fun updateUserData(){
        viewModel.getTokensFromWallet().observe(this){coinsList->
            if (!coinsList.isNullOrEmpty()) {
                var totalInvestment: Double = 0.0
                var userTotalBalanceWorth: Double = 0.0
                var totalInvestmentWorth: Double = 0.0

                for (coin in coinsList){
                    totalInvestment += coin.totalInvestmentAmount
                    totalInvestmentWorth += coin.price*coin.totalTokenHeldAmount
                }
                userTotalBalanceWorth = formatTotalBalanceValue(totalInvestmentWorth).toDouble()
                viewModel.getUserData(1).observe(this) {
                    it.userTotalLoss = 0.0 // dummy for now
                    it.userTotalInvestment = totalInvestment
                    it.userTotalBalanceWorth = String.format("%.2f",userTotalBalanceWorth).toDouble()
                    it.userTotalCoinInvestedQuantity = coinsList.size

                    binding!!.tvTotalBalance.text = "$${String.format("%,.2f", userTotalBalanceWorth)}"

                    viewModel.userData = it
                    viewModel.updateUserdata()
                }
            }else{
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

    private fun setupActionBar(){
        setSupportActionBar(binding!!.toolbarMainActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.title = "InvestmentTracker"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_coin, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionAddCoin -> {
                startActivity(Intent(this, SearchCoinActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        updateUserData()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null){
            binding = null
        }
    }
}