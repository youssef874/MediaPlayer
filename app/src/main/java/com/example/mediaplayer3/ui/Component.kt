package com.example.mediaplayer3.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
fun ErrorScreen(modifier: Modifier = Modifier, content: String? = null) {
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
            text = content ?: stringResource(R.string.default_failed_fech_string),
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

@Composable
fun AddItemComponent(
    modifier: Modifier = Modifier,
    itemName: String,
    defaultValue: String? = null,
    onAddClicked: (defaultValue: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var text by rememberSaveable {
        mutableStateOf(defaultValue?:"")
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.add_item_title_string, itemName),
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(start = 8.dp, bottom = 32.dp, top = 8.dp)
                )
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(text = stringResource(R.string.name_add_item_tabel, itemName)) },
                    singleLine = true,
                    modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 32.dp)
                )
                Row (
                    modifier = modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                ){
                    Button(onClick = onDismissRequest) {
                        Text(text = stringResource(R.string.cancel_string))
                    }
                    Spacer(modifier = modifier.weight(1F))
                    Button(onClick = { onAddClicked(text) }) {
                        Text(text = stringResource(R.string.add_string))
                    }
                }
            }
        }
    }
}