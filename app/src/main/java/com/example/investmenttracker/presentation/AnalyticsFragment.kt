package com.example.investmenttracker.presentation

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.investmenttracker.R
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.model.UserData
import com.example.investmenttracker.databinding.FragmentAnalyticsBinding
import com.example.investmenttracker.domain.use_case.util.*
import com.example.investmenttracker.presentation.view_model.AnalyticsViewModel
import com.example.investmenttracker.presentation.view_model.AnalyticsViewModelFactory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AnalyticsFragment : Fragment() {

    private var binding: FragmentAnalyticsBinding? = null
    @Inject
    lateinit var factory: AnalyticsViewModelFactory
    private lateinit var viewModel: AnalyticsViewModel

    private var mProgressDialog: Dialog? = null
    private var navigation: BottomNavigationView? = null
    private var pieChart: PieChart? = null
    private var percentFormatter: PercentFormatter? = null
    private var user: UserData? = null
    private var walletCoins: List<CoinModel>? = null

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
        viewModel = ViewModelProvider(this, factory)[AnalyticsViewModel::class.java]

        mProgressDialog = showProgressDialog(requireContext())
        pieChart = binding!!.pieChart
        percentFormatter = ChartUtil.getPercentFormatter(pieChart!!)

        navigation = activity?.findViewById(R.id.bottom_navigation) as BottomNavigationView

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_analyticsFragment_to_mainFragment)
                navigation?.selectedItemId = R.id.home
            }
        })

        viewModel.userDataLiveData.observe(viewLifecycleOwner) { userData ->
            user = userData
        }
        viewModel.walletCoins.observe(viewLifecycleOwner) {coins->
            walletCoins = coins
        }

        // set an emptylist to walletCoins if data is actually null
        viewModel.combinedLiveData.addSource(viewModel.userDataLiveData) { userData ->
            viewModel.combinedLiveData.value = Pair(userData, viewModel.walletCoins.value ?: emptyList())
        }
        viewModel.combinedLiveData.addSource(viewModel.walletCoins) { coins ->
            viewModel.combinedLiveData.value = Pair(viewModel.userDataLiveData.value, coins)
        }

        // observe combined live data for both userdata and coinsWallet,
        // this will prevent NullPointerException crashes when user tries to switch between fragments rapidly.
        viewModel.combinedLiveData.observe(viewLifecycleOwner) { (userData, coins) ->
            if (userData != null && coins.isNotEmpty()) {

                cancelProgressDialog(mProgressDialog!!)

                user = userData
                walletCoins = coins
                setupView()
                setupActionBar()

            }else{
                showProgressDialog(requireContext())
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setupView(){
        // setup pie chart
        if (walletCoins!!.isNotEmpty()){
            createPieChart(pieChart!!, percentFormatter ,requireContext(), walletCoins!!)
            binding!!.pieChart.visibility = View.VISIBLE
            binding!!.tvPieChartTitle.visibility = View.VISIBLE
        }else{
            binding!!.pieChart.visibility = View.GONE
            binding!!.tvPieChartTitle.visibility = View.GONE
        }
        // setup Investment Details section
        val userInvestment = user!!.userTotalInvestment
        val userInvestmentWorth = user!!.userTotalBalanceWorth
        binding!!.tvTotalUserInvestment.text = "$"+ formatToTwoDecimal(userInvestment).toString()
        binding!!.tvTotalUserInvestmentWorth.text = "$"+ formatToTwoDecimal(userInvestmentWorth).toString()

        val investmentReturnPercentage = calculateProfitLossPercentage(userInvestmentWorth, userInvestment)
        println(investmentReturnPercentage)
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
        binding!!.tvInvestmentProfitLoss.text = "$$investmentProfitLoss"

        // setup Insight section
        // most profit- loss texts
        try {
            val mostProfitByCoin = mostProfitByCoin(walletCoins!!)
            val mostProfitAmount = "$"+ formatToTwoDecimal(mostProfitByCoin.totalInvestmentWorth - mostProfitByCoin.totalInvestmentAmount)
            val tvMostProfitTokenText = spannableTextGreen(
                formatCoinNameText(mostProfitByCoin.symbol) + " $mostProfitAmount profit on investment.",
                mostProfitAmount,
                requireContext()
            )
            binding!!.tvMostProfitToken.text = tvMostProfitTokenText
            binding!!.tvMostProfitToken.visibility = View.VISIBLE
        }catch (e: NoSuchElementException){
            e.printStackTrace()
            binding!!.tvMostProfitToken.visibility = View.GONE
        }

        try {
            val mostLossByCoin = mostLossByCoin(walletCoins!!)
            var mostLossAmount = formatToTwoDecimal((mostLossByCoin.totalInvestmentWorth - mostLossByCoin.totalInvestmentAmount)).toString()
            if (mostLossAmount.contains("-")){
                mostLossAmount = mostLossAmount.replace("-","")
            }
            mostLossAmount = "$$mostLossAmount"

            val tvMostLossTokenText = spannableTextRed(
                formatCoinNameText(mostLossByCoin.symbol) + " $mostLossAmount loss on investment.",
                mostLossAmount,
                requireContext()
            )
            binding!!.tvMostLossToken.text = tvMostLossTokenText
            binding!!.tvMostLossToken.visibility = View.VISIBLE
        }catch (e: NoSuchElementException){
            e.printStackTrace()
            binding!!.tvMostLossToken.visibility = View.GONE
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
            binding!!.tvMostProfitByPercentage.visibility = View.VISIBLE
        }catch (e: NoSuchElementException){
            e.printStackTrace()
            binding!!.tvMostProfitByPercentage.visibility = View.GONE
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
            binding!!.tvMostLossTokenByPercentage.visibility = View.VISIBLE
        }catch (e: NoSuchElementException){
            e.printStackTrace()
            binding!!.tvMostLossTokenByPercentage.visibility = View.GONE
        }

    }


    private fun setupActionBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding!!.toolbarAnalyticsFragment)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (actionBar != null){
            actionBar.title = "Analytics"
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow_white)
            binding!!.toolbarAnalyticsFragment.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_analyticsFragment_to_mainFragment)
                navigation?.selectedItemId = R.id.home
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        mProgressDialog = null
        pieChart = null
        percentFormatter = null
        navigation = null
        user = null
        walletCoins = null

        viewModel.combinedLiveData.removeObservers(viewLifecycleOwner)
        viewModel.userDataLiveData.removeObservers(viewLifecycleOwner)
        viewModel.walletCoins.removeObservers(viewLifecycleOwner)
    }
}