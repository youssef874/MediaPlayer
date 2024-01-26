package com.example.mediaplayer3.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface MediaPlayerDestinations{

    val route: String
}

object TrackListDest: MediaPlayerDestinations{
    override val route: String
        get() = "track_list"
}

object SplashScreenDest: MediaPlayerDestinations{
    override val route: String
        get() = "splash_screen"

}

object TrackDetailDest: MediaPlayerDestinations{
    override val route: String
        get() = "track_detail"

    const val ID_ARGS = "songId"

    val routeWithArgs = "$route/{$ID_ARGS}"

    val arguments = listOf(
        navArgument(ID_ARGS){
            type = NavType.LongType
        }
    )
}

object PlayListDest : MediaPlayerDestinations{
    override val route: String
        get() = "play_list"

    const val ID_ARGS = "songId"
    const val CAN_MODIFY = "canModify"

    val routeWithArgs = "$route/{$ID_ARGS}/{$CAN_MODIFY}"

    val arguments = listOf(
        navArgument(ID_ARGS){
            type = NavType.LongType
        },
        navArgument(CAN_MODIFY){
            type = NavType.BoolType
        }
    )

}