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