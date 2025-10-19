package com.example.gamebugs.model

import kotlin.math.cos
import kotlin.math.sin

class SpiderBug(speedFactor: Float) : Bug(BugType.SPIDER, speedFactor = speedFactor) {
    // Паук двигается прямолинейно
    override fun move(screenWidth: Float, screenHeight: Float): BugState {
        val speed = type.speed * this.speedFactor
        if (Math.random() < 0.02) {
            state.direction += (Math.random() - 0.5).toFloat() * 0.5f
        }

        val newX = state.position.first + cos(state.direction.toDouble()).toFloat() * speed
        val newY = state.position.second + sin(state.direction.toDouble()).toFloat() * speed

        val (checkedX, checkedY) = checkBoundaries(newX, newY, screenWidth, screenHeight)

        state = state.copy(
            position = Pair(checkedX, checkedY),
            movementPhase = state.movementPhase + 0.1f
        )
        return state
    }
    override fun onDamage(): BugState {
        state = state.copy(isAlive = false)
        return state
    }
    override fun getReward(): Int = type.basePoints
}