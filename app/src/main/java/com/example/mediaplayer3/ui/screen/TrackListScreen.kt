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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.mediaplayer3.ui.theme.ItemBackground
import com.example.mediaplayer3.ui.theme.LightBlue
import com.example.mediaplayer3.viewModel.TrackListViewModel
import com.example.mediaplayer3.viewModel.data.tracklist.TrackListUiEvent
import com.example.mplog.MPLogger


@Composable
fun TrackListScreen(trackListViewModel: TrackListViewModel = viewModel(),navigateToTrackDetail: (Long)->Unit) {
    MPLogger.i(
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
    val state by trackListViewModel.uiState.collectAsState()

    if (state.isLoading) {
        MPLogger.d(
            Constant.TrackList.CLASS_NAME,
            "TrackListScreen",
            Constant.TrackList.TAG,
            "loading data"
        )
        LoadingScreen()
    }
    if (state.isError) {
        MPLogger.w(
            Constant.TrackList.CLASS_NAME,
            "TrackListScreen",
            Constant.TrackList.TAG,
            "could not fetch data"
        )
        ErrorScreen()
    }
    if (state.dataList.isNotEmpty()) {
        MPLogger.d(
            Constant.TrackList.CLASS_NAME,
            "TrackListScreen",
            Constant.TrackList.TAG,
            "loading data success ${state.dataList.size}"
        )
        Box {
            TrackList(
                audioList = state.dataList,
                isEndReached = state.isEndReached,
                isNextItemLoading = state.iNextItemsLoading,
                selectedItem = state.currentSelectedItem,
                onListItemClick = {
                    trackListViewModel.onEvent(TrackListUiEvent.ClickSong(it, context))
                }
            ) {
                MPLogger.d(
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
fun TrackList(
    modifier: Modifier = Modifier,
    audioList: List<UiAudio>,
    isEndReached: Boolean,
    isNextItemLoading: Boolean,
    selectedItem: UiAudio? = null,
    onListItemClick: (UiAudio) -> Unit,
    loadNextItem: () -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(audioList.size, key = { audioList[it].id }) {
            val item = audioList[it]
            if (it >= audioList.size - 1 && !isEndReached && !isNextItemLoading) {
                LaunchedEffect(key1 = Unit) {
                    loadNextItem()
                }
            }
            if (selectedItem != null && selectedItem.id == item.id) {
                MPLogger.d(
                    Constant.TrackList.CLASS_NAME,
                    "AudioList",
                    Constant.TrackList.TAG,
                    "selectedItem: $item"
                )
                AudioItem(uiAudio = selectedItem, isPlaying = true) {
                    onListItemClick(item)
                }
            } else {
                AudioItem(uiAudio = item) {
                    onListItemClick(item)
                }
            }
            if (it >= audioList.size - 1 && isEndReached && !isNextItemLoading) {
                Spacer(modifier = Modifier.padding(bottom = 50.dp))
            } else if (isNextItemLoading) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    LoadingScreen()
                }
            }
        }
    }
}


@Composable
fun AudioItem(
    modifier: Modifier = Modifier,
    uiAudio: UiAudio,
    isPlaying: Boolean = false,
    onItemClicked: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(100.dp)
            .clickable {
                onItemClicked()
            }, elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .background(color = ItemBackground)
                .fillMaxSize()
        ) {
            ItemImage(
                imageUri = uiAudio.albumThumbnailUri,
                modifier = modifier
                    .height(50.dp)
                    .width(50.dp)
                    .weight(1F)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier.weight(3F)) {
                TitleText(
                    text = uiAudio.songName,
                    modifier = modifier.padding(8.dp),
                    isPlaying = isPlaying
                )
                SubTitleText(text = uiAudio.artistName, isPlaying = isPlaying)
            }
            Spacer(modifier = Modifier.weight(1F))
            val second = uiAudio.duration / 1000
            val minutes = second / 60
            val hours = minutes / 60
            val formattedDuration =
                String.format("%02d:%02d:%02d", hours, minutes % 60, second % 60)
            RegularText(text = formattedDuration)
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
    MPLogger.i(
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
            MPLogger.d(
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
                MPLogger.d(
                    Constant.TrackList.CLASS_NAME,
                    "Buttons",
                    Constant.TrackList.TAG,
                    "pause currentSong"
                )
                action()
            }
        } else {
            PlayButton {
                MPLogger.d(
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
            MPLogger.d(
                Constant.TrackList.CLASS_NAME,
                "Buttons",
                Constant.TrackList.TAG,
                "play next song"
            )
            onNext()
        }
    }
}