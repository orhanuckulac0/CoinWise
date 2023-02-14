package com.example.investmenttracker

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.data.model.CoinModel
import com.example.investmenttracker.data.util.Resource
import com.example.investmenttracker.databinding.ActivitySearchCoinBinding
import com.example.investmenttracker.presentation.view_model.SearchCoinViewModel
import com.example.investmenttracker.presentation.view_model.SearchCoinViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchCoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchCoinBinding
    lateinit var viewModel: SearchCoinViewModel
    @Inject lateinit var factory: SearchCoinViewModelFactory

    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_coin)

        binding = ActivitySearchCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            var model: CoinModel? = null
            when(result) {
                is Resource.Success -> {
                    result.data?.getAsJsonObject("data")?.asJsonObject?.asMap()?.forEach {
                        model = CoinModel(
                            0,
                            name = it.value.asJsonObject.get("name").toString(),
                            symbol = it.value.asJsonObject.get("symbol").toString(),
                            price = it.value.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("price").toString().toDouble(),
                            marketCap = it.value.asJsonObject.get("quote").asJsonObject.get("USD").asJsonObject.get("market_cap").toString().toDouble()
                        )
                    }
                    viewModel.coinSearchInputText.value = ""
                    Log.i("MYTAG", "$model")
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

    private fun showProgressDialog(){
        mProgressDialog = Dialog(this)
        mProgressDialog?.setContentView(R.layout.progress_bar)
        mProgressDialog?.show()
    }

    private fun cancelProgressDialog(){
        mProgressDialog?.dismiss()
    }
}