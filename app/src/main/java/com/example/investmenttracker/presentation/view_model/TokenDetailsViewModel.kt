package com.example.investmenttracker.presentation.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.investmenttracker.domain.use_case.DeleteCoinUseCase
import com.example.investmenttracker.domain.use_case.GetSingleCoinByIdUseCase

class TokenDetailsViewModel(
    private val app: Application,
    private val getSingleCoinByIdUseCase: GetSingleCoinByIdUseCase,
    private val deleteCoinUseCase: DeleteCoinUseCase
    ): AndroidViewModel(app) {

}