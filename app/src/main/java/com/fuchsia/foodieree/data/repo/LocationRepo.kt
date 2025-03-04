package com.fuchsia.foodieree.data.repo

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationRepo @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onSuccess: (Pair<Double, Double>) -> Unit,
        onFailure: (Exception) -> Unit,
        highAccuracy: Boolean = true
    ) {
        val priority = if (highAccuracy) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY

        fusedLocationProviderClient.getCurrentLocation(priority, CancellationTokenSource().token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    onSuccess(Pair(location.latitude, location.longitude))
                } else {
                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { lastLocation ->
                            if (lastLocation != null) {
                                onSuccess(Pair(lastLocation.latitude, lastLocation.longitude))
                            } else {
                                onFailure(Exception("Location not available"))
                            }
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
