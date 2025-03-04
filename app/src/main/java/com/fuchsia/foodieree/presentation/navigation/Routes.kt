package com.fuchsia.foodieree.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Routes {

    @Serializable
    object BottomNavBar : Routes()

    @Serializable
    data class MapView(
        val desLat: Double,
        val desLong: Double,
        val userLat: Double,
        val userLong: Double

    ): Routes()

}