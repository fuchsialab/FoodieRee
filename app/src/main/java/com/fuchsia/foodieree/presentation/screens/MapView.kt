package com.fuchsia.foodieree.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapView(
    desLat: Double,
    desLong: Double,
    userLat: Double,
    userLong: Double

) {
    Log.d("MapView", "Latitude: $desLat, Longitude: $desLong")
    Log.d("UsrMapView", "Latitude: $userLat, Longitude: $userLong")

    val desLocation = LatLng(desLat, desLong)
    val userLocation = LatLng(userLat, userLong)


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(desLocation, 15f)
    }

    var uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
    }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        Marker(
            state = MarkerState(position = desLocation),
            title = "Restaurant Location"
        )
        Marker(
            state = MarkerState(position = userLocation),
            title = "User Location"
        )

    }
}