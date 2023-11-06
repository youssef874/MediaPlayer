package com.example.mediaplayer3.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mediaplayer3.R
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.ui.Constant
import com.example.mediaplayer3.ui.RequestPermissionDialog
import com.example.mediaplayer3.ui.RequestSinglePermission
import com.example.mediaplayer3.ui.theme.ItemBackground
import com.example.mediaplayer3.ui.theme.LightBlue
import com.example.mediaplayer3.viewModel.AudioListViewModel
import com.example.mediaplayer3.viewModel.data.TrackListUiState
import com.example.mediaplayer3.viewModel.data.UiEvent
import com.example.mplog.MPLogger

@Composable
fun TrackListDetail(
    audioViewModel: AudioListViewModel = viewModel(), navigateToTrackDetail: (songId: Long) -> Unit
) {
    Box {
        val trackListUiState: TrackListUiState by audioViewModel.uiState.collectAsState()
        val context = LocalContext.current

        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO
            else Manifest.permission.READ_EXTERNAL_STORAGE

        if (trackListUiState.isLoading) {
            MPLogger.i(
                Constant.TrackList.CLASS_NAME,
                "TrackListDetail",
                Constant.TrackList.TAG,
                "is loading"
            )
            Box {
                LoadingScreen()
                if (!trackListUiState.isPermissionGranted) {
                    MPLogger.w(
                        Constant.TrackList.CLASS_NAME,
                        "TrackListDetail",
                        Constant.TrackList.TAG,
                        "permission is not granted"
                    )
                    RequestSinglePermission(permission = permission, onPermissionGranted = {
                        LaunchedEffect(key1 = Unit) {
                            audioViewModel.onEvent(UiEvent.FetchData(context = it))
                        }
                    }, onPermissionDenied = {
                        audioViewModel.onEvent(UiEvent.NotifyPermissionNeeded)
                    })
                }
            }
        }
        if (trackListUiState.dataList.isNotEmpty()) {
            MPLogger.d(
                Constant.TrackList.CLASS_NAME,
                "TrackListDetail",
                Constant.TrackList.TAG,
                "data loaded to display ${trackListUiState.dataList.size}"
            )
            Box {
                AudioList(
                    audioList = trackListUiState.dataList, selectedItem = trackListUiState.currentSelectedItem
                ) {
                    MPLogger.d(
                        Constant.TrackList.CLASS_NAME,
                        "TrackListDetail",
                        Constant.TrackList.TAG,
                        "play $it"
                    )
                    audioViewModel.onEvent(UiEvent.PlaySong(it, context))
                }
            }
        }
        if (trackListUiState.error != null) {
            MPLogger.e(
                Constant.TrackList.CLASS_NAME,
                "TrackListDetail",
                Constant.TrackList.TAG,
                "cannot display song list"
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                ErrorScreen()
                if (trackListUiState.needShowDialogForPermission) {
                    MPLogger.w(
                        Constant.TrackList.CLASS_NAME,
                        "TrackListDetail",
                        Constant.TrackList.TAG,
                        "show alert dialog"
                    )
                    RequestPermissionDialog(permissions = listOf(permission))
                }
                if (ContextCompat.checkSelfPermission(
                        context, permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    LaunchedEffect(key1 = Unit) {
                        audioViewModel.onEvent(UiEvent.FetchData(context))
                    }
                }

            }
        }
        if (trackListUiState.currentSelectedItem != null) {
            BottomBar(currentPlayingSong = trackListUiState.currentSelectedItem!!,
                modifier = Modifier.align(Alignment.BottomCenter),
                onPlayButtonClick = {
                    audioViewModel.onEvent(
                        UiEvent.PauseOrResume(trackListUiState.currentSelectedItem!!, context)
                    )
                },
                onNextButtonClick = {
                    audioViewModel.onEvent(UiEvent.PlayNextSong(context))
                },
                isPlaying = trackListUiState.isPlaying,
                onPreviousButtonClicked = {
                    audioViewModel.onEvent(UiEvent.PlayPreviousSong(context))
                },
                onViewClicked = {
                    navigateToTrackDetail(trackListUiState.currentSelectedItem!!.id)
                })
        }
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
fun AudioList(
    modifier: Modifier = Modifier,
    audioList: List<UiAudio>,
    selectedItem: UiAudio? = null,
    onListItemClick: (UiAudio) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(audioList, key = {
            it.id
        }) { item: UiAudio ->
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
            } else if (item.id == audioList.last().id) {
                AudioItem(uiAudio = item) {
                    onListItemClick(item)
                }
                Spacer(modifier = Modifier.padding(bottom = 50.dp))
            } else {
                AudioItem(uiAudio = item) {
                    onListItemClick(item)
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