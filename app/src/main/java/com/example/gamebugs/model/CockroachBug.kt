package com.example.gamebugs.model

import kotlin.math.cos
import kotlin.math.sin

class CockroachBug(speedFactor: Float) : Bug(BugType.COCKROACH, speedFactor = speedFactor) {
    // Таракан двигается по синусоиде
    override fun move(screenWidth: Float, screenHeight: Float): BugState {
        val speed = type.speed * this.speedFactor
        state.movementPhase += 0.1f

        val baseX = state.position.first + cos(state.direction.toDouble()).toFloat() * speed
        val baseY = state.position.second + sin(state.direction.toDouble()).toFloat() * speed
        val waveOffset = sin(state.movementPhase.toDouble()).toFloat() * 20f

        val newX = baseX + cos(state.direction.toDouble() + Math.PI / 2).toFloat() * waveOffset
        val newY = baseY + sin(state.direction.toDouble() + Math.PI / 2).toFloat() * waveOffset

        val (checkedX, checkedY) = checkBoundaries(newX, newY, screenWidth, screenHeight)

        state = state.copy(
            position = Pair(checkedX, checkedY),
            movementPhase = state.movementPhase + 0.1f
        )
        return state
    }
    override fun onDamage(): BugState {
        state = if (state.health > 1) {
            state.copy(health = state.health - 1)
        } else {
            state.copy(isAlive = false)
        }
        return state
    }
    override fun getReward(): Int = type.basePoints
}