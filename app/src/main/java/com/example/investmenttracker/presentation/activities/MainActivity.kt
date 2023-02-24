package com.example.investmenttracker.presentation.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investmenttracker.R
import com.example.investmenttracker.databinding.ActivityMainBinding
import com.example.investmenttracker.presentation.adapter.WalletAdapter
import com.example.investmenttracker.presentation.view_model.CoinViewModel
import com.example.investmenttracker.presentation.view_model.CoinViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @Inject lateinit var factory: CoinViewModelFactory
    lateinit var viewModel: CoinViewModel
    private lateinit var walletAdapter: WalletAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[CoinViewModel::class.java]
        walletAdapter = WalletAdapter(this)

        setupActionBar()
        initRecyclerView()
        setupView()
    }

    private fun initRecyclerView(){
        binding.rvTokens.apply {
            adapter = walletAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupView(){
        viewModel.getTokensFromWallet().observe(this){
            walletAdapter.differ.submitList(it)
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarMainActivity)
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
}