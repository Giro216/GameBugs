package com.example.gamebugs.model

data class Settings(
    var gameDifficult: Int = 3,
    var gameSpeed: Float = 1.0f,
    var maxBeetles: Int = 10,
    var bonusInterval: Int = 15,
    var roundDuration: Int = 60
)