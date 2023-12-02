package com.example.mediaplayer3.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mediaplayer3.R

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}


@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.music_off_fill0_wght400_grad0_opsz24),
            contentDescription = null,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Sorry we couldn't fetch you a song list for you",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun PlayButton(modifier: Modifier = Modifier, onPlay: () -> Unit) {
    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = modifier.clickable {
        onPlay()
    }, tint = Color.White)
}

@Composable
fun PauseButton(modifier: Modifier = Modifier, onPause: () -> Unit) {
    Icon(Icons.Filled.Pause, contentDescription = null, modifier = modifier.clickable {
        onPause()
    }, tint = Color.White)
}

@Composable
fun NextButton(modifier: Modifier = Modifier, onNext: () -> Unit) {
    Icon(Icons.Filled.SkipNext, contentDescription = null, modifier = modifier.clickable {
        onNext()
    }, tint = Color.White)
}

@Composable
fun PreviousButton(modifier: Modifier = Modifier, onPrevious: () -> Unit) {
    Icon(Icons.Filled.SkipPrevious, contentDescription = null, modifier = modifier.clickable {
        onPrevious()
    }, tint = Color.White)
}