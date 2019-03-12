package com.appeaser.nbateamviewer

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.mockwebserver.MockResponse
import kotlin.random.Random

val gson = Gson()

fun random500Error() = Random.nextInt(500, 599)
fun random400Error() = Random.nextInt(400, 499)

inline fun <reified T> fromJson(body: String): T {
    return gson.fromJson(body, object : TypeToken<T>() {}.type)
}

fun createResponse(responseCode: Int, body: String): MockResponse {
    return MockResponse().apply {
        setResponseCode(responseCode)
        setBody(body)
    }
}

val SUCCESS_RESPONSE_BODY = """
    [
  {
    "wins": 45,
    "losses": 20,
    "full_name": "Boston Celtics",
    "id": 1,
    "players": [
      {
        "id": 37729,
        "first_name": "Kadeem",
        "last_name": "Allen",
        "position": "SG",
        "number": 45
      },
      {
        "id": 30508,
        "first_name": "Aron",
        "last_name": "Baynes",
        "position": "C",
        "number": 46
      },
      {
        "id": 30847,
        "first_name": "Jabari",
        "last_name": "Bird",
        "position": "SG",
        "number": 26
      },
      {
        "id": 45455,
        "first_name": "Jaylen",
        "last_name": "Brown",
        "position": "G-F",
        "number": 7
      },
      {
        "id": 989,
        "first_name": "Gordon",
        "last_name": "Hayward",
        "position": "G-F",
        "number": 20
      },
      {
        "id": 12,
        "first_name": "Al",
        "last_name": "Horford",
        "position": "F-C",
        "number": 42
      },
      {
        "id": 6154,
        "first_name": "Kyrie",
        "last_name": "Irving",
        "position": "G",
        "number": 11
      },
      {
        "id": 12714,
        "first_name": "Shane",
        "last_name": "Larkin",
        "position": "PG",
        "number": 8
      },
      {
        "id": 1965,
        "first_name": "Greg",
        "last_name": "Monroe",
        "position": "F-C",
        "number": 55
      },
      {
        "id": 2444,
        "first_name": "Marcus",
        "last_name": "Morris",
        "position": "F",
        "number": 13
      },
      {
        "id": 12275,
        "first_name": "Abdel",
        "last_name": "Nader",
        "position": "PF",
        "number": 28
      },
      {
        "id": 31438,
        "first_name": "Semi",
        "last_name": "Ojeleye",
        "position": "F",
        "number": 37
      },
      {
        "id": 31126,
        "first_name": "Terry",
        "last_name": "Rozier",
        "position": "G",
        "number": 12
      },
      {
        "id": 24740,
        "first_name": "Marcus",
        "last_name": "Smart",
        "position": "G",
        "number": 36
      },
      {
        "id": 52651,
        "first_name": "Jayson",
        "last_name": "Tatum",
        "position": "F",
        "number": 0
      },
      {
        "id": 60058,
        "first_name": "Daniel",
        "last_name": "Theis",
        "position": "F-C",
        "number": 27
      },
      {
        "id": 52283,
        "first_name": "Guerschon",
        "last_name": "Yabusele",
        "position": "F",
        "number": 30
      }
    ]
  },
  {
    "wins": 20,
    "losses": 44,
    "full_name": "Brooklyn Nets",
    "id": 2,
    "players": [
      {
        "id": 802,
        "first_name": "Quincy",
        "last_name": "Acy",
        "position": "F",
        "number": 13
      },
      {
        "id": 53393,
        "first_name": "Jarrett",
        "last_name": "Allen",
        "position": "F-C",
        "number": 31
      },
      {
        "id": 233,
        "first_name": "DeMarre",
        "last_name": "Carroll",
        "position": "F",
        "number": 9
      },
      {
        "id": 5912,
        "first_name": "Allen",
        "last_name": "Crabbe",
        "position": "G-F",
        "number": 33
      },
      {
        "id": 415,
        "first_name": "Dante",
        "last_name": "Cunningham",
        "position": "F",
        "number": 44
      },
      {
        "id": 11989,
        "first_name": "Spencer",
        "last_name": "Dinwiddie",
        "position": "PG",
        "number": 8
      },
      {
        "id": 24692,
        "first_name": "Milton",
        "last_name": "Doyle",
        "position": "PG",
        "number": 14
      },
      {
        "id": 5967,
        "first_name": "Joe",
        "last_name": "Harris",
        "position": "G-F",
        "number": 12
      },
      {
        "id": 30838,
        "first_name": "Rondae",
        "last_name": "Hollis-Jefferson",
        "position": "F",
        "number": 24
      },
      {
        "id": 24860,
        "first_name": "Caris",
        "last_name": "LeVert",
        "position": "SF",
        "number": 22
      },
      {
        "id": 2109,
        "first_name": "Jeremy",
        "last_name": "Lin",
        "position": "G",
        "number": 7
      },
      {
        "id": 6330,
        "first_name": "Timofey",
        "last_name": "Mozgov",
        "position": "C",
        "number": 20
      },
      {
        "id": 37621,
        "first_name": "Jahlil",
        "last_name": "Okafor",
        "position": "C",
        "number": 4
      },
      {
        "id": 37752,
        "first_name": "D'Angelo",
        "last_name": "Russell",
        "position": "G",
        "number": 1
      },
      {
        "id": 24863,
        "first_name": "Nik",
        "last_name": "Stauskas",
        "position": "G-F",
        "number": 2
      },
      {
        "id": 32213,
        "first_name": "James",
        "last_name": "Webb III",
        "position": "SF",
        "number": 0
      },
      {
        "id": 37705,
        "first_name": "Isaiah",
        "last_name": "Whitehead",
        "position": "G",
        "number": 15
      }
    ]
  },
  {
    "wins": 24,
    "losses": 40,
    "full_name": "New York Knicks",
    "id": 3,
    "players": [
      {
        "id": 12606,
        "first_name": "Ron",
        "last_name": "Baker",
        "position": "G",
        "number": 31
      },
      {
        "id": 248,
        "first_name": "Michael",
        "last_name": "Beasley",
        "position": "F",
        "number": 8
      },
      {
        "id": 11734,
        "first_name": "Trey",
        "last_name": "Burke",
        "position": "PG",
        "number": 23
      },
      {
        "id": 24746,
        "first_name": "Damyean",
        "last_name": "Dotson",
        "position": "SG",
        "number": 21
      },
      {
        "id": 6179,
        "first_name": "Tim",
        "last_name": "Hardaway Jr.",
        "position": "G-F",
        "number": 3
      },
      {
        "id": 31411,
        "first_name": "Isaiah",
        "last_name": "Hicks",
        "position": "PF",
        "number": 4
      },
      {
        "id": 461,
        "first_name": "Jarrett",
        "last_name": "Jack",
        "position": "G",
        "number": 55
      },
      {
        "id": 6456,
        "first_name": "Enes",
        "last_name": "Kanter",
        "position": "C",
        "number": 0
      },
      {
        "id": 30792,
        "first_name": "Luke",
        "last_name": "Kornet",
        "position": "F-C",
        "number": 2
      },
      {
        "id": 301,
        "first_name": "Courtney",
        "last_name": "Lee",
        "position": "G-F",
        "number": 5
      },
      {
        "id": 44890,
        "first_name": "Emmanuel",
        "last_name": "Mudiay",
        "position": "PG",
        "number": 1
      },
      {
        "id": 61,
        "first_name": "Joakim",
        "last_name": "Noah",
        "position": "F-C",
        "number": 13
      },
      {
        "id": 60048,
        "first_name": "Frank",
        "last_name": "Ntilikina",
        "position": "PG",
        "number": 11
      },
      {
        "id": 3372,
        "first_name": "Kyle",
        "last_name": "O'Quinn",
        "position": "F-C",
        "number": 9
      },
      {
        "id": 44888,
        "first_name": "Kristaps",
        "last_name": "Porzingis",
        "position": "F-C",
        "number": 6
      },
      {
        "id": 1635,
        "first_name": "Lance",
        "last_name": "Thomas",
        "position": "G-F",
        "number": 42
      },
      {
        "id": 31111,
        "first_name": "Troy",
        "last_name": "Williams",
        "position": "G-F",
        "number": 0
      }
    ]
  }]
""".trimIndent()