package com.fuchsia.foodieree.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fuchsia.foodieree.data.model.FoodVideoList
import com.fuchsia.foodieree.data.model.FoodVideoModel
import com.fuchsia.foodieree.data.room.FoodEntity
import com.fuchsia.foodieree.presentation.screens.player.VideoPlayer
import com.fuchsia.foodieree.viewmodel.AppState
import com.fuchsia.foodieree.viewmodel.RoomViewModel

@Composable
fun HomeReelScreen(
    isHomeReelScreenVisible: Boolean,
    roomViewModel: RoomViewModel = hiltViewModel()
) {

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { FoodVideoList.foodReels.size }
    )

    val currentPage by remember { derivedStateOf { pagerState.currentPage } }

    val state by roomViewModel.state.collectAsState()

    val data = remember { mutableStateListOf<FoodEntity>() }

    // get saved food videos
    LaunchedEffect(state) {
        if (state is AppState.Data) {
            data.clear()
            data.addAll((state as AppState.Data).data)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            FoodReelItem(
                FoodVideoList.foodReels[page],
                shouldPlay = (page == currentPage) && isHomeReelScreenVisible,
                viewModel = roomViewModel,
                data
            )
        }
    }
}


// video item view

@Composable
fun FoodReelItem(
    reel: FoodVideoModel,
    shouldPlay: Boolean,
    viewModel: RoomViewModel = hiltViewModel(),
    data: SnapshotStateList<FoodEntity>
) {
    val context = LocalContext.current

    // Observe item for fav list
    val isLiked by remember { derivedStateOf { data.any { it.title == reel.title } } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // video player call
        VideoPlayer(
            videoUrl = reel.videoUrl,
            shouldPlay = shouldPlay,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {

            // video/vendor details
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
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth().padding(start = 16.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
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

                            // Remove favorite
                            val existingFood = data.find { it.title == reel.title }

                            if (existingFood != null) {
                                viewModel.deleteFood(existingFood)
                            }
                            data.removeIf { it.title == reel.title } // Update UI
                            Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show()
                        } else {

                            // Add to favorite
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
                            data.add(FoodEntity(0, reel.title, reel.latitude, reel.longitude, reel.location, reel.videoUrl)) // Update UI instantly
                            Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show()
                        }

                    },
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
