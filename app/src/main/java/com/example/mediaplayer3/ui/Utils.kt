package com.example.mediaplayer3.ui

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.example.mediaplayer3.R
import com.example.mediaplayer3.domain.entity.UiAudio

@Composable
fun RequestSinglePermission(
    permission: String,
    onPermissionGranted: @Composable (Context) -> Unit,
    onPermissionDenied: @Composable function0
) {
    val context = LocalContext.current
    var isPermissionGranted by rememberSaveable {
        mutableStateOf(false)
    }
    val launcherState =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            isPermissionGranted = isGranted
        }
    if (
        ContextCompat.checkSelfPermission(
            context, permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        SideEffect {
            launcherState.launch(permission)
        }
    } else {
        isPermissionGranted = true
    }
    if (isPermissionGranted) {
        onPermissionGranted(context)
    } else {
        onPermissionDenied()
    }
}

@Composable
fun RequestPermissionDialog(
    modifier: Modifier = Modifier,
    permissions: List<String>,
    onDismissRequest: function0? = null,
    onConfirm: function0? = null
) {
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDismissRequest?.invoke()
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onConfirm?.invoke()
                }) {
                    Text("OK")
                }
            },
            title = {
                Text(text = stringResource(R.string.permission_needed_string))
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.we_need_to_give_list_of_songs_string,
                        permissions.joinToString(",")
                    )
                )
            },
            modifier = modifier
        )
    }
}

fun Int.timeFormatter(): String {
    val second = this / 1000
    val minutes = second / 60
    val hours = minutes / 60
    return String.format("%02d:%02d:%02d", hours, minutes % 60, second % 60)
}

typealias function0 = () -> Unit

typealias uiAudioFun0 = (UiAudio, Context) -> Unit