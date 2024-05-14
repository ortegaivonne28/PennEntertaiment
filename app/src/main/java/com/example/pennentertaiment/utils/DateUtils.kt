package com.example.pennentertaiment.utils

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

fun String?.getMonthAndDay(): String {
    return this?.let {
        val localDate = LocalDate.parse(it)
        val month = localDate.month.getDisplayName(TextStyle.SHORT, Locale("us"))
        val day = localDate.dayOfMonth
        "$month $day"
    } ?: ""
}