package com.example.investmenttracker.presentation.fragments

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.databinding.FragmentSearchCoinBinding
import com.example.investmenttracker.domain.use_case.util.*
import com.example.investmenttracker.presentation.adapter.SearchCoinAdapter
import com.example.investmenttracker.presentation.events.UiEvent
import com.example.investmenttracker.presentation.events.UiEventActions
import com.example.investmenttracker.presentation.view_model.SearchCoinViewModel
import com.example.investmenttracker.presentation.view_model_factory.SearchCoinViewModelFactory
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class SearchCoinFragment : Fragment() {

    private var binding: FragmentSearchCoinBinding? = null
    @Inject
    lateinit var factory: SearchCoinViewModelFactory
    private lateinit var viewModel: SearchCoinViewModel
    private var adapter: SearchCoinAdapter? = null
    private var menuProvider: MenuProvider? = null
    private var menuHost: MenuHost? = null
    private var menuItem: MenuItem? = null
    private var toolbar: Toolbar? = null
    private var appBarLayout: AppBarLayout? = null
    private var actionBar: ActionBar? = null
    private var userData: UserData? = null
    private var sharedPref: SharedPreferences? = null

    private var navigation: BottomNavigationView? = null
    private var onBackPressedCallback: OnBackPressedCallback? = null

    private var mProgressDialog: Dialog? = null
    private var mInformationDialog: Dialog? = null
    private var coin: CoinModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_search_coin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchCoinBinding.bind(view)
        toolbar = binding?.toolbarSearchCoinFragment
        appBarLayout = binding?.appBarLayoutSearchCoinFragment
        mProgressDialog = showProgressDialog(requireContext())
        mInformationDialog = showAddCoinInformationDialog(requireContext())
        viewModel = ViewModelProvider(this, factory)[SearchCoinViewModel::class.java]
        adapter = SearchCoinAdapter(requireContext())

        viewModel.userData.observe(viewLifecycleOwner){
            userData = it
        }
        sharedPref = requireContext().getSharedPreferences(Constants.THEME_PREF, Context.MODE_PRIVATE)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
        }

        binding!!.searchCoinBtnSlug.setOnClickListener {
            if(binding!!.etSearchCoin.text.toString() != ""){
                viewModel.coinSearchInputText.value = binding!!.etSearchCoin.text.toString().lowercase()
                binding!!.etSearchCoin.setText("")
                if (viewModel.isNetworkAvailable(requireContext())){
                    getCoinBySlug()
                }else{
                    viewModel.triggerUiEvent(UiEventActions.NO_INTERNET_CONNECTION,UiEventActions.NO_INTERNET_CONNECTION)
                }
            }else{
                viewModel.triggerUiEvent(UiEventActions.EMPTY_INPUT, UiEventActions.EMPTY_INPUT)
            }
        }
        binding!!.searchCoinBtnSymbol.setOnClickListener {
            if (binding!!.etSearchCoin.text.toString() != ""){
                viewModel.coinSearchInputText.value = binding!!.etSearchCoin.text.toString().uppercase()
                binding!!.etSearchCoin.setText("")
                if (viewModel.isNetworkAvailable(requireContext())){
                    getCoinBySymbol()
                }else{
                    viewModel.triggerUiEvent(UiEventActions.NO_INTERNET_CONNECTION,UiEventActions.NO_INTERNET_CONNECTION)
                }
            }else{
                viewModel.triggerUiEvent(UiEventActions.EMPTY_INPUT, UiEventActions.EMPTY_INPUT)
            }
        }

        navigation = activity?.findViewById(R.id.bottom_navigation) as BottomNavigationView

        onBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_searchCoinFragment_to_mainFragment)
                navigation?.selectedItemId = R.id.home
            }

        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback!!)

        setupActionBar()
        setupMenu()

    }

    private fun getCoinBySlug() {
        val searchInput = viewModel.coinSearchInputText.value.toString()
        viewModel.getSearchedCoinBySlug(searchInput)

        viewModel.coinSearchedBySlug.observe(viewLifecycleOwner){ result ->
            val responseList = arrayListOf<CoinModel>()

            when(result) {
                is Resource.Success -> {
                    val jsonObject = JSONObject(result.data.toString())
                    try {
                        val dataObject = jsonObject.getJSONObject("data")

                        viewModel.parseSlugResponse(dataObject)

                        responseList.add(viewModel.slugCoinParsed)

                        setupView(responseList)
                        result.data?.asMap()?.clear()
                    }catch (_: JSONException){ }

                    if (mProgressDialog != null){
                        cancelProgressDialog(mProgressDialog!!)
                    }
                }

                is Resource.Error -> {
                    if (mProgressDialog != null){
                        cancelProgressDialog(mProgressDialog!!)
                    }
                    if (result.data?.toString() == "No Internet Connection Error"){
                        viewModel.triggerUiEvent(UiEventActions.NO_INTERNET_CONNECTION, UiEventActions.NO_INTERNET_CONNECTION)
                    }else {
                        responseList.clear()
                        setupView(responseList)
                    }
                }

                is Resource.Loading -> {
                    mProgressDialog!!.show()
                }
            }
        }
        if (mProgressDialog != null){
            cancelProgressDialog(mProgressDialog!!)
        }
    }

    private fun getCoinBySymbol() {
        val searchInput = viewModel.coinSearchInputText.value.toString()
        viewModel.getSearchCoinBySymbol(searchInput)

        viewModel.coinSearchedBySymbol.observe(requireActivity()){ result ->
            val responseList = arrayListOf<CoinModel>()

            when(result) {
                is Resource.Success -> {
                    val response = result.data?.getAsJsonObject("data")?.get(searchInput)?.asJsonArray

                    viewModel.parseSymbolResponse(response)

                    setupView(viewModel.symbolCoinsListParsed)
                    if (mProgressDialog != null){
                        cancelProgressDialog(mProgressDialog!!)
                    }
                }

                is Resource.Error -> {
                    if (mProgressDialog != null){
                        cancelProgressDialog(mProgressDialog!!)
                    }
                    if (result.data?.toString() == "No Internet Connection Error"){
                        viewModel.triggerUiEvent(UiEventActions.NO_INTERNET_CONNECTION, UiEventActions.NO_INTERNET_CONNECTION)
                    }else {
                        responseList.clear()
                        setupView(responseList)
                    }
                }

                is Resource.Loading -> {
                    mProgressDialog!!.show()
                }
            }
        }
        if (mProgressDialog != null){
            cancelProgressDialog(mProgressDialog!!)
        }
    }

    private fun setupView(coinList: MutableList<CoinModel>) {

        if (coinList.isNotEmpty()){
            adapter?.differ?.submitList(null)
            adapter?.differ?.submitList(coinList)

            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            val rvCoinSearchResults = binding!!.rvCoinSearchResults

            binding!!.tvNoResults.visibility = View.GONE
            rvCoinSearchResults.visibility = View.VISIBLE
            rvCoinSearchResults.adapter = adapter
            rvCoinSearchResults.layoutManager = layoutManager
            rvCoinSearchResults.setHasFixedSize(true)

            adapter?.setOnClickListener(object: SearchCoinAdapter.OnClickListener {
                override fun onClick(position: Int, coinModel: CoinModel) {

                    var isAlreadyInWallet = false
                    viewModel.allCoinIDs.forEach { cmcId->
                        if (cmcId == coinModel.cmcId.toString()) {
                            isAlreadyInWallet = true
                            return@forEach
                        }
                    }

                    if (userData != null){
                        if (isAlreadyInWallet){
                            viewModel.triggerUiEvent("${coinModel.name} is already in wallet.", UiEventActions.ALREADY_IN_WALLET)
                        }else{
                            updateUserAndCoinData(userData!!, coinModel)

                            coinList.removeAt(position)
                            adapter?.notifyItemRemoved(position)

                            viewModel.triggerUiEvent("${coinModel.name} added successfully!", UiEventActions.COIN_ADDED)
                        }
                    }else{
                        val dummyUser = UserData(
                            1,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            Constants.USD,
                            Constants.USD,
                            0,
                            0L
                            )

                        updateUserAndCoinData(dummyUser, coinModel)

                        coinList.removeAt(position)
                        adapter?.notifyItemRemoved(position)

                        viewModel.triggerUiEvent("${coinModel.name} added successfully!", UiEventActions.COIN_ADDED)
                    }

                    binding!!.etSearchCoin.setText("")
                    if (coinList.isEmpty()){
                        binding!!.rvCoinSearchResults.visibility = View.INVISIBLE
                    }
                }
            })

        }else{
            binding!!.tvNoResults.visibility = View.VISIBLE
            binding!!.rvCoinSearchResults.visibility = View.GONE
        }
    }

    private fun updateUserAndCoinData(userData: UserData, coinModel: CoinModel){
        val updatedUser = userData.copy(
            userTotalCoinInvestedQuantity = userData.userTotalCoinInvestedQuantity + 1)

        val coinData = CoinModel(
            id = coinModel.cmcId,
            cmcId = coinModel.cmcId,
            name = coinModel.name,
            slug = coinModel.slug,
            symbol = coinModel.symbol,
            price = formatPrice(coinModel.price).toDouble(),
            marketCap = formatPrice(coinModel.marketCap).toDouble(),
            percentChange24h = coinModel.percentChange24h,
            0.0,
            0.0,
            0.0,
            Constants.USD.substringBefore(" ").trim()
        )

        viewModel.updateUserDataDB(updatedUser)
        viewModel.saveCoinToDB(coinData)
    }

    private fun setupMenu(){
        menuHost = requireActivity()
        menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_information, menu)

                menuItem = menu.findItem(R.id.actionAddCoinInformation)

                val theme = sharedPref?.getBoolean(Constants.SWITCH_STATE_KEY, true)
                if (theme!!) {
                    menuItem?.setIcon(R.drawable.ic_info_white_24)
                } else {
                    menuItem?.setIcon(R.drawable.ic_info_blue_24)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.actionAddCoinInformation -> {
                        mInformationDialog!!.show()
                        return true
                    }
                }
                return true
            }

        }
        menuHost?.addMenuProvider(menuProvider!!, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupActionBar() {
        val theme = sharedPref?.getBoolean(Constants.SWITCH_STATE_KEY, true)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (actionBar != null){
            actionBar?.title = "Add Coin"
            actionBar?.setDisplayHomeAsUpEnabled(true)
            if (theme!!){
                actionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            }else{
                actionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_black)
            }
            toolbar?.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_searchCoinFragment_to_mainFragment)
                navigation?.selectedItemId = R.id.home
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.coinSearchedBySymbol.removeObservers(viewLifecycleOwner)
        viewModel.coinSearchedBySlug.removeObservers(viewLifecycleOwner)
        viewModel.userData.removeObservers(viewLifecycleOwner)
        binding = null
        adapter = null
        menuItem = null
        menuProvider?.let { provider ->
            menuHost?.removeMenuProvider(provider)
            menuProvider = null
        }
        menuHost = null
        actionBar = null
        appBarLayout = null
        toolbar = null
        navigation = null
        sharedPref = null
        coin = null
        mProgressDialog = null
        mInformationDialog = null
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
    }
}
