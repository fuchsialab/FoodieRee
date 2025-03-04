package com.fuchsia.foodieree.presentation.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.fuchsia.foodieree.presentation.screens.BottomNavBar
import com.fuchsia.foodieree.presentation.screens.MapView
import com.fuchsia.foodieree.viewmodel.RoomViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun App (
    modifier: Modifier = Modifier,
){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.BottomNavBar
    ) {

        composable<Routes.BottomNavBar> {
            BottomNavBar(
                navController = navController
            )
        }

        composable<Routes.MapView> {
            val desLat = it.arguments?.getDouble("desLat")
            val desLong = it.arguments?.getDouble("desLong")
            val userLat = it.arguments?.getDouble("userLat")
            val userLong = it.arguments?.getDouble("userLong")

            MapView(
                desLat = desLat!!,
                desLong = desLong!!,
                userLat = userLat!!,
                userLong = userLong!!

            )
        }
    }


}