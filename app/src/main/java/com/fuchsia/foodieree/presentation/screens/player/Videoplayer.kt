package com.fuchsia.foodieree.presentation.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayer(videoUrl: String,
                shouldPlay: Boolean,
                modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = shouldPlay

        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(
        modifier = modifier.fillMaxSize().background(color = Color.Black),
    ) {

        AndroidView(
            modifier = Modifier.fillMaxSize() ,
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                }
            },
            update = {
                it.player?.playWhenReady = shouldPlay
            }
        )
    }
}
