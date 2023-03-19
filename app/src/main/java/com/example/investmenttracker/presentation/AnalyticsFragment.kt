package com.example.investmenttracker.presentation

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
import com.example.investmenttracker.databinding.FragmentAnalyticsBinding
import com.example.investmenttracker.domain.use_case.util.ChartUtil
import com.example.investmenttracker.domain.use_case.util.createPieChart
import com.example.investmenttracker.domain.use_case.util.showProgressDialog
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
        setupView()
        setupActionBar()
    }


    private fun setupView(){
        // setup pie chart
        val walletCoins = viewModel.walletCoins
        createPieChart(pieChart!!, percentFormatter ,requireContext(), walletCoins)
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
    }
}