package com.example.gamebugs.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.gamebugs.R

data class BugState(
    var position: Pair<Double, Double> = Pair(0.0, 0.0),
    var health: Int = 1,
    var isAlive: Boolean = true,
    var isVisible: Boolean = true,
    var direction: Double = 0.0,
    var movementPhase: Double = 0.0
)

enum class BugType(val imageRes: Int, val basePoints: Int, var speed: Double) {
    SPIDER(R.drawable.spider, 10, 3.5),
    COCKROACH(R.drawable.cockroach, 20, 0.5),
    RHINOCEROS(R.drawable.rhinoceros, 15, 4.5),
    BONUSBUG(R.drawable.mickey_mouse_bonus, 20, 8.0),
    GOLDBUG(R.drawable.goldbug, 1, 8.0)
}

abstract class Bug(
    val type: BugType,
    var state: BugState = BugState(),
    var speedFactor: Float
) {
    abstract fun move(screenWidth: Float, screenHeight: Float): BugState
    abstract fun onDamage(): BugState

    fun isAlive(): Boolean = state.isAlive
    fun getPosition(): Pair<Double, Double> = state.position
    fun setRandomPosition(maxX: Int, maxY: Int) {
        state = state.copy(
            position = Pair(
                (80 until maxX - 80).random().toDouble(),
                (80 until maxY - 80).random().toDouble()
            ),
            direction = (0 until 360).random() * (Math.PI / 180f),
            movementPhase = (0 until 100).random().toDouble()
        )
    }
    open fun moveWithGravity(screenWidth: Float, screenHeight: Float, gravityX: Float, gravityY: Float): BugState {
        val gravityStrength = 3.0f

        val newX = state.position.first + gravityX * gravityStrength
        val newY = state.position.second + gravityY * gravityStrength

        val (checkedX, checkedY) = checkBoundaries(newX, newY, screenWidth, screenHeight)

        state = state.copy(
            position = Pair(checkedX, checkedY)
        )
        return state
    }
    @Composable
    fun getImage(): Painter = painterResource(type.imageRes)

    open fun getReward(): Int = type.basePoints
    protected fun checkBoundaries(newX: Double, newY: Double, screenWidth: Float, screenHeight: Float): Pair<Double, Double> {
        //TODO отследить где жук отталкивается от левой стены
        var x = newX
        var y = newY
        val bugSize = 80.0

        if (x < bugSize/2) {
            x = bugSize/2
            state.direction = Math.PI - state.direction
        } else if (x > screenWidth - bugSize/2) {
            x = screenWidth - bugSize/2
            state.direction = Math.PI.toFloat() - state.direction
        }

        if (y < bugSize/2) {
            y = bugSize/2
            state.direction = -state.direction
        } else if (y > screenHeight - bugSize/2) {
            y = screenHeight - bugSize/2
            state.direction = -state.direction
        }

        return Pair(x, y)
    }
    protected fun getAdjustedSpeed(): Double = type.speed * speedFactor
    protected fun getPhaseSpeed(): Float = 0.05f * speedFactor
}
