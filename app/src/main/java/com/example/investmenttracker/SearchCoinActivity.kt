package com.example.investmenttracker

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.util.Resource
import com.example.investmenttracker.databinding.ActivitySearchCoinBinding
import com.example.investmenttracker.presentation.adapter.SearchCoinAdapter
import com.example.investmenttracker.presentation.view_model.SearchCoinViewModel
import com.example.investmenttracker.presentation.view_model.SearchCoinViewModelFactory
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

        binding.searchCoinBtn.setOnClickListener {
            viewModel.coinSearchInputText.value = binding.etSearchCoin.text.toString()
            getCoin()
        }
    }
    // test function to see if all works well
    private fun getCoin() {
        viewModel.getSearchedCoin(viewModel.coinSearchInputText.value.toString())

        viewModel.coinSearched.observe(this){ result ->
            cancelProgressDialog()

            when(result) {
                is Resource.Success -> {
                    val regex = Regex("[^A-Za-z0-9 ]") // to remove "" coming with api result, otherwise name is "Bitcoin" instead of just Bitcoin
                    result.data?.getAsJsonObject("data")?.asJsonObject?.asMap()?.forEach {
                        coin = CoinModel(
                            0,
                            cmcId = it.key.toInt(),
                            name = regex.replace(it.value.asJsonObject.get("name").toString(), ""),
                            symbol = it.value.asJsonObject.get("symbol").toString(),
                            price = it.value.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("price").toString().toDouble(),
                            marketCap = it.value.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("market_cap").toString().toDouble()
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