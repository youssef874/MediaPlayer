package com.example.mediaplayer3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.mediaplayer3.ui.navigation.MediaPlayerNavHost
import com.example.mediaplayer3.ui.theme.MediaPlayer3Theme
import com.example.mediaplayer3.viewModel.AudioListViewModel

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

    companion object {
        private const val CLASS_NAME = "MainActivity"
        private const val TAG = "APP"
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MediaPlayer3Theme {

    }
}