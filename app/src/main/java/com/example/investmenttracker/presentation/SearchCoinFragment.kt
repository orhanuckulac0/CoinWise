package com.example.investmenttracker.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.domain.use_case.util.Resource
import com.example.investmenttracker.domain.use_case.util.formatPrice
import com.example.investmenttracker.databinding.FragmentSearchCoinBinding
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
class SearchCoinFragment : Fragment() {

    private var binding: FragmentSearchCoinBinding? = null
    @Inject
    lateinit var factory: SearchCoinViewModelFactory
    private lateinit var viewModel: SearchCoinViewModel
    private var adapter: SearchCoinAdapter? = null

    private var dividerCreated: Boolean = false

    private var mProgressDialog: Dialog? = null
    private var coin: CoinModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_coin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchCoinBinding.bind(view)

        setupActionBar()

        viewModel = ViewModelProvider(this, factory)[SearchCoinViewModel::class.java]
        adapter = SearchCoinAdapter(requireContext())
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_information, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.actionAddCoinInformation -> {
                        return false
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

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

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_searchCoinFragment_to_mainFragment)
            }
        })
    }

    // search coins by slug, bitcoin - ethereum
    // returns jsonObject, only 1 coin
    private fun getCoinBySlug() {
        val searchInput = viewModel.coinSearchInputText.value.toString()
        viewModel.getSearchedCoinBySlug(searchInput)

        viewModel.coinSearchedBySlug.observe(viewLifecycleOwner){ result ->
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

        viewModel.coinSearchedBySymbol.observe(requireActivity()){ result ->
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

    private fun setupView(coinList: ArrayList<CoinModel>) {
        if (coinList.isNotEmpty()){
            adapter = SearchCoinAdapter(requireContext())
            adapter?.differ?.submitList(coinList)

            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            val rvCoinSearchResults = binding!!.rvCoinSearchResults

            binding!!.tvNoResults.visibility = View.GONE
            rvCoinSearchResults.visibility = View.VISIBLE
            rvCoinSearchResults.layoutManager = layoutManager
            rvCoinSearchResults.adapter = adapter
            rvCoinSearchResults.setHasFixedSize(true)

            adapter?.setOnClickListener(object: SearchCoinAdapter.OnClickListener {
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
                    adapter?.notifyDataSetChanged()

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
                val decorator = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
                decorator.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.line_divider)!!)
                rvCoinSearchResults.addItemDecoration(decorator)
                dividerCreated = true
            }

        }else{
            binding!!.tvNoResults.visibility = View.VISIBLE
            binding!!.rvCoinSearchResults.visibility = View.GONE
        }
    }

    private fun setupActionBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding!!.toolbarSearchCoinActivity)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (actionBar != null){
            actionBar.title = "Add Coin"
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            binding!!.toolbarSearchCoinActivity.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_searchCoinFragment_to_mainFragment)
            }
        }
    }

    private fun showProgressDialog(){
        mProgressDialog = Dialog(requireContext())
        mProgressDialog?.setContentView(R.layout.progress_bar)
        mProgressDialog?.show()
    }

    private fun cancelProgressDialog(){
        mProgressDialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (binding != null){
            binding = null
        }
        if (adapter != null){
            adapter = null
        }
        if (coin != null){
            coin = null
        }
        if (mProgressDialog != null){
            mProgressDialog = null
        }
    }
}
