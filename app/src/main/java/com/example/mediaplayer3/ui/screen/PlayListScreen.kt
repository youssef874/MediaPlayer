package com.example.mediaplayer3.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediaplayer3.R
import com.example.mediaplayer3.ui.AddItemComponent
import com.example.mediaplayer3.ui.Constant
import com.example.mediaplayer3.ui.ErrorScreen
import com.example.mediaplayer3.ui.LoadingScreen
import com.example.mediaplayer3.ui.listcomponent.ListComponent
import com.example.mediaplayer3.ui.theme.LightBlue
import com.example.mediaplayer3.ui.toItemData
import com.example.mediaplayer3.viewModel.PlayListViewModel
import com.example.mediaplayer3.viewModel.data.playlist.PlayListUiEvent
import com.example.mpcore.api.log.MPLog


@Composable
fun PlayListScreen(
    songId: Long?,
    canModify: Boolean,
    playListViewModel: PlayListViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    MPLog.i(
        Constant.PlayListScreen.CLASS_NAME,
        "PlayListScreen",
        Constant.PlayListScreen.TAG,
        "open screen with songId: $songId, toAdd: $canModify"
    )
    BackHandler {
        onBack()
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        playListViewModel.onEvent(PlayListUiEvent.LoadData(context))
    }
    val state by playListViewModel.uiState.collectAsStateWithLifecycle()
    Box {
        if (state.dataList == null) {
            MPLog.d(Constant.PlayListScreen.CLASS_NAME,"PlayListScreen",Constant.PlayListScreen.TAG,"loading playlist")
            LoadingScreen()
        } else if (state.dataList?.isEmpty() == true) {
            MPLog.d(Constant.PlayListScreen.CLASS_NAME,"PlayListScreen",Constant.PlayListScreen.TAG,"There no playlist")
            Text(
                text = stringResource(R.string.no_play_list_string), modifier = Modifier
                    .align(
                        Alignment.Center
                    )
                    .padding(start = 16.dp, end = 16.dp),
                fontWeight = FontWeight.Bold
            )
        } else if (state.isError) {
            MPLog.w(Constant.PlayListScreen.CLASS_NAME,"PlayListScreen",Constant.PlayListScreen.TAG,"an error occurs")
            ErrorScreen(content = stringResource(R.string.Failed_fetch_play_list_string))
        } else {
            MPLog.d(
                Constant.PlayListScreen.CLASS_NAME,
                "PlayListScreen",
                Constant.PlayListScreen.TAG,
                "display the existed playlist"
            )
            ListComponent(
                dataList = state.dataList?.map { it.toItemData() }!!,
                isEndReached = state.isEndReached,
                isNextItemLoading = state.isNextItemLoading,
                onListItemClick = {},
                loadNextItem = {
                    playListViewModel.onEvent(PlayListUiEvent.LoadNextData)
                }
            )
        }
        var isDialogVisible by rememberSaveable {
            mutableStateOf(false)
        }
        FloatingActionButton(
            onClick = { isDialogVisible = true },
            containerColor = LightBlue,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            Icon(Icons.Filled.Add, "Add playlist")
        }
        val defaultValue = if (state.dataList.isNullOrEmpty())
            "playList 1 "
        else
            "playlist ${state.dataList?.maxByOrNull { it.playListId }!!.playListId.plus(1)}"
        if (isDialogVisible) {
            MPLog.d(
                Constant.PlayListScreen.CLASS_NAME,
                "PlayListScreen",
                Constant.PlayListScreen.TAG,
                "display dialog"
            )
            AddItemComponent(
                itemName = stringResource(R.string.playlist_string),
                defaultValue = defaultValue,
                onAddClicked = {
                    MPLog.i(
                        Constant.PlayListScreen.CLASS_NAME,
                        "PlayListScreen",
                        Constant.PlayListScreen.TAG,
                        "add button clicked playListName: $it"
                    )
                    isDialogVisible = false
                    playListViewModel.onEvent(
                        PlayListUiEvent.AttachSongToPlayList(
                            context,
                            songId,
                            it
                        )
                    )
                },
                onDismissRequest = {
                    MPLog.i(
                        Constant.PlayListScreen.CLASS_NAME,
                        "PlayListScreen",
                        Constant.PlayListScreen.TAG,
                        "cancel button clicked "
                    )
                    isDialogVisible = false
                }
            )
        }
        if (state.isAudioAttachedToPlayList) {
            MPLog.d(
                Constant.PlayListScreen.CLASS_NAME,
                "PlayListScreen",
                Constant.PlayListScreen.TAG,
                "audio attached to playlist"
            )
            Toast.makeText(
                context,
                stringResource(R.string.added_song_toast_message), Toast.LENGTH_SHORT
            ).show()
            LaunchedEffect(key1 = true){
                playListViewModel.onEvent(PlayListUiEvent.SongAttachedTopPlayListEventReceived)
            }
        }
    }
}