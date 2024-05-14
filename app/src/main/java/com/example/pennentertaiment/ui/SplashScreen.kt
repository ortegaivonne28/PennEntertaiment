package com.example.pennentertaiment.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.pennentertaiment.R

@Composable
fun SplashScreen() {
    Image(painter = painterResource(id = R.drawable.ic_penn_entertaiment), contentDescription = "Penn Entertaiment")
}