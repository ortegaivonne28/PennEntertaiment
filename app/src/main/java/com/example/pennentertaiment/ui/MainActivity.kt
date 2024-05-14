package com.example.pennentertaiment.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.pennentertaiment.R
import com.example.pennentertaiment.ui.theme.AirQualityScreen
import com.example.pennentertaiment.ui.theme.PennEntertaimentTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val viewModel: AirQualityScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PennEntertaimentTheme {
                SplashScreen()
                val permissionGranted = remember { mutableStateOf(false) }
                val permissionDenied = remember { mutableStateOf(false) }

                val requestPermissionLauncher =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted: Boolean ->
                            if (isGranted) {
                                getCurrentLocation({
                                    notifyViewModelOfLocation(it)
                                    permissionGranted.value = true
                                }, {
                                    //TODO this would probably be something different and more elaborate.
                                    permissionDenied.value = true
                                })
                                permissionGranted.value = true
                            } else {
                                permissionDenied.value = true
                            }
                        })

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                if (!areLocationPermissionsGranted()) {
                    SideEffect {
                        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                } else {
                    getCurrentLocation({
                        notifyViewModelOfLocation(it)
                        permissionGranted.value = true
                    }, {
                        //TODO this would probably be something different and more elaborate.
                        permissionDenied.value = true
                    })
                }

                if (permissionDenied.value) {
                    PermissionOrDeniedRevokedScreen(R.string.permission_denied)
                }

                if (permissionGranted.value) {
                    AirQualityScreen(viewModel)
                }


            }
        }
    }

    private fun notifyViewModelOfLocation(location: Pair<Double,Double>) {
        viewModel.location = location
        viewModel.getAirQualityByGeolocation()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(
        onGetCurrentLocationSuccess: (Pair<Double, Double>) -> Unit,
        onGetCurrentLocationFailed: (Exception) -> Unit,
        priority: Boolean = true
    ) {
        val accuracy = if (priority) Priority.PRIORITY_HIGH_ACCURACY
        else Priority.PRIORITY_BALANCED_POWER_ACCURACY

        if (areLocationPermissionsGranted()) {
            fusedLocationProviderClient.getCurrentLocation(
                accuracy, CancellationTokenSource().token,
            ).addOnSuccessListener { location ->
                location?.let {
                    onGetCurrentLocationSuccess(Pair(it.latitude, it.longitude))
                }
            }.addOnFailureListener { exception ->
                onGetCurrentLocationFailed(exception)
            }
        }
    }

    private fun areLocationPermissionsGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

}