package com.fuchsia.foodieree.presentation.screens

import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import com.fuchsia.foodieree.R
import com.fuchsia.foodieree.viewmodel.RoomViewModel


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun BottomNavBar(
    navController: NavController
) {

    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    var backPressedOnce by remember { mutableStateOf(false) }
    val context = LocalContext.current


    BackHandler {
        if (backPressedOnce) {
            (context as? ComponentActivity)?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        bottomBar = {

            Column {

                NavigationBar {
                    NavigationBarItem(
                        selected = selectedIndex == 0,
                        onClick = { selectedIndex = 0 },
                        label = {
                            Text(
                                text = "Home",
                                fontFamily = FontFamily.Serif,
                            )
                        },
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        }

                    )
                    NavigationBarItem(
                        selected = selectedIndex == 1,
                        onClick = { selectedIndex = 1 },
                        label = {
                            Text(
                                text = "NearBy",
                                fontFamily = FontFamily.Serif,
                            )
                        },
                        icon = {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "NearBy"
                            )
                        }

                    )
                    NavigationBarItem(
                        selected = selectedIndex == 2,
                        onClick = { selectedIndex = 2 },
                        label = {
                            Text(
                                text = "Favourite",
                                fontFamily = FontFamily.Serif,
                            )
                        },
                        icon = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Favourite"
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedIndex) {
                0 -> HomeReelScreen(
                    isHomeReelScreenVisible = true,
                )

                1 -> NearbyScreen(
                    isNearbyScreenVisible = true,
                    navController = navController

                )

                2 -> BookMarkScreen(
                    isBookmarkVisible = true,

                )
            }
        }
    }
}