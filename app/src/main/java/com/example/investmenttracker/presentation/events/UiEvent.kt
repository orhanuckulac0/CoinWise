package com.example.investmenttracker.presentation.events

sealed class UiEvent {
    data class ShowErrorSnackbar(
        val message: String,
    ): UiEvent()
    data class ShowCoinAddedSnackbar(
        val message: String,
    ): UiEvent()
}
