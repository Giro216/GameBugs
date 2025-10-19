package com.example.gamebugs.model

import kotlin.math.cos
import kotlin.math.sin

class RhinocerosBug(speedFactor: Float) : Bug(BugType.RHINOCEROS, speedFactor = speedFactor) {
    // Носорог двигается по кругу
    override fun move(screenWidth: Float, screenHeight: Float): BugState {
        val speed = type.speed * this.speedFactor
        state.movementPhase += 0.05f

        val circleRadius = 30f
        val centerX = state.position.first + cos(state.direction.toDouble()).toFloat() * speed * 0.5f
        val centerY = state.position.second + sin(state.direction.toDouble()).toFloat() * speed * 0.5f

        val newX = centerX + cos(state.movementPhase.toDouble()).toFloat() * circleRadius
        val newY = centerY + sin(state.movementPhase.toDouble()).toFloat() * circleRadius

        if (Math.random() < 0.1) {
            state.direction = (Math.random() * Math.PI * 2).toFloat()
        }

        val (checkedX, checkedY) = checkBoundaries(newX, newY, screenWidth, screenHeight)

        state = state.copy(
            position = Pair(checkedX, checkedY),
            movementPhase = state.movementPhase + 0.05f
        )
        return state
    }
    override fun onDamage(): BugState {
        state = state.copy(isAlive = false)
        return state
    }
    override fun getReward(): Int = type.basePoints
}