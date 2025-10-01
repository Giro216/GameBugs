package com.example.gamebugs.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.gamebugs.R
import kotlin.math.cos
import kotlin.math.sin

// Data class для состояния жука
data class BugState(
    var position: Pair<Float, Float> = Pair(0f, 0f),
    var health: Int = 1,
    var isAlive: Boolean = true,
    var isVisible: Boolean = true,
    var direction: Float = 0f, // направление движения в радианах
    var movementPhase: Float = 0f // фаза для периодического движения
)

// Enum для типов жуков
enum class BugType(val imageRes: Int, val basePoints: Int, val speed: Float) {
    SPIDER(R.drawable.spider, 20, 3f),
    COCKROACH(R.drawable.cockroach, 35, 2f),
    RHINOCEROS(R.drawable.rhinoceros, 35, 0.2f)
}

// Основной класс Bug
abstract class Bug(
    val type: BugType,
    var state: BugState = BugState()
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
            direction = (0 until 360).random().toFloat() * (Math.PI.toFloat() / 180f), // случайное направление
            movementPhase = (0 until 100).random().toFloat() // случайная фаза
        )
    }

    @Composable
    fun getImage(): Painter = painterResource(type.imageRes)

    // Общая функция для проверки столкновений с границами
    protected fun checkBoundaries(newX: Float, newY: Float, screenWidth: Float, screenHeight: Float): Pair<Float, Float> {
        var x = newX
        var y = newY
        val bugSize = 80f

        // Отражение от границ с учетом размера жука (80dp)
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

// Конкретные классы
class SpiderBug : Bug(BugType.SPIDER) {
    // Паук двигается прямолинейно с случайными изменениями направления
    override fun move(screenWidth: Float, screenHeight: Float): BugState {
        if (Math.random() < 0.02) { // 2% шанс изменить направление
            state.direction += (Math.random() - 0.5).toFloat() * 0.5f
        }

        val newX = state.position.first + cos(state.direction.toDouble()).toFloat() * type.speed
        val newY = state.position.second + sin(state.direction.toDouble()).toFloat() * type.speed

        // Проверяем границы
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

class CockroachBug : Bug(BugType.COCKROACH) {
    // Таракан двигается по синусоиде
    override fun move(screenWidth: Float, screenHeight: Float): BugState {
        state.movementPhase += 0.1f

        val baseX = state.position.first + cos(state.direction.toDouble()).toFloat() * type.speed
        val baseY = state.position.second + sin(state.direction.toDouble()).toFloat() * type.speed
        val waveOffset = sin(state.movementPhase.toDouble()).toFloat() * 20f

        val newX = baseX + cos(state.direction.toDouble() + Math.PI / 2).toFloat() * waveOffset
        val newY = baseY + sin(state.direction.toDouble() + Math.PI / 2).toFloat() * waveOffset

        // Проверяем границы
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

class RhinocerosBug : Bug(BugType.RHINOCEROS) {
    // Носорог двигается по круговой траектории
    override fun move(screenWidth: Float, screenHeight: Float): BugState {
        state.movementPhase += 0.05f

        val circleRadius = 50f
        val centerX = state.position.first + cos(state.direction.toDouble()).toFloat() * type.speed * 0.5f
        val centerY = state.position.second + sin(state.direction.toDouble()).toFloat() * type.speed * 0.5f

        val newX = centerX + cos(state.movementPhase.toDouble()).toFloat() * circleRadius
        val newY = centerY + sin(state.movementPhase.toDouble()).toFloat() * circleRadius

        // Периодически меняем основное направление
        if (Math.random() < 0.1) {
            state.direction = (Math.random() * Math.PI * 2).toFloat()
        }

        // Проверяем границы
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