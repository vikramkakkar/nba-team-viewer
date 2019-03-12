package com.appeaser.nbateamviewer.domain.entity

import com.google.gson.annotations.SerializedName

/**
 *  {
 *      "id": 37729,
 *      "first_name": "Kadeem",
 *      "last_name": "Allen",
 *      "position": "SG",
 *      "number": 45
 *  }
 */
data class Player(

    val id: Int,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    val position: String,

    val number: Int
)