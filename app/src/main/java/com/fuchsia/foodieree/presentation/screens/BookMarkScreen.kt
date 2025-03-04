package com.fuchsia.foodieree.presentation.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fuchsia.foodieree.data.room.FoodEntity
import com.fuchsia.foodieree.presentation.screens.player.VideoPlayer
import com.fuchsia.foodieree.viewmodel.AppState
import com.fuchsia.foodieree.viewmodel.RoomViewModel
import kotlinx.coroutines.launch

@Composable
fun BookMarkScreen(
    isBookmarkVisible: Boolean,
    roomViewModel: RoomViewModel = hiltViewModel()
) {



    val state = roomViewModel.state.collectAsState()

    // get saved food videos
    LaunchedEffect(Unit) {
        roomViewModel.getAllFoods()
    }

    when (state.value) {

        is AppState.Data -> {
            val data = (state.value as AppState.Data).data
            Log.d("TAG", "BookMarkScreen: $data")

            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { data.size }
            )
            val currentPage by remember { derivedStateOf { pagerState.currentPage } }


            if (data.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->

                        // pass items
                        BookmarkFoodReelItem(
                            data[page],
                            shouldPlay = (page == currentPage) && isBookmarkVisible,
                            viewModel = roomViewModel
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No Favourite Food")
                }
            }


        }

        is AppState.Loading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        }
    }
}

//video item view for bookmark screen
@Composable
fun BookmarkFoodReelItem(
    reel: FoodEntity,
    shouldPlay: Boolean,
    viewModel: RoomViewModel = hiltViewModel(),

    ) {
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
                .fillMaxSize()
                .padding(bottom = 50.dp)
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
            }

            Spacer(modifier = Modifier.weight(1f))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxWidth().padding(start = 16.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Crave & Order Button
                val context = LocalContext.current
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Order button is clicked", Toast.LENGTH_SHORT)
                            .show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)

                ) {
                    Text(
                        text = "Crave & Order",
                        color = Color.White
                    )
                }

                val scope = rememberCoroutineScope()

                // delete fav item button
                IconButton(
                    onClick = {

                        viewModel.deleteFood(
                            foodEntity = FoodEntity(
                                id = reel.id,
                                title = reel.title,
                                latitude = reel.latitude,
                                longitude = reel.longitude,
                                location = reel.location,
                                videoUrl = reel.videoUrl
                            )
                        )
                        Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT)
                            .show()

                        scope.launch {
                            viewModel.getAllFoods()
                        }

                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}
