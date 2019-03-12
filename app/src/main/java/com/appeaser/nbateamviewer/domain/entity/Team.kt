package com.appeaser.nbateamviewer.domain.entity

import com.google.gson.annotations.SerializedName

/**
 *  {
 *      "id": 1,
 *      "full_name": "Boston Celtics",
 *      "wins": 45,
 *      "losses": 20,
 *      "players": [{
 *          ...
 *          ...
 *      }]
 *  }
 */
data class Team(

    val id: Int,

    @SerializedName("full_name")
    val fullName: String,

    val wins: Int,

    val losses: Int,

    val players: List<Player>
)