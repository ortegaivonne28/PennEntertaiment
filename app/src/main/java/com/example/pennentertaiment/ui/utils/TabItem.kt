package com.example.pennentertaiment.ui.utils

import androidx.compose.runtime.Composable

data class TabItem(
    val title: String,
    val screen: @Composable () -> Unit
)

enum class DAY {
    YESTERDAY,
    TODAY,
    TOMORROW
}