package com.example.investmenttracker.presentation.activities

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.util.Resource
import com.example.investmenttracker.data.util.formatPrice
import com.example.investmenttracker.databinding.ActivitySearchCoinBinding
import com.example.investmenttracker.presentation.adapter.SearchCoinAdapter
import com.example.investmenttracker.presentation.events.UiEvent
import com.example.investmenttracker.presentation.events.UiEventActions
import com.example.investmenttracker.presentation.view_model.SearchCoinViewModel
import com.example.investmenttracker.presentation.view_model.SearchCoinViewModelFactory
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject


@AndroidEntryPoint
class SearchCoinActivity : AppCompatActivity() {
    private var binding: ActivitySearchCoinBinding? = null
    lateinit var viewModel: SearchCoinViewModel
    @Inject lateinit var factory: SearchCoinViewModelFactory
    private lateinit var adapter: SearchCoinAdapter

    private var dividerCreated: Boolean = false

    private var mProgressDialog: Dialog? = null
    private var coin: CoinModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_coin)

        binding = ActivitySearchCoinBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()

        viewModel = ViewModelProvider(this, factory)[SearchCoinViewModel::class.java]

        lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collect {event->
                when(event) {
                    is UiEvent.ShowCoinAddedSnackbar -> {
                        Snackbar.make(binding!!.root, event.message, Snackbar.LENGTH_SHORT).show()
                    }
                    is UiEvent.ShowErrorSnackbar -> {
                        Snackbar.make(binding!!.root, event.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding!!.searchCoinBtnSlug.setOnClickListener {
            viewModel.coinSearchInputText.value = binding!!.etSearchCoin.text.toString().lowercase()
            getCoinBySlug()
        }
        binding!!.searchCoinBtnSymbol.setOnClickListener {
            viewModel.coinSearchInputText.value = binding!!.etSearchCoin.text.toString().uppercase()
            getCoinBySymbol()
        }

    }
    // search coins by slug, bitcoin - ethereum
    // returns jsonObject, only 1 coin
    private fun getCoinBySlug() {
        val searchInput = viewModel.coinSearchInputText.value.toString()
        viewModel.getSearchedCoinBySlug(searchInput)

        viewModel.coinSearchedBySlug.observe(this){ result ->
            cancelProgressDialog()
            val responseList = arrayListOf<CoinModel>()

            when(result) {
                is Resource.Success -> {
                    // to remove "" coming with api result, otherwise name is "Bitcoin" instead of just Bitcoin
                    val regex = Regex("[^A-Za-z0-9 ]")

                    val jsonObject = JSONObject(result.data.toString())
                    val dataObject = jsonObject.getJSONObject("data")
                    val firstKey = dataObject.keys().next()  // use keys().next() because each firstKey is unique
                    val resultCoinObject = dataObject.getJSONObject(firstKey)

                    // Extract the "USD" object from the "quote" object
                    val quoteObject = resultCoinObject.getJSONObject("quote")
                    val usdObject = quoteObject.getJSONObject("USD")

                    coin = CoinModel(
                        id = resultCoinObject.getInt("id"),
                        cmcId = resultCoinObject.getInt("id"),
                        name = regex.replace(resultCoinObject.get("name").toString(), ""),
                        slug = resultCoinObject.get("slug").toString(),
                        symbol = resultCoinObject.get("symbol").toString(),
                        price = usdObject.get("price").toString().toDouble(),
                        marketCap = usdObject.get("market_cap").toString().toDouble(),
                        percentChange1h = usdObject.get("percent_change_1h").toString().toDouble(),
                        percentChange24h = usdObject.get("percent_change_24h").toString().toDouble(),
                        percentChange7d = usdObject.get("percent_change_7d").toString().toDouble(),
                        percentChange30d = usdObject.get("percent_change_30d").toString().toDouble(),
                        totalTokenHeldAmount = 0.toDouble(),
                        totalInvestmentAmount = 0.toDouble(),
                        totalInvestmentWorth = 0.toDouble()
                    )

                    responseList.add(coin!!)

                    setupView(responseList)
                    result.data?.asMap()?.clear()

                    cancelProgressDialog()
                }

                is Resource.Error -> {
                    cancelProgressDialog()
                    if (result.data.toString() == "No Internet Connection Error"){
                        viewModel.triggerUiEvent(UiEventActions.NO_INTERNET_CONNECTION, UiEventActions.NO_INTERNET_CONNECTION)
                    }else {
                        responseList.clear() // clear response here
                        setupView(responseList)
                    }
                }

                is Resource.Loading -> {
                    showProgressDialog()
                }
            }
        }
        cancelProgressDialog()
    }

    // search coins by symbol, BTC - ETH
    // returns a jsonArray because symbols are not unique
    private fun getCoinBySymbol() {
        val searchInput = viewModel.coinSearchInputText.value.toString()
        viewModel.getSearchCoinBySymbol(searchInput)

        viewModel.coinSearchedBySymbol.observe(this){ result ->
            cancelProgressDialog()
            val responseList = arrayListOf<CoinModel>()

            when(result) {
                is Resource.Success -> {
                    // to remove "" coming with api result, otherwise name is "Bitcoin" instead of just Bitcoin
                    val regex = Regex("[^A-Za-z0-9 ]")

                    val response = result.data?.getAsJsonObject("data")?.get(searchInput)?.asJsonArray
                    println(response)

                    if (response != null){
                        for (c in response.asJsonArray){
                            coin = CoinModel(
                                id = c.asJsonObject.get("id").toString().toInt(),
                                cmcId = c.asJsonObject.get("id").toString().toInt(),
                                name = regex.replace(c.asJsonObject.get("name").toString(), ""),
                                slug = c.asJsonObject.get("slug").toString(),
                                symbol = c.asJsonObject.get("symbol").toString(),
                                price = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("price").toString().toDouble(),
                                marketCap = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("market_cap").toString().toDouble(),
                                percentChange1h = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_1h").toString().toDouble(),
                                percentChange24h = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_24h").toString().toDouble(),
                                percentChange7d = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_7d").toString().toDouble(),
                                percentChange30d = c.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("percent_change_30d").toString().toDouble(),
                                totalTokenHeldAmount = 0.toDouble(),
                                totalInvestmentAmount = 0.toDouble(),
                                totalInvestmentWorth =0.toDouble()
                            )
                            responseList.add(coin!!)
                        }
                    }

                    setupView(responseList)
                    cancelProgressDialog()
                }

                is Resource.Error -> {
                    cancelProgressDialog()
                    if (result.data.toString() == "No Internet Connection Error"){
                        viewModel.triggerUiEvent(UiEventActions.NO_INTERNET_CONNECTION, UiEventActions.NO_INTERNET_CONNECTION)
                    }else {
                        responseList.clear() // clear response here
                        setupView(responseList)
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
        setSupportActionBar(binding?.toolbarSearchCoinActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.title = "Add Coin"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            binding!!.toolbarSearchCoinActivity.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupView(coinList: ArrayList<CoinModel>) {
        if (coinList.isNotEmpty()){
            adapter = SearchCoinAdapter(this)
            adapter.differ.submitList(coinList)

            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val rvCoinSearchResults = binding!!.rvCoinSearchResults

            binding!!.tvNoResults.visibility = View.GONE
            rvCoinSearchResults.visibility = View.VISIBLE
            rvCoinSearchResults.layoutManager = layoutManager
            rvCoinSearchResults.adapter = adapter
            rvCoinSearchResults.setHasFixedSize(true)

            adapter.setOnClickListener(object: SearchCoinAdapter.OnClickListener {
                override fun onClick(position: Int, coinModel: CoinModel) {
                    // save coin to db
                    viewModel.saveCoinToDB(
                        CoinModel(
                            id = coinModel.cmcId,
                            cmcId = coinModel.cmcId,
                            name = coinModel.name,
                            slug = coinModel.slug,
                            symbol = coinModel.symbol,
                            price = formatPrice(coinModel.price).toDouble(),
                            marketCap = formatPrice(coinModel.marketCap).toDouble(),
                            percentChange1h = coinModel.percentChange1h,
                            percentChange24h = coinModel.percentChange24h,
                            percentChange7d = coinModel.percentChange7d,
                            percentChange30d = coinModel.percentChange30d,
                            0.0,
                            0.0,
                            0.0
                        )
                    )
                    coinList.removeAt(position)
                    adapter.notifyDataSetChanged()

                    // refresh UI
                    binding!!.etSearchCoin.setText("")
                    if (coinList.isEmpty()){
                        binding!!.rvCoinSearchResults.visibility = View.INVISIBLE
                    }

                    // trigger snackbar
                    viewModel.triggerUiEvent("${coinModel.name} added successfully!", UiEventActions.COIN_ADDED)
                }
            })
            if (!dividerCreated && coinList.size > 1){
                val decorator = DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
                decorator.setDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.line_divider)!!)
                rvCoinSearchResults.addItemDecoration(decorator)
                dividerCreated = true
            }

        }else{
            binding!!.tvNoResults.visibility = View.VISIBLE
            binding!!.rvCoinSearchResults.visibility = View.GONE
        }
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

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null){
            binding = null
        }
    }
}