package com.example.gamebugs.model

object BugFactory {
    fun createSpiderBug(speedFactor: Float): Bug = SpiderBug(speedFactor)
    fun createCockroachBug(speedFactor: Float): Bug = CockroachBug(speedFactor)
    fun createRhinocerosBug(speedFactor: Float): Bug = RhinocerosBug(speedFactor)
    fun createBonusBug(): Bug = BonusBug()

    fun createRandomBug(gameSpeed: Float): Bug {
        return when ((1..3).random()) {
            1 -> createSpiderBug(gameSpeed)
            2 -> createCockroachBug(gameSpeed)
            else -> createRhinocerosBug(gameSpeed)
        }
    }
}