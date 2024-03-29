package com.example.mediaplayer3.ui.screen

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mediaplayer3.R
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.ui.Constant
import com.example.mediaplayer3.ui.ErrorScreen
import com.example.mediaplayer3.ui.LoadingScreen
import com.example.mediaplayer3.ui.NextButton
import com.example.mediaplayer3.ui.PauseButton
import com.example.mediaplayer3.ui.PlayButton
import com.example.mediaplayer3.ui.PreviousButton
import com.example.mediaplayer3.ui.listcomponent.ListComponent
import com.example.mediaplayer3.ui.theme.LightBlue
import com.example.mediaplayer3.ui.toItemData
import com.example.mediaplayer3.viewModel.TrackListViewModel
import com.example.mediaplayer3.viewModel.data.tracklist.TrackListUiEvent
import com.example.mpcore.api.log.MPLog


@Composable
fun TrackListScreen(trackListViewModel: TrackListViewModel = hiltViewModel(), navigateToTrackDetail: (Long)->Unit) {
    MPLog.i(
        Constant.TrackList.CLASS_NAME,
        "TrackListScreen",
        Constant.TrackList.TAG,
        "track list displayed"
    )
    val context = LocalContext.current
    val activity = LocalContext.current as? Activity
    BackHandler {
        activity?.finish()
    }
    LaunchedEffect(key1 = Unit) {
        trackListViewModel.onEvent(TrackListUiEvent.LoadData(context))
    }
    val state by trackListViewModel.uiState.collectAsStateWithLifecycle()

    if (state.isLoading) {
        MPLog.d(
            Constant.TrackList.CLASS_NAME,
            "TrackListScreen",
            Constant.TrackList.TAG,
            "loading data"
        )
        LoadingScreen()
    }
    if (state.isError) {
        MPLog.w(
            Constant.TrackList.CLASS_NAME,
            "TrackListScreen",
            Constant.TrackList.TAG,
            "could not fetch data"
        )
        ErrorScreen()
    }
    if (state.dataList.isNotEmpty()) {
        MPLog.d(
            Constant.TrackList.CLASS_NAME,
            "TrackListScreen",
            Constant.TrackList.TAG,
            "loading data success ${state.dataList.size}"
        )
        Box {
            ListComponent(
                dataList = state.dataList.map { it.toItemData() },
                isEndReached = state.isEndReached,
                isNextItemLoading = state.iNextItemsLoading,
                selectedItem = state.currentSelectedItem?.toItemData(),
                onListItemClick = {
                    trackListViewModel.onEvent(TrackListUiEvent.ClickSong(state.dataList.first { uiAudio -> uiAudio.id == it.id }, context))
                }
            ) {
                MPLog.d(
                    Constant.TrackList.CLASS_NAME,
                    "TrackListScreen",
                    Constant.TrackList.TAG,
                    "load more data "
                )
                trackListViewModel.onEvent(TrackListUiEvent.LoadNextData)
            }
            if (state.currentSelectedItem != null) {
                BottomBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    currentPlayingSong = state.currentSelectedItem!!,
                    isPlaying = state.isPlaying,
                    onPreviousButtonClicked = {
                        trackListViewModel.onEvent(TrackListUiEvent.PlayPreviousSong(context))
                    },
                    onNextButtonClick = {
                        trackListViewModel.onEvent(TrackListUiEvent.PlayNextSong(context))
                    },
                    onPlayButtonClick = {
                        trackListViewModel.onEvent(TrackListUiEvent.PlayOrPause(context))
                    },
                    onViewClicked = {
                        navigateToTrackDetail(state.currentSelectedItem!!.id)
                    }
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    currentPlayingSong: UiAudio,
    isPlaying: Boolean = false,
    onPreviousButtonClicked: () -> Unit,
    onNextButtonClick: () -> Unit,
    onPlayButtonClick: () -> Unit,
    onViewClicked: () -> Unit
) {
    MPLog.i(
        Constant.TrackList.CLASS_NAME,
        "BottomBar",
        Constant.TrackList.TAG,
        "display it with $currentPlayingSong"
    )
    BottomAppBar(modifier = modifier
        .clip(RoundedCornerShape(24.dp))
        .height(70.dp)
        .clickable {
            onViewClicked()
        }
        .scrollable(orientation = Orientation.Horizontal, state = rememberScrollableState {
            if (it > 5) {
                onViewClicked()
            }
            it
        }), tonalElevation = 4.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .background(color = LightBlue)
                .fillMaxSize()
        ) {
            ItemImage(
                imageUri = currentPlayingSong.albumThumbnailUri,
                modifier = modifier
                    .weight(1F)
                    .clip(CircleShape)
                    .width(50.dp)
                    .height(50.dp)
            )
            Spacer(
                modifier = Modifier
                    .width(4.dp)
                    .weight(2F)
            )
            Text(
                text = currentPlayingSong.songName,
                maxLines = 1,
                modifier = modifier.weight(3F),
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(4F))
            Buttons(action = onPlayButtonClick, onNext = onNextButtonClick, isStopped = isPlaying) {
                onPreviousButtonClicked()
            }
        }
    }
}

@Composable
fun ItemImage(modifier: Modifier = Modifier, imageUri: Uri?) {
    imageUri?.let {
        AsyncImage(
            model = it.toString(),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    } ?: run {
        Image(
            painter = painterResource(id = R.drawable.baseline_audio_file_24),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun TitleText(modifier: Modifier = Modifier, text: String, isPlaying: Boolean = false) {
    if (isPlaying) {
        Text(
            text = text,
            modifier = modifier,
            maxLines = 1,
            color = LightBlue,
            fontWeight = FontWeight.Bold
        )
    } else {
        Text(
            text = text, modifier = modifier, maxLines = 1, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SubTitleText(modifier: Modifier = Modifier, text: String, isPlaying: Boolean = false) {
    if (isPlaying) {
        Text(text = text, modifier = modifier, maxLines = 1, color = LightBlue)
    } else {
        Text(text = text, modifier = modifier, maxLines = 1)
    }
}

@Composable
fun RegularText(modifier: Modifier = Modifier, text: String) {
    Text(text = text, modifier = modifier)
}

@Composable
fun Buttons(
    modifier: Modifier = Modifier,
    isStopped: Boolean = false,
    action: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        PreviousButton {
            MPLog.d(
                Constant.TrackList.CLASS_NAME,
                "Buttons",
                Constant.TrackList.TAG,
                "playPrevious song"
            )
            onPrevious()
        }
        Spacer(modifier = Modifier.width(16.dp))
        if (isStopped) {
            PauseButton {
                MPLog.d(
                    Constant.TrackList.CLASS_NAME,
                    "Buttons",
                    Constant.TrackList.TAG,
                    "pause currentSong"
                )
                action()
            }
        } else {
            PlayButton {
                MPLog.d(
                    Constant.TrackList.CLASS_NAME,
                    "Buttons",
                    Constant.TrackList.TAG,
                    "resume currentSong"
                )
                action()
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        NextButton(modifier = modifier.padding(end = 8.dp)) {
            MPLog.d(
                Constant.TrackList.CLASS_NAME,
                "Buttons",
                Constant.TrackList.TAG,
                "play next song"
            )
            onNext()
        }
    }
}