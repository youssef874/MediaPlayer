package com.example.mediaplayer3.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediaplayer3.R
import com.example.mediaplayer3.ui.Constant
import com.example.mediaplayer3.ui.PauseButton
import com.example.mediaplayer3.ui.PlayButton
import com.example.mediaplayer3.ui.theme.LightBlue
import com.example.mediaplayer3.viewModel.TrackDetailViewModel
import com.example.mediaplayer3.viewModel.data.trackDetail.TrackDetailsUiEvent
import com.example.mediaplayer3.data.entity.RepeatMode
import com.example.mplog.MPLogger

@Composable
fun AudioDetailScreen(
    trackDetailViewModel: TrackDetailViewModel = viewModel(),
    songId: Long,
    onBack: () -> Unit
) {
    MPLogger.i(
        Constant.TrackDetail.CLASS_NAME,
        "AudioDetailScreen",
        Constant.TrackDetail.TAG,
        "songId: $songId"
    )
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        trackDetailViewModel.onEvent(TrackDetailsUiEvent.SearchForCurrentSong(songId, context))
    }
    BackHandler {
        onBack()
    }
    val state by trackDetailViewModel.uiState.collectAsState()
    if (songId != -1L) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(color = LightBlue)
                .scrollable(
                    orientation = Orientation.Horizontal,
                    state = rememberScrollableState {
                        if (it < -5) {
                            onBack()
                        }
                        it
                    }
                )
        ) {
            Column(
                modifier = Modifier.weight(3F),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                ItemImage(
                    imageUri = state.currentSong.albumThumbnailUri,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = state.currentSong.songName,
                    color = Color.White,
                    maxLines = 1,
                    fontSize = 32.sp
                )
                Text(
                    text = state.currentSong.artistName,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            //Spacer(modifier = Modifier.weight(2F))
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                DetailsSongControlsButtons(
                    isPlaying = state.isPlaying,
                    isInRandomMode = state.isInRandomMode,
                    repeatMode = state.repeatMode,
                    playOrPause = {
                         trackDetailViewModel.onEvent(TrackDetailsUiEvent.PauseOrResume(state.currentSong, context))
                    },
                    next = {
                          trackDetailViewModel.onEvent(TrackDetailsUiEvent.PlayNextSong(context))
                    },
                    previous = {
                          trackDetailViewModel.onEvent(TrackDetailsUiEvent.PlayPreviousSong(context))
                    },
                    shuffleAction = { /*TODO*/ },
                    changeRepeatMode = {}
                )
            }
        }
    }
}

@Composable
fun DetailsSongControlsButtons(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    isInRandomMode: Boolean = false,
    repeatMode: RepeatMode = RepeatMode.NO_REPEAT,
    playOrPause: () -> Unit,
    next: () -> Unit,
    previous: () -> Unit,
    shuffleAction: () -> Unit,
    changeRepeatMode: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        ShuffleButton(
            modifier = modifier.weight(1F), isInRandomMode = isInRandomMode
        ) {
            shuffleAction()
        }
        RewindSecondsButton(
            modifier = modifier.weight(1F)
        ) {
            previous()
        }
        PlayOrStoppedButton(
            modifier = modifier.weight(1F), isStopped = isPlaying
        ) {
            playOrPause()
        }
        SkipSecondsButton(
            modifier = modifier.weight(1F)
        ) {
            next()
        }
        RepeatButton(
            modifier = modifier.weight(1F), repeatMode = repeatMode
        ) {
            changeRepeatMode()
        }
    }
}

@Composable
fun RewindSecondsButton(modifier: Modifier = Modifier, rewind: () -> Unit) {
    Icon(
        painter = painterResource(id = R.drawable.baseline_fast_rewind_24),
        contentDescription = null,
        modifier = modifier.clickable {
            rewind()
        },
        tint = Color.White
    )
}

@Composable
fun ShuffleButton(
    modifier: Modifier = Modifier,
    isInRandomMode: Boolean = false,
    shuffleAction: () -> Unit
) {
    if (!isInRandomMode) {
        Icon(Icons.Filled.Shuffle, contentDescription = null, modifier = modifier.clickable {
            shuffleAction()
        }, tint = Color.White)
    } else {
        Icon(Icons.Filled.ShuffleOn, contentDescription = null, modifier = modifier.clickable {
            shuffleAction()
        }, tint = Color.White)
    }
}

@Composable
fun PlayOrStoppedButton(
    modifier: Modifier = Modifier,
    isStopped: Boolean = true,
    action: () -> Unit
) {
    if (isStopped) {
        PauseButton(modifier) {
            action()
        }
    } else {
        PlayButton(modifier = modifier) {
            action()
        }
    }
}

@Composable
fun SkipSecondsButton(modifier: Modifier = Modifier, skipSeconds: () -> Unit) {
    Icon(
        painter = painterResource(id = R.drawable.baseline_fast_forward_24),
        contentDescription = null,
        modifier = modifier.clickable {
            skipSeconds()
        },
        tint = Color.White
    )
}

@Composable
fun RepeatButton(
    modifier: Modifier = Modifier,
    repeatMode: RepeatMode = RepeatMode.NO_REPEAT,
    autoPlayAction: () -> Unit
) {
    when (repeatMode) {
        RepeatMode.NO_REPEAT -> {
            Icon(
                painter = painterResource(id = R.drawable.repeat_off_icon_138246),
                contentDescription = null,
                modifier = modifier.clickable {
                    autoPlayAction()
                },
                tint = Color.White
            )
        }

        RepeatMode.ONE_REPEAT -> {
            Icon(
                Icons.Filled.RepeatOne,
                contentDescription = null,
                modifier = modifier.clickable {
                    autoPlayAction()
                },
                tint = Color.White
            )
        }

        RepeatMode.REPEAT_ALL -> {
            Icon(
                Icons.Filled.Repeat,
                contentDescription = null,
                modifier = modifier.clickable {
                    autoPlayAction()
                },
                tint = Color.White
            )
        }
    }
}