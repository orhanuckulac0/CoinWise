package com.example.investmenttracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.investmenttracker.data.util.Resource
import com.example.investmenttracker.presentation.view_model.CoinViewModel
import com.example.investmenttracker.presentation.view_model.CoinViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var factory: CoinViewModelFactory
    lateinit var viewModel: CoinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, factory)[CoinViewModel::class.java]
        getCoin()
    }

    // test function to see if all works well
    private fun getCoin() {
        viewModel.getCoin("bitcoin")

        viewModel.coinSearched.observe(this){response->
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        Log.i("MYTAG", it.data.toString())
                    }
                }
                is Resource.Error -> {
                    response.message?.let {
                        Log.i("MYTAG", "Error message $it")
                    }
                    response.data?.let {
                        Log.i("MYTAG", "Error data $it")
                    }
                }

                else -> {}
            }
        }
    }
}