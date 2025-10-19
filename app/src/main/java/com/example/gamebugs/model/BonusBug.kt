package com.example.gamebugs.model

import kotlin.math.cos
import kotlin.math.sin

class BonusBug() : Bug(BugType.GOLDBUG, speedFactor = 1f) {
    override fun move(
        screenWidth: Float,
        screenHeight: Float
    ): BugState {
        val speed = getAdjustedSpeed()
        val phaseSpeed = getPhaseSpeed()
        if (Math.random() < 0.1 * speedFactor) {
            state.direction += (Math.random() - 0.5) + 0.1
        }

        val newX = state.position.first + cos(state.direction) * speed
        val newY = state.position.second + sin(state.direction) * speed

        val (checkedX, checkedY) = checkBoundaries(newX, newY, screenWidth, screenHeight)

        state = state.copy(
            position = Pair(checkedX, checkedY),
            movementPhase = state.movementPhase + phaseSpeed
        )
        return state
    }

    // TODO("add accelerometer logic")
    override fun onDamage(): BugState {
        state = state.copy(isAlive = false)
        return state
    }

}