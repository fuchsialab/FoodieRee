package com.fuchsia.foodieree.viewmodel

import androidx.lifecycle.ViewModel
import com.fuchsia.foodieree.data.model.FoodVideoList
import com.fuchsia.foodieree.data.model.FoodVideoModel
import com.fuchsia.foodieree.data.repo.LocationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepo
) : ViewModel() {

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> = _userLocation

    private val _nearbyFood = MutableStateFlow<List<FoodVideoModel>>(emptyList())
    val nearbyFood: StateFlow<List<FoodVideoModel>> = _nearbyFood

    fun fetchUserLocation() {
        viewModelScope.launch {
            locationRepository.getCurrentLocation(
                onSuccess = { location ->
                    _userLocation.value = location
                    _nearbyFood.value = filterNearbyFood(location)
                },
                onFailure = { exception ->
                    println("Error fetching location: ${exception.message}")
                }
            )
        }
    }

    fun filterNearbyFood(userLocation: Pair<Double, Double>): List<FoodVideoModel> {
        return FoodVideoList.foodReels.filter {
            calculateDistance(
                userLocation.first,
                userLocation.second,
                it.latitude,
                it.longitude
            ) <= 10
        }
    }

    // Calculate distance between vendor and user

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radius = 6371 // Radius of Earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radius * c
    }
}