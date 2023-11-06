package com.example.mediaplayer3.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mediaplayer3.ui.screen.TrackDetailScreen
import com.example.mediaplayer3.ui.screen.TrackListDetail
import com.example.mediaplayer3.viewModel.AudioListViewModel


@Composable
fun MediaPlayerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    audioViewModel: AudioListViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = TrackListDest.route,
        modifier = modifier
    ) {
        composable(route = TrackListDest.route) {
            TrackListDetail(audioViewModel = audioViewModel) {
                navController.navigateToTrackDetail(it)
            }
        }
        composable(
            route = TrackDetailDest.routeWithArgs,
            arguments = TrackDetailDest.arguments,enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(700)
                )
            }
        ) { navBackStackEntry ->
            val songId = navBackStackEntry.arguments?.getLong(TrackDetailDest.ID_ARGS)
            if (songId != null) {
                TrackDetailScreen(songId = songId){
                    navController.navigateUp()
                }
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }

private fun NavHostController.navigateToTrackDetail(songId: Long){
    this.navigateSingleTopTo("${TrackDetailDest.route}/$songId")
}