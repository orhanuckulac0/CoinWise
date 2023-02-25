package com.example.investmenttracker.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.util.Constants
import com.example.investmenttracker.databinding.ActivityTokenDetailsBinding
import com.example.investmenttracker.presentation.view_model.TokenDetailsViewModel
import com.example.investmenttracker.presentation.view_model.TokenDetailsViewModelFactory
import javax.inject.Inject

class TokenDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTokenDetailsBinding
    lateinit var viewModel: TokenDetailsViewModel
    @Inject lateinit var factory: TokenDetailsViewModelFactory
    private lateinit var currentCoin: CoinModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokenDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentCoin = intent.getSerializableExtra(Constants.PASSED_COIN) as CoinModel
        setupActionBar()
        setupView()
    }

    private fun setupView(){
        val coinIcon = "https://s2.coinmarketcap.com/static/img/coins/64x64/"+"${currentCoin.cmcId}"+".png"

        binding.tvCoinName.text = currentCoin.name

        Glide
            .with(this)
            .load(coinIcon)
            .thumbnail(Glide.with(this).load(R.drawable.spinner))
            .centerCrop()
            .placeholder(R.drawable.coin_place_holder)
            .into(binding.ivTokenDetailImage)
    }


    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarTokenDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.title = "Token Details"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            binding.toolbarTokenDetailsActivity.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_coin, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionDeleteCoin -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}