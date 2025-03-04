package com.fuchsia.foodieree.presentation.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fuchsia.foodieree.data.model.FoodVideoModel
import com.fuchsia.foodieree.data.room.FoodEntity
import com.fuchsia.foodieree.presentation.navigation.Routes
import com.fuchsia.foodieree.presentation.screens.player.VideoPlayer
import com.fuchsia.foodieree.viewmodel.AppState
import com.fuchsia.foodieree.viewmodel.LocationViewModel
import com.fuchsia.foodieree.viewmodel.RoomViewModel


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun NearbyScreen(
    isNearbyScreenVisible: Boolean,
    viewModel: LocationViewModel = hiltViewModel(),
    roomViewModel: RoomViewModel = hiltViewModel(),
    navController: NavController

) {
    val context = LocalContext.current
    val isPermissionGranted = rememberLocationPermissionState()
    val userLocation by viewModel.userLocation.collectAsState()
    val nearbyFood by viewModel.nearbyFood.collectAsState()
    var showGPSDialog by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { nearbyFood.size }
    )
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }

    //get user location
    LaunchedEffect(Unit) {
        if (isPermissionGranted) {
            viewModel.fetchUserLocation()
        }
    }

    val state by roomViewModel.state.collectAsState()

    val data = remember { mutableStateListOf<FoodEntity>() }

    // getting room data
    LaunchedEffect(state) {
        if (state is AppState.Data) {
            data.clear()
            data.addAll((state as AppState.Data).data)
        }
    }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        when {

            //check permission
            !isPermissionGranted -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Location permission is required.", textAlign = TextAlign.Center)
                }
            }

            // check gps on or not
            !isGPSEnabled(context) -> {
                showGPSDialog = true
            }


            userLocation == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Finding Nearby Foods...", textAlign = TextAlign.Center)
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        NearByFoodReelItem(
                            nearbyFood[page],
                            shouldPlay = (page == currentPage) && isNearbyScreenVisible,
                            viewModel = roomViewModel,
                            locationViewModel = viewModel,
                            data,
                            userLocation,
                            navController
                        )
                    }
                }
            }
        }
    }

    // show dialogue for turn on gps
    if (showGPSDialog) {
        ShowGPSDialog(
            onDismiss = { showGPSDialog = false },
            onEnableGPS = {
                showGPSDialog = false
                openLocationSettings(context)
            }
        )
    }
}


// show gps dialogue view
@Composable
fun ShowGPSDialog(onDismiss: () -> Unit, onEnableGPS: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enable GPS") },
        text = { Text("Your GPS is turned off. Please enable it to find nearby food spots.") },
        confirmButton = {
            Button(onClick = onEnableGPS) { Text("Enable GPS") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// checking function for gps
fun isGPSEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

// open location settings
fun openLocationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}

// location permission
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun rememberLocationPermissionState(): Boolean {
    val context = LocalContext.current
    var isPermissionGranted by remember { mutableStateOf(isLocationPermissionGranted(context)) }
    var showDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            isPermissionGranted = permissions.values.all { it }
            if (!isPermissionGranted) showDialog = true
        }
    )

    LaunchedEffect(Unit) {
        if (!isPermissionGranted) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Permission Required") },
            text = { Text("We need your location to find nearby food spots.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                        )
                    )
                }) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    return isPermissionGranted
}

// check permission granted or not
private fun isLocationPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
}

// food video item view
@Composable
fun NearByFoodReelItem(
    reel: FoodVideoModel,
    shouldPlay: Boolean,
    viewModel: RoomViewModel,
    locationViewModel: LocationViewModel,
    data: SnapshotStateList<FoodEntity>,
    userLocation: Pair<Double, Double>?,
    navController: NavController
) {
    val context = LocalContext.current

    val distance by remember(userLocation) {
        derivedStateOf {
            userLocation?.let {
                locationViewModel.calculateDistance(it.first, it.second, reel.latitude, reel.longitude)

            } ?: 0.0
        }
    }

    val isLiked by remember { derivedStateOf { data.any { it.title == reel.title } } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background Video
        VideoPlayer(
            videoUrl = reel.videoUrl,
            shouldPlay = shouldPlay,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)
        )

        // Overlay content
        Box(modifier = Modifier.fillMaxWidth()) {

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Favorite",
                tint = Color.Cyan,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
                    .clickable {

                        navController.navigate(Routes.MapView(
                            desLat = reel.latitude,
                            desLong = reel.longitude,
                            userLat = userLocation!!.first,
                            userLong = userLocation.second

                        ))

                    }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Column (Modifier.padding(10.dp)){
                    Text(
                        text = reel.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Cyan

                    )
                    Text(
                        text = reel.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Cyan

                    )
                    Text(
                        text = "Distance: %.1f km".format(distance),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Cyan

                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxWidth().padding(start = 16.dp, bottom = 2.dp),                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Crave & Order Button
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Order button is clicked", Toast.LENGTH_SHORT)
                                .show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    ) {
                        Text(text = "Crave & Order", color = Color.White)
                    }

                    // Favorite button
                    IconButton(
                        onClick = {
                            if (isLiked) {
                                // Remove from favorites

                                val existingFood = data.find { it.title == reel.title }

                                if (existingFood != null) {
                                    viewModel.deleteFood(existingFood)
                                }
                                data.removeIf { it.title == reel.title } // Update UI instantly
                                Toast.makeText(
                                    context,
                                    "Removed from Favourites",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                // Add to favorites
                                viewModel.insertFood(
                                    foodEntity = FoodEntity(
                                        id = 0,
                                        title = reel.title,
                                        latitude = reel.latitude,
                                        longitude = reel.longitude,
                                        location = reel.location,
                                        videoUrl = reel.videoUrl
                                    )
                                )
                                data.add(
                                    FoodEntity(
                                        0,
                                        reel.title,
                                        reel.latitude,
                                        reel.longitude,
                                        reel.location,
                                        reel.videoUrl
                                    )
                                )
                                Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}