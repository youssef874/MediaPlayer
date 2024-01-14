package com.example.mediaplayer3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.mediaplayer3.ui.listcomponent.ItemData
import com.example.mediaplayer3.ui.listcomponent.ListComponent
import com.example.mediaplayer3.ui.navigation.MediaPlayerNavHost
import com.example.mediaplayer3.ui.theme.MediaPlayer3Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaPlayer3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    MediaPlayerNavHost(navController = navController)
                }
            }
        }
    }
}

val list = listOf(
    ItemData(
        title = "title",
        subtitle = "subtitle",
        endText = "10000",
        id = 1L
    ), ItemData(title = "title2", subtitle = "subtitle2", endText = "1000000", id = 2L),
    ItemData(title = "title3", subtitle = "subtitle3", endText = "2000000", id = 3L),
    ItemData(title = "title4", subtitle = "subtitle4", endText = "3000000", id = 4L),
    ItemData(title = "title5", subtitle = "subtitle5", endText = "4000000", id = 5L),
    ItemData(title = "title6", subtitle = "subtitle6", endText = "5000000", id = 6L),
    ItemData(title = "title7", subtitle = "subtitle7", endText = "2050000", id = 7L),
    ItemData(title = "title8", subtitle = "subtitle8", endText = "2000500", id = 8L)
)
@Preview
@Composable
fun Preview() {
    MediaPlayer3Theme {

        ListComponent(
            dataList = list,
            isEndReached = true,
            isNextItemLoading = false,
            onListItemClick = {},
            selectedItem = list.first { it.id == 2L }
        ) {

        }
    }
}