package com.example.gamebugs.model

import kotlin.math.cos
import kotlin.math.sin

class CockroachBug(speedFactor: Float) : Bug(BugType.COCKROACH, speedFactor = speedFactor) {

    override fun move(screenWidth: Float, screenHeight: Float): BugState {
        val speed = getAdjustedSpeed()
        val phaseSpeed = getPhaseSpeed()

        state.movementPhase += phaseSpeed + 0.1f

        val baseX = state.position.first + cos(state.direction) * speed
        val baseY = state.position.second + sin(state.direction) * speed
        val waveOffset = sin(state.movementPhase) * 20f

        val newX = baseX + cos(state.direction + Math.PI / 2) * waveOffset
        val newY = baseY + sin(state.direction + Math.PI / 2) * waveOffset

        val (checkedX, checkedY) = checkBoundaries(newX, newY, screenWidth, screenHeight)

        state = state.copy(
            position = Pair(checkedX, checkedY)
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
}