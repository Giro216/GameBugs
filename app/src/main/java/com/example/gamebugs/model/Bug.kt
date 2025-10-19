package com.example.gamebugs.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.gamebugs.R

data class BugState(
    var position: Pair<Float, Float> = Pair(0f, 0f),
    var health: Int = 1,
    var isAlive: Boolean = true,
    var isVisible: Boolean = true,
    var direction: Float = 0f,
    var movementPhase: Float = 0f
)

enum class BugType(val imageRes: Int, val basePoints: Int, var speed: Float) {
    SPIDER(R.drawable.spider, 20, 3f),
    COCKROACH(R.drawable.cockroach, 35, 2f),
    RHINOCEROS(R.drawable.rhinoceros, 35, 0.00002f)
}

abstract class Bug(
    val type: BugType,
    var state: BugState = BugState(),
    var speedFactor: Float
) {
    abstract fun move(screenWidth: Float, screenHeight: Float): BugState
    abstract fun onDamage(): BugState
    abstract fun getReward(): Int

    fun isAlive(): Boolean = state.isAlive
    fun getPosition(): Pair<Float, Float> = state.position
    fun setRandomPosition(maxX: Int, maxY: Int) {
        state = state.copy(
            position = Pair(
                (80 until maxX - 80).random().toFloat(),
                (80 until maxY - 80).random().toFloat()
            ),
            direction = (0 until 360).random().toFloat() * (Math.PI.toFloat() / 180f),
            movementPhase = (0 until 100).random().toFloat()
        )
    }

    @Composable
    fun getImage(): Painter = painterResource(type.imageRes)

    protected fun checkBoundaries(newX: Float, newY: Float, screenWidth: Float, screenHeight: Float): Pair<Float, Float> {
        var x = newX
        var y = newY
        val bugSize = 80f

        // Отражение от границ
        if (x < bugSize/2) {
            x = bugSize/2
            state.direction = Math.PI.toFloat() - state.direction
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
}
