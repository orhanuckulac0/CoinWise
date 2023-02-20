package com.example.investmenttracker.presentation.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.util.Resource
import com.example.investmenttracker.databinding.ActivitySearchCoinBinding
import com.example.investmenttracker.presentation.adapter.SearchCoinAdapter
import com.example.investmenttracker.presentation.events.UiEvent
import com.example.investmenttracker.presentation.events.UiEventActions
import com.example.investmenttracker.presentation.view_model.SearchCoinViewModel
import com.example.investmenttracker.presentation.view_model.SearchCoinViewModelFactory
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchCoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchCoinBinding
    lateinit var viewModel: SearchCoinViewModel
    @Inject lateinit var factory: SearchCoinViewModelFactory
    private lateinit var adapter: SearchCoinAdapter

    private var mProgressDialog: Dialog? = null
    private var coin: CoinModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_coin)

        binding = ActivitySearchCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        viewModel = ViewModelProvider(this, factory)[SearchCoinViewModel::class.java]

        lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collect {event->
                when(event) {
                    is UiEvent.ShowErrorSnackbar -> {
                        Snackbar.make(binding.root, event.message, Snackbar.LENGTH_LONG).show()
                    }
                    is UiEvent.ShowCoinAddedSnackbar -> {
                        Snackbar.make(binding.root, event.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.searchCoinBtn.setOnClickListener {
            viewModel.coinSearchInputText.value = binding.etSearchCoin.text.toString()
            binding.rvCoinSearchResults.visibility = View.VISIBLE
            getCoinBySlug()
        }
    }
    // test function to see if all works well
    private fun getCoinBySlug() {
        viewModel.getSearchedCoinBySlug(viewModel.coinSearchInputText.value.toString())

        viewModel.coinSearched.observe(this){ result ->
            cancelProgressDialog()

            when(result) {
                is Resource.Success -> {
                    val regex = Regex("[^A-Za-z0-9 ]") // to remove "" coming with api result, otherwise name is "Bitcoin" instead of just Bitcoin
                    result.data?.getAsJsonObject("data")?.asJsonObject?.asMap()?.forEach {
                        coin = CoinModel(
                            id = it.key.toInt(),
                            cmcId = it.key.toInt(),
                            name = regex.replace(it.value.asJsonObject.get("name").toString(), ""),
                            slug = it.value.asJsonObject.get("slug").toString(),
                            symbol = it.value.asJsonObject.get("symbol").toString(),
                            price = it.value.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("price").toString().toDouble(),
                            marketCap = it.value.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("market_cap").toString().toDouble(),
                            percentChange1h = it.value.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_1h").toString().toDouble(),
                            percentChange24h = it.value.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_24h").toString().toDouble(),
                            percentChange7d = it.value.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_7d").toString().toDouble(),
                            percentChange30d = it.value.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_30d").toString().toDouble()
                        )
                    }
                    if (coin != null){
                        setupView(coin!!)
                    }

                    viewModel.coinSearchInputText.value = ""
                    result.data?.asMap()?.clear()

                    cancelProgressDialog()
                }

                is Resource.Error -> {
                    cancelProgressDialog()
                    if (result.data.toString() == "No Internet Connection Error"){
                        viewModel.triggerUiEvent(UiEventActions.NO_INTERNET_CONNECTION, UiEventActions.NO_INTERNET_CONNECTION)
                    }else {
                        viewModel.triggerUiEvent(UiEventActions.COIN_ADDED_FAILED, UiEventActions.COIN_ADDED_FAILED)
                    }
                }

                is Resource.Loading -> {
                    showProgressDialog()
                }
            }
        }
        cancelProgressDialog()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarSearchCoinActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.title = "Add Coin"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            binding.toolbarSearchCoinActivity.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupView(model: CoinModel) {
        adapter = SearchCoinAdapter(this, model)
        binding.rvCoinSearchResults.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCoinSearchResults.adapter = adapter
        binding.rvCoinSearchResults.visibility = View.VISIBLE


        adapter.setOnClickListener(object: SearchCoinAdapter.OnClickListener {
            override fun onClick(position: Int, coinModel: CoinModel) {
                // save coin to db
                viewModel.saveCoinToDB(coinModel)

                // refresh UI
                binding.etSearchCoin.setText("")
                binding.rvCoinSearchResults.visibility = View.INVISIBLE

                // trigger snackbar
                viewModel.triggerUiEvent("${coinModel.name} added successfully!", UiEventActions.COIN_ADDED)
            }
        })
    }

    private fun showProgressDialog(){
        mProgressDialog = Dialog(this)
        mProgressDialog?.setContentView(R.layout.progress_bar)
        mProgressDialog?.show()
    }

    private fun cancelProgressDialog(){
        mProgressDialog?.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_information, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionAddCoinInformation -> {
                // TODO create dialog for information on how to search coins properly
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}