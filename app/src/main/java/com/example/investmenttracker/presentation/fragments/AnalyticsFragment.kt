package com.example.investmenttracker.presentation.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.CurrencyModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.databinding.FragmentAnalyticsBinding
import com.example.investmenttracker.domain.use_case.util.*
import com.example.investmenttracker.presentation.view_model.AnalyticsViewModel
import com.example.investmenttracker.presentation.view_model_factory.AnalyticsViewModelFactory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AnalyticsFragment : Fragment() {

    private var binding: FragmentAnalyticsBinding? = null
    @Inject
    lateinit var factory: AnalyticsViewModelFactory
    private lateinit var viewModel: AnalyticsViewModel
    private var scrollView: ScrollView? = null
    private var toolbar: Toolbar? = null
    private var appBarLayout: AppBarLayout? = null
    private var actionBar: ActionBar? = null

    private var mProgressDialog: Dialog? = null
    private var navigation: BottomNavigationView? = null
    private var pieChart: PieChart? = null
    private var percentFormatter: PercentFormatter? = null

    private var user: UserData? = null
    private var walletCoins: List<CoinModel>? = null
    private var currencies: List<CurrencyModel>? = null
    private var currencyRate: Float = 0f
    private var sharedPref: SharedPreferences? = null
    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAnalyticsBinding.bind(view)
        scrollView = binding?.svAnalyticFragment
        toolbar = binding?.toolbarAnalyticsFragment
        appBarLayout = binding?.appBarLayoutAnalyticsFragment
        viewModel = ViewModelProvider(this, factory)[AnalyticsViewModel::class.java]

        mProgressDialog = showProgressDialog(requireContext())
        mProgressDialog!!.show()
        pieChart = binding!!.pieChart
        percentFormatter = ChartUtil.getPercentFormatter(pieChart!!)
        sharedPref = requireContext().getSharedPreferences(Constants.THEME_PREF,
            Context.MODE_PRIVATE
        )

        navigation = activity?.findViewById(R.id.bottom_navigation) as BottomNavigationView

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_analyticsFragment_to_mainFragment)
                navigation?.selectedItemId = R.id.home
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback!!)

        viewModel.getCurrencyData()

        viewModel.userDataLiveData.observe(viewLifecycleOwner) { userData ->
            user = userData
        }
        viewModel.walletCoins.observe(viewLifecycleOwner) {coins->
            walletCoins = coins
        }


        // observe combined live data for both userdata and coinsWallet,
        // this will prevent NullPointerException crashes when user tries to switch between fragments rapidly.
        viewModel.combinedLiveData.observe(viewLifecycleOwner) { (userData, coins) ->
            if (
                userData != null &&
                coins.isNotEmpty() &&
                userData.userTotalInvestment != 0.0  // in case user just added the coin
            ) {
                user = userData
                walletCoins = coins
                setupActionBar()
                observeCurrencyData()
            }else{
                setupActionBar()
                binding!!.pieChart.visibility = View.GONE
                binding!!.tvPieChartTitle.visibility = View.GONE
                binding!!.llInvestmentDetails.visibility = View.GONE
                binding!!.llInvestmentInsights.visibility = View.GONE
                binding!!.tvEmptyWallet.visibility = View.VISIBLE
                cancelProgressDialog(mProgressDialog!!)
            }
        }
    }

    private fun observeCurrencyData(){
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.currencyData.observe(viewLifecycleOwner) {resource->
                when (resource) {
                    is Resource.Loading -> {
                        // do nothing, progress dialog is currently showing
                    }
                    is Resource.Success -> {
                        currencies = resource.data
                        setupView()
                    }
                    is Resource.Error -> {
                        cancelProgressDialog(mProgressDialog!!)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupView(){
        val userInvestment: Double
        val userInvestmentWorth: Double
        val symbol = user!!.userCurrentCurrency.substringBefore(" ").trim()
        val userCurrency = user!!.userCurrentCurrency.substringAfter(" ").trim()

        if (user!!.userCurrentCurrency != Constants.USD) {
            currencies?.forEach {
                if (it.currencyName == userCurrency){
                    currencyRate = it.currencyRate
                }
            }
            userInvestment = user!!.userTotalInvestment * currencyRate.toDouble()
            userInvestmentWorth = user!!.userTotalBalanceWorth * currencyRate.toDouble()
        }else{
            userInvestment = user!!.userTotalInvestment
            userInvestmentWorth = user!!.userTotalBalanceWorth
        }

        // setup pie chart
        if (walletCoins!!.isNotEmpty()){
            val theme = sharedPref?.getBoolean(Constants.SWITCH_STATE_KEY, true)
            createPieChart(pieChart!!, percentFormatter ,requireContext(), walletCoins!!, theme!!)
            binding!!.pieChart.visibility = View.VISIBLE
            binding!!.tvPieChartTitle.visibility = View.VISIBLE
            binding!!.llInvestmentDetails.visibility = View.VISIBLE
            binding!!.llInvestmentInsights.visibility = View.VISIBLE
            binding!!.tvEmptyWallet.visibility = View.GONE
        }
        // setup Investment Details section
        binding!!.tvTotalUserInvestment.text = symbol + formatToTwoDecimalWithComma(userInvestment)
        binding!!.tvTotalUserInvestmentWorth.text = symbol + formatToTwoDecimalWithComma(userInvestmentWorth)
        val investmentReturnPercentage = calculateProfitLossPercentage(userInvestmentWorth, userInvestment)
        // first check for NaN and 0.0 values to avoid crashes
        val holder = investmentReturnPercentage.replace("%","").toDouble()
        if (!holder.isNaN() || holder != 0.0){
            if (investmentReturnPercentage.contains("-")){
                binding!!.tvInvestmentReturnPercentage.setTextColor(requireContext().getColor(R.color.red_color_percentage))
                binding!!.tvInvestmentReturnPercentage.text = investmentReturnPercentage
            }else{
                binding!!.tvInvestmentReturnPercentage.setTextColor(requireContext().getColor(R.color.green_color_percentage))
                binding!!.tvInvestmentReturnPercentage.text = investmentReturnPercentage
            }
        }else{
            binding!!.tvInvestmentReturnPercentage.setTextColor(requireContext().getColor(R.color.white))
            binding!!.tvInvestmentReturnPercentage.text = "0.0%"
        }

        var investmentProfitLoss = formatToTwoDecimal(userInvestmentWorth-userInvestment).toString()
        if (investmentProfitLoss.contains("-")){
            binding!!.tvInvestmentProfitLoss.setTextColor(requireContext().getColor(R.color.red_color_percentage))
            investmentProfitLoss = investmentProfitLoss.replace("-","")
        }else if (investmentProfitLoss.toDouble() != 0.0 || !investmentProfitLoss.toDouble().isNaN()){
            binding!!.tvInvestmentProfitLoss.setTextColor(requireContext().getColor(R.color.green_color_percentage))
        }else {
            binding!!.tvInvestmentProfitLoss.setTextColor(requireContext().getColor(R.color.white))
        }
        binding!!.tvInvestmentProfitLoss.text = symbol + formatToTwoDecimalWithComma(investmentProfitLoss.toDouble())

        // setup Insight section
        // most profit- loss texts
        try {
            val mostProfitByCoin = mostProfitByCoin(walletCoins!!)
            val mostProfitAmount: String = if (user!!.userCurrentCurrency != Constants.USD){
                symbol + formatToTwoDecimalWithComma(
                    (mostProfitByCoin.totalInvestmentWorth - mostProfitByCoin.totalInvestmentAmount)* currencyRate.toDouble()
                )
            }else{
                symbol + formatToTwoDecimalWithComma((mostProfitByCoin.totalInvestmentWorth - mostProfitByCoin.totalInvestmentAmount))
            }
            if (mostProfitAmount.contains("-") || mostProfitAmount == "$0.0"){
                binding!!.tvMostProfitToken.text = "${symbol}0 profit on investments."
            }else{
                val tvMostProfitTokenText = spannableTextGreen(
                    formatCoinNameText(mostProfitByCoin.symbol) + " $mostProfitAmount profit on investment.",
                    mostProfitAmount,
                    requireContext()
                )
                binding!!.tvMostProfitToken.text = tvMostProfitTokenText
            }
        }catch (e: NoSuchElementException){
            e.printStackTrace()
        }

        try {
            val mostLossByCoin = mostLossByCoin(walletCoins!!)
            var mostLossAmount: String
            mostLossAmount = if (user!!.userCurrentCurrency != Constants.USD){
                formatToTwoDecimalWithComma((mostLossByCoin.totalInvestmentWorth - mostLossByCoin.totalInvestmentAmount)* currencyRate.toDouble())
            }else{
                formatToTwoDecimalWithComma((mostLossByCoin.totalInvestmentWorth - mostLossByCoin.totalInvestmentAmount))
            }
            if (mostLossAmount.contains("-")){
                mostLossAmount = mostLossAmount.replace("-","")
                mostLossAmount = symbol+mostLossAmount
                val tvMostLossTokenText = spannableTextRed(
                    formatCoinNameText(mostLossByCoin.symbol) + " $mostLossAmount loss on investment.",
                    mostLossAmount,
                    requireContext()
                )
                binding!!.tvMostLossToken.text = tvMostLossTokenText

            }else{
                binding!!.tvMostLossToken.text = "${symbol}0 loss on investments."
            }
        }catch (e: NoSuchElementException){
            e.printStackTrace()
        }

        // most profit - loss percentage texts
        // most profit
        try {
            val calcMPPC = mostProfitPercentageByCoin(walletCoins!!)
            val mostProfitPercentageCoin = calcMPPC[0]
            val mostProfitPercentage = calcMPPC[1]
            binding!!.tvMostProfitByPercentage.text = spannableTextGreen(
                "$mostProfitPercentageCoin $mostProfitPercentage profit rate.",
                mostProfitPercentage,
                requireContext()
            )
        }catch (e: NoSuchElementException){
            e.printStackTrace()
            binding!!.tvMostProfitByPercentage.text = "0.0%"
        }catch (e: NullPointerException){
            e.printStackTrace()
            binding!!.tvMostProfitByPercentage.text = "0.0%"
        }

        // most loss
        try {
            val calcMLPC = mostLossPercentageByCoin(walletCoins!!)
            val mostLossPercentageCoin = calcMLPC[0]
            val mostLossPercentage = calcMLPC[1]
            binding!!.tvMostLossTokenByPercentage.text = spannableTextRed(
                "$mostLossPercentageCoin $mostLossPercentage loss rate.",
                mostLossPercentage,
                requireContext()
            )
        }catch (e: NoSuchElementException){
            binding!!.tvMostLossTokenByPercentage.text = "0.0%"
            e.printStackTrace()
        }catch (e: NullPointerException){
            e.printStackTrace()
            binding!!.tvMostLossTokenByPercentage.text = "0.0%"
        }

        // cancel progress dialog here
        cancelProgressDialog(mProgressDialog!!)
    }


    private fun setupActionBar() {
        val theme = sharedPref?.getBoolean(Constants.SWITCH_STATE_KEY, true)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (actionBar != null){
            actionBar?.title = "Analytics"
            actionBar?.setDisplayHomeAsUpEnabled(true)
            if (theme!!){
                actionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            }else{
                actionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_black)
            }
            toolbar?.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_analyticsFragment_to_mainFragment)
                navigation?.selectedItemId = R.id.home
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.userDataLiveData.removeObservers(viewLifecycleOwner)
        viewModel.walletCoins.removeObservers(viewLifecycleOwner)
        viewModel.combinedLiveData.removeObservers(viewLifecycleOwner)
        viewModel.currencyData.removeObservers(viewLifecycleOwner)
        binding = null
        scrollView = null
        actionBar = null
        appBarLayout = null
        toolbar = null
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
        navigation = null
        mProgressDialog = null
        pieChart = null
        percentFormatter = null
        sharedPref = null
        user = null
        walletCoins = null
        currencies = null
    }
}