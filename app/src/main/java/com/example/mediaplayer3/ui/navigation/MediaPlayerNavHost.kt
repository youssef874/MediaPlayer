package com.example.mediaplayer3.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mediaplayer3.ui.screen.AudioDetailScreen
import com.example.mediaplayer3.ui.screen.PlayListScreen
import com.example.mediaplayer3.ui.screen.SplashScreen
import com.example.mediaplayer3.ui.screen.TrackListScreen
import com.example.mediaplayer3.viewModel.PlayListViewModel
import com.example.mediaplayer3.viewModel.SplashViewModel
import com.example.mediaplayer3.viewModel.TrackDetailViewModel
import com.example.mediaplayer3.viewModel.TrackListViewModel


@Composable
fun MediaPlayerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = SplashScreenDest.route,
        modifier = modifier
    ) {
        composable(route = SplashScreenDest.route) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(splashViewModel = viewModel) {
                navController.navigateToTrackList()
                viewModel.clear()
            }
        }

        composable(route = TrackListDest.route) {
            val viewModel: TrackListViewModel = hiltViewModel()
            TrackListScreen(trackListViewModel = viewModel) {
                navController.navigateToTrackDetail(it)
                viewModel.clear()
            }
        }
        composable(
            route = TrackDetailDest.routeWithArgs,
            arguments = TrackDetailDest.arguments,
            deepLinks = TrackDetailDest.deepLink,
            enterTransition = {
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
                val viewModel: TrackDetailViewModel = hiltViewModel()
                AudioDetailScreen(trackDetailViewModel = viewModel, songId = songId, onBack = {
                    navController.navigateUp()
                }, onNavigateToPlayListScreen = {
                    navController.navigateToPlayList(it, false)
                    viewModel.clear()
                })
            }
        }
        composable(
            route = PlayListDest.routeWithArgs,
            arguments = PlayListDest.arguments,
            enterTransition = {
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
            val songId = navBackStackEntry.arguments?.getLong(PlayListDest.ID_ARGS)
            val canModify = navBackStackEntry.arguments?.getBoolean(PlayListDest.CAN_MODIFY)
            if (canModify != null) {
                val viewModel: PlayListViewModel = hiltViewModel()
                PlayListScreen(
                    songId = songId,
                    canModify = canModify,
                    playListViewModel = viewModel,
                    onBack = { navController.navigateUp() })
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {

        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }

private fun NavHostController.navigateToTrackDetail(songId: Long) {
    this.navigateSingleTopTo("${TrackDetailDest.route}/$songId")
}

private fun NavHostController.navigateToTrackList() {
    this.navigateSingleTopTo(TrackListDest.route)
}

private fun NavHostController.navigateToPlayList(songId: Long, canModify: Boolean) {
    this.navigateSingleTopTo("${PlayListDest.route}/$songId/$canModify")
}