package com.example.investmenttracker.presentation.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.util.Constants
import com.example.investmenttracker.data.util.formatTokenTotalValue
import com.example.investmenttracker.data.util.setDecimalInput
import com.example.investmenttracker.databinding.ActivityTokenDetailsBinding
import com.example.investmenttracker.presentation.events.UiEvent
import com.example.investmenttracker.presentation.view_model.TokenDetailsViewModel
import com.example.investmenttracker.presentation.view_model.TokenDetailsViewModelFactory
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TokenDetailsActivity : AppCompatActivity() {
    private var binding: ActivityTokenDetailsBinding? = null
    private lateinit var viewModel: TokenDetailsViewModel
    @Inject
    lateinit var factory: TokenDetailsViewModelFactory
    private lateinit var currentCoin: CoinModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokenDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        viewModel = ViewModelProvider(this, factory)[TokenDetailsViewModel::class.java]

        currentCoin = intent.getSerializableExtra(Constants.PASSED_COIN) as CoinModel
        setupActionBar()
        setupView()

        lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collect {event->
                when(event) {
                    is UiEvent.ShowErrorSnackbar -> {
                        Snackbar.make(binding!!.root, event.message, Snackbar.LENGTH_LONG).show()
                    }else -> {}
                }
            }
        }

        // to prevent copy pasting
        binding!!.etTokenHeldAmount.setDecimalInput()
        binding!!.etTokenInvestmentAmount.setDecimalInput()

        binding!!.tokenDetailUpdateBtn.setOnClickListener {
            val totalTokenHeld = binding!!.etTokenHeldAmount.text.toString()
            val totalInvestment = binding!!.etTokenInvestmentAmount.text.toString()

            if (viewModel.checkEmptyInput(totalTokenHeld, totalInvestment)){
                val totalInvestmentWorth = formatTokenTotalValue(currentCoin.price, totalTokenHeld.toDouble()).replace(",", "").toDouble()
                viewModel.updateTokenDetails(currentCoin.cmcId, totalTokenHeld.toDouble(), totalInvestment.toDouble(), totalInvestmentWorth)
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun setupView(){
        val coinIcon = "https://s2.coinmarketcap.com/static/img/coins/64x64/"+"${currentCoin.cmcId}"+".png"

        binding!!.tvCoinName.text = currentCoin.name

        Glide
            .with(this)
            .load(coinIcon)
            .thumbnail(Glide.with(this).load(R.drawable.spinner))
            .centerCrop()
            .placeholder(R.drawable.coin_place_holder)
            .into(binding!!.ivTokenDetailImage)
    }


    private fun setupActionBar(){
        setSupportActionBar(binding!!.toolbarTokenDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.title = "Token Details"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            binding!!.toolbarTokenDetailsActivity.setNavigationOnClickListener {
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
                // TODO add dialog for this before deleting token
                viewModel.deleteTokenFromDB(currentCoin)
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null){
            binding = null
        }
    }
}