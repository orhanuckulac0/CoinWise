package com.example.investmenttracker.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.databinding.FragmentTokenDetailsBinding
import com.example.investmenttracker.domain.use_case.util.*
import com.example.investmenttracker.presentation.events.UiEvent
import com.example.investmenttracker.presentation.view_model.TokenDetailsViewModel
import com.example.investmenttracker.presentation.view_model_factory.TokenDetailsViewModelFactory
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TokenDetailsFragment : Fragment() {

    private var binding: FragmentTokenDetailsBinding? = null
    @Inject
    lateinit var factory: TokenDetailsViewModelFactory
    lateinit var viewModel: TokenDetailsViewModel
    private var menuHost: MenuHost? = null
    private var menuItem: MenuItem? = null
    private var menuProvider: MenuProvider? = null
    private var toolbar: Toolbar? = null
    private var appBarLayout: AppBarLayout? = null
    private var actionBar: ActionBar? = null
    private var sharedPrefTheme: SharedPreferences? = null
    private var currentCoin: CoinModel? = null
    private var usdCoinData: CoinModel? = null
    private var userData: UserData? = null
    private var navigation: BottomNavigationView? = null
    private var theme: Boolean = false
    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_token_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTokenDetailsBinding.bind(view)
        toolbar = binding?.toolbarTokenDetailsFragment
        appBarLayout = binding?.appBarLayoutTokenDetailsFragment
        viewModel = ViewModelProvider(this, factory)[TokenDetailsViewModel::class.java]

        arguments?.let {
            currentCoin = it.customGetSerializable(Constants.PASSED_COIN)
            usdCoinData = it.customGetSerializable(Constants.BASE_COIN)
            userData = it.customGetSerializable(Constants.PASSED_USER)
        }

        sharedPrefTheme = requireContext().getSharedPreferences(Constants.THEME_PREF,
            Context.MODE_PRIVATE
        )
        theme = sharedPrefTheme!!.getBoolean(Constants.SWITCH_STATE_KEY, true)
        setupActionBar()
        setupView()
        setupMenu()

        navigation = activity?.findViewById(R.id.bottom_navigation) as BottomNavigationView
        navigation?.selectedItemId = 0 // set the selected item to be null

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collect { event ->
                    when (event) {
                        is UiEvent.ShowErrorSnackbar -> {
                            Snackbar.make(binding!!.root, event.message, Snackbar.LENGTH_LONG)
                                .show()
                        }
                        else -> {}
                    }
                }
            }
        }

        // to prevent copy pasting
        binding!!.etTokenHeldAmount.setDecimalInput()
        binding!!.etTokenInvestmentAmount.setDecimalInput()

        binding!!.tokenDetailUpdateBtn.setOnClickListener {
            val totalTokenHeld = binding!!.etTokenHeldAmount.text.toString()
            val totalInvestment = binding!!.etTokenInvestmentAmount.text.toString()
            updateUserAndCoinDetails(totalTokenHeld, totalInvestment)
        }

        onBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_tokenDetailsFragment_to_mainFragment)
                navigation?.selectedItemId = R.id.home
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback!!)
    }

    private fun updateUserAndCoinDetails(totalTokenHeld: String, totalInvestment: String) {
        if (viewModel.checkEmptyInput(totalTokenHeld, totalInvestment)){
            val totalInvestmentWorth = formatTokenTotalValue(usdCoinData!!.price, totalTokenHeld.toDouble()).replace(",", "").toDouble()

            if (totalTokenHeld.toDouble() == usdCoinData!!.totalTokenHeldAmount &&
                totalInvestment.toDouble() == usdCoinData!!.totalInvestmentAmount){
                Log.i("MYTAG", "SAME VALUES ENTERED, PASS")

            }else{
                val overallTotalInvestment: Double = (userData!!.userTotalInvestment - usdCoinData!!.totalInvestmentAmount) + totalInvestment.toDouble()
                val overallTotalInvestmentWorth: Double = (userData!!.userTotalBalanceWorth - usdCoinData!!.totalInvestmentWorth) + totalInvestmentWorth
                // update userData first
                viewModel.updateUserDB(userData!!.copy(
                    userTotalInvestment = overallTotalInvestment,
                    userTotalBalanceWorth = overallTotalInvestmentWorth
                ))

                // update coins
                viewModel.updateTokenDetails(usdCoinData!!.cmcId, totalTokenHeld.toDouble(), totalInvestment.toDouble(), totalInvestmentWorth)
            }

            findNavController().navigate(
                R.id.action_tokenDetailsFragment_to_mainFragment
            )
            navigation?.selectedItemId = R.id.home
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupView(){
        val coinIcon = Constants.COIN_IMAGE_LINK+"${currentCoin?.cmcId}"+".png"
        val userCurrencySymbol = userData?.userCurrentCurrency!!.substringBefore(" ").trim()

        binding!!.tvCoinName.text = currentCoin?.name + " / " + formatCoinNameText(currentCoin!!.symbol)
        binding!!.tvTotalHeldAmount.text = formatTokenHeldAmount(currentCoin!!.totalTokenHeldAmount) + " " + formatCoinNameText(currentCoin!!.symbol)
        binding!!.tvTotalInvestment.text = userCurrencySymbol+ formatToTwoDecimalWithComma(currentCoin?.totalInvestmentAmount!!)
        binding!!.tvCurrentInvestmentValue.text =  userCurrencySymbol+formatToTwoDecimalWithComma(currentCoin?.totalInvestmentWorth!!)
        binding!!.tvProfitLossAmount.text = formatTotalProfitAmountUI(userCurrencySymbol,currentCoin!!)

        if (currentCoin?.totalInvestmentAmount == 0.0){
            binding!!.tvProfitLossAmount.setTextColor(requireContext().getColor(R.color.white))
            binding!!.tvProfitLossPercentage.setTextColor(requireContext().getColor(R.color.white))
            binding!!.tvProfitLossPercentage.text = "0.0%"
        }else{
            val profitLoss = (currentCoin!!.totalInvestmentWorth-currentCoin!!.totalInvestmentAmount).toString()
            val percentage = calculateProfitLossPercentage(currentCoin!!.totalInvestmentWorth, currentCoin!!.totalInvestmentAmount)
            if (profitLoss.contains("-")){
                binding!!.tvProfitLossAmount.setTextColor(requireContext().getColor(R.color.red_color_percentage))
                binding!!.tvProfitLossPercentage.setTextColor(requireContext().getColor(R.color.red_color_percentage))
            }else{
                binding!!.tvProfitLossAmount.setTextColor(requireContext().getColor(R.color.green_color_percentage))
                binding!!.tvProfitLossPercentage.setTextColor(requireContext().getColor(R.color.green_color_percentage))
            }
            binding!!.tvProfitLossPercentage.text = percentage
        }


        Glide
            .with(this)
            .load(coinIcon)
            .thumbnail(Glide.with(this).load(R.drawable.spinner))
            .centerCrop()
            .placeholder(R.drawable.coin_place_holder)
            .into(binding!!.ivTokenDetailImage)
    }

    private fun setupMenu(){
        menuHost = requireActivity()
        menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_delete_coin, menu)
                // change icon depending on the theme
                menuItem = menu.findItem(R.id.actionDeleteCoin)
                if (theme) {
                    menuItem?.setIcon(R.drawable.ic_delete_24)
                } else {
                    menuItem?.setIcon(R.drawable.ic_delete_blue_24)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.actionDeleteCoin -> {
                        val user = viewModel.userData!!
                        // if that coin is the last in db, set all values to default **necessary
                        if (user.userTotalCoinInvestedQuantity == 1){
                            viewModel.updateUserDB(user.copy(
                                userTotalInvestment = 0.0,
                                userTotalBalanceWorth = 0.0,
                                userTotalProfitAndLoss = 0.0,
                                userCurrentCurrency = Constants.USD,
                                userPreviousCurrency = Constants.USD,
                                userTotalProfitAndLossPercentage = 0.0,
                                userTotalCoinInvestedQuantity = 0
                            ))
                        }else{
                            viewModel.updateUserDB(user.copy(
                                userTotalInvestment = formatToTwoDecimal(user.userTotalInvestment - usdCoinData!!.totalInvestmentAmount),
                                userTotalBalanceWorth = formatToTwoDecimal(user.userTotalBalanceWorth - usdCoinData!!.totalInvestmentWorth),
                                userTotalProfitAndLoss = formatToTwoDecimal(user.userTotalBalanceWorth - user.userTotalInvestment),
                                userTotalProfitAndLossPercentage = calculateProfitLossPercentage(user.userTotalBalanceWorth, user.userTotalInvestment).replace("%", "").toDouble(),
                                userTotalCoinInvestedQuantity = user.userTotalCoinInvestedQuantity - 1
                            ))
                        }
                        viewModel.deleteTokenFromDB(currentCoin!!)

                        findNavController().navigate(
                            R.id.action_tokenDetailsFragment_to_mainFragment
                        )
                        navigation?.selectedItemId = R.id.home
                        return true
                    }
                }
                return true
            }
        }
        menuHost!!.addMenuProvider(menuProvider!!, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupActionBar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (actionBar != null){
            actionBar?.title = "Token Details"
            actionBar?.setDisplayHomeAsUpEnabled(true)
            if (theme){
                actionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            }else{
                actionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_black)
            }
            toolbar?.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_tokenDetailsFragment_to_mainFragment)
                navigation?.selectedItemId = R.id.home
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toolbar = null
        appBarLayout = null
        actionBar = null
        binding = null
        menuItem = null
        menuProvider?.let { provider ->
            menuHost?.removeMenuProvider(provider)
            menuProvider = null
        }
        menuHost = null
        navigation = null
        sharedPrefTheme = null
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
        currentCoin = null
        usdCoinData = null
        userData = null
    }
}