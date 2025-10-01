package com.example.gamebugs.model

// Фабрика для создания жуков
object BugFactory {
    fun createSpiderBug(): Bug = SpiderBug()
    fun createCockroachBug(): Bug = CockroachBug()
    fun createRhinocerosBug(): Bug = RhinocerosBug()

    fun createRandomBug(): Bug {
        return when ((1..3).random()) {
            1 -> createSpiderBug()
            2 -> createCockroachBug()
            else -> createRhinocerosBug()
        }
    }
}