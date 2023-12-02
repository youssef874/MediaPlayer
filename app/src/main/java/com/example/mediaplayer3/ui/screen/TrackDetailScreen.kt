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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.ui.Constant
import com.example.mediaplayer3.ui.theme.LightBlue
import com.example.mediaplayer3.ui.timeFormatter
import com.example.mediaplayer3.viewModel.AudioDetailViewModel
import com.example.mediaplayer3.viewModel.data.trackDetail.TrackDetailsUiEvent
import com.example.mpdataprovider.datastore.RepeatMode
import com.example.mplog.MPLogger
import kotlinx.coroutines.flow.map
import kotlin.math.abs

@Composable
fun TrackDetailScreen(
    audioDetailViewModel: AudioDetailViewModel = viewModel(),
    songId: Long,
    onBack: () -> Unit
) {
    MPLogger.i(
        Constant.TrackDetail.CLASS_NAME,
        "TrackDetailScreen",
        Constant.TrackDetail.TAG,
        "songId: $songId"
    )
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        audioDetailViewModel.onEvent(TrackDetailsUiEvent.SearchForCurrentSong(songId, context))
    }
    BackHandler {
        onBack()
    }
    val state by audioDetailViewModel.uiState.map {
        it.currentSong to it.isPlaying
    }.collectAsState(initial = UiAudio() to false)
    if (state.first.id != -1L) {
        MPLogger.i(
            Constant.TrackDetail.CLASS_NAME,
            "TrackDetailScreen",
            Constant.TrackDetail.TAG,
            "state: ${state.first}"
        )
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
                    imageUri = state.first.albumThumbnailUri,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = state.first.songName,
                    color = Color.White,
                    maxLines = 1,
                    fontSize = 32.sp
                )
                Text(
                    text = state.first.artistName,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.weight(2F))
            Column(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                var progress by rememberSaveable {
                    mutableFloatStateOf(0F)
                }
                var move by rememberSaveable {
                    mutableStateOf(false)
                }
                val progressState by audioDetailViewModel.uiState.map { it.songProgress }
                    .collectAsState(
                        initial = 0
                    )
                OtherActions(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .height(50.dp),
                    isFavorite = state.first.isFavorite,
                    onFavoriteButtonClicked = {
                        audioDetailViewModel.onEvent(TrackDetailsUiEvent.ChangeFavoriteStatus(context))
                    })
                AudioRangeLabel(
                    duration = state.first.duration,
                    startAt = if (progressState == -1) 0 else progressState
                )
                Slider(
                    value = if (move) progress else progressState.toFloat(),
                    onValueChange = { value ->
                        move = true
                        progress = value
                    },
                    valueRange = 0F..state.first.duration.toFloat(),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    onValueChangeFinished = {
                        if (state.second) {
                            val diff = abs(progressState.toFloat() - progress)
                            if (progressState.toFloat() > progress) {
                                MPLogger.d(
                                    Constant.TrackDetail.CLASS_NAME,
                                    "TrackDetailScreen",
                                    Constant.TrackDetail.TAG,
                                    "song is playing so rewind: $diff"
                                )
                                audioDetailViewModel.onEvent(
                                    TrackDetailsUiEvent.Rewind(
                                        context = context,
                                        rewindTo = diff.toInt()
                                    )
                                )
                            } else if (progressState.toFloat() < progress) {
                                MPLogger.d(
                                    Constant.TrackDetail.CLASS_NAME,
                                    "TrackDetailScreen",
                                    Constant.TrackDetail.TAG,
                                    "song is playing so forward: $diff"
                                )
                                audioDetailViewModel.onEvent(
                                    TrackDetailsUiEvent.Forward(
                                        context = context,
                                        forwardTo = diff.toInt()
                                    )
                                )
                            }
                        } else {
                            MPLogger.d(
                                Constant.TrackDetail.CLASS_NAME,
                                "TrackDetailScreen",
                                Constant.TrackDetail.TAG,
                                "song is not playing so update playing position: $progress"
                            )
                            audioDetailViewModel.onEvent(
                                TrackDetailsUiEvent.UpdatePlayingPosition(context, progress.toInt())
                            )
                        }
                    }
                )
                val randomState by audioDetailViewModel.uiState.map { it.isInRandomMode }
                    .collectAsState(
                        initial = false
                    )
                val repeatModeState by audioDetailViewModel.uiState.map { it.repeatMode }
                    .collectAsState(
                        initial = RepeatMode.NO_REPEAT
                    )
                DetailsSongControlsButtons(
                    isPlaying = state.second,
                    isInRandomMode = randomState,
                    playOrPause = {
                        move = false
                        audioDetailViewModel.onEvent(
                            TrackDetailsUiEvent.PauseOrResume(
                                state.first,
                                context
                            )
                        )
                    },
                    next = {
                        move = false
                        audioDetailViewModel.onEvent(
                            TrackDetailsUiEvent.PlayNextSong(context = context)
                        )
                    },
                    previous = {
                        move = false
                        audioDetailViewModel.onEvent(
                            TrackDetailsUiEvent.PlayPreviousSong(context = context)
                        )
                    },
                    shuffleAction = {
                        audioDetailViewModel.onEvent(
                            TrackDetailsUiEvent.ChangePlayNextBehavior(
                                context
                            )
                        )
                    }, changeRepeatMode = {
                        audioDetailViewModel.onEvent(TrackDetailsUiEvent.ChangeRepeatMode(context))
                    }
                )
            }
        }
    }
}

@Composable
fun OtherActions(
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteButtonClicked: () -> Unit
) {
    Row(modifier = modifier) {
        AudioListAction(modifier = Modifier
            .padding(start = 16.dp)
            .width(30.dp)
            .height(30.dp)) {

        }
        FavoriteAction(
            modifier = Modifier
                .weight(1F)
                .width(30.dp)
                .height(30.dp),
            isFavorite = isFavorite
        ) {
            onFavoriteButtonClicked()
        }
        AddSongToAction(modifier = Modifier
            .padding(end = 16.dp)
            .width(30.dp)
            .height(30.dp)) {

        }
    }
}

@Composable
fun FavoriteAction(
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteChange: () -> Unit
) {
    if (isFavorite) {
        Icon(Icons.Filled.Favorite, contentDescription = null, modifier = modifier.clickable {
            onFavoriteChange()
        }, tint = Color.White)
    } else {
        Icon(Icons.Filled.FavoriteBorder, contentDescription = null, modifier = modifier.clickable {
            onFavoriteChange()
        }, tint = Color.White)
    }
}

@Composable
fun AddSongToAction(modifier: Modifier = Modifier, onAddSongToRequested: () -> Unit) {
    Icon(Icons.Filled.Add, contentDescription = null, modifier = modifier.clickable {
        onAddSongToRequested()
    }, tint = Color.White)
}

@Composable
fun AudioListAction(modifier: Modifier = Modifier, onAudioListRequest: () -> Unit) {
    Icon(Icons.Filled.QueueMusic, contentDescription = null, modifier = modifier.clickable {
        onAudioListRequest()
    }, tint = Color.White)
}

@Composable
fun AudioRangeLabel(modifier: Modifier = Modifier, startAt: Int = 0, duration: Int) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = startAt.timeFormatter(),
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1F))
        Text(
            text = duration.timeFormatter(),
            color = Color.White,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}