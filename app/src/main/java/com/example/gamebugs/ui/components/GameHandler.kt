package com.example.gamebugs.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gamebugs.R
import com.example.gamebugs.ui.theme.GameBugsTheme


// Data class для состояния жука
data class BugState(
    var position: Pair<Float, Float> = Pair(0f, 0f),
    var health: Int = 1,
    var isAlive: Boolean = true,
    var isVisible: Boolean = true
)

// Enum для типов жуков
enum class BugType(val imageRes: Int, val basePoints: Int, val speed: Int) {
    SPIDER(R.drawable.spider, 10, 2),
    COCKROACH(R.drawable.cockroach, 5, 1),
    RHINOCEROS(R.drawable.rhinoceros, 50, 4)
}

// Обновите класс Bug
abstract class Bug(
    val type: BugType,
    var state: BugState = BugState()
) {
    abstract fun move(): BugState
    abstract fun onDamage(): BugState
    abstract fun getReward(): Int

    fun isAlive(): Boolean = state.isAlive
    fun getPosition(): Pair<Float, Float> = state.position
    fun setRandomPosition(maxX: Int, maxY: Int) {
        state = state.copy(
            position = Pair(
                (0 until maxX).random().toFloat(),
                (0 until maxY).random().toFloat()
            )
        )
    }

    // Получаем image в @Composable функции
    @Composable
    fun getImage(): Painter = painterResource(type.imageRes)
}

// Конкретные классы (без Painter в конструкторе)
class SpiderBug : Bug(BugType.SPIDER) {
    override fun move(): BugState {
        val newPosition = Pair(state.position.first + type.speed, state.position.second)
        state = state.copy(position = newPosition)
        return state
    }
    override fun onDamage(): BugState = state.copy(isAlive = false)
    override fun getReward(): Int = type.basePoints
}

class CockroachBug : Bug(BugType.COCKROACH) {
    override fun move(): BugState {
        val newPosition = Pair(state.position.first + type.speed, state.position.second)
        state = state.copy(position = newPosition)
        return state
    }
    override fun onDamage(): BugState {
        return if (state.health > 1) {
            state.copy(health = state.health - 1)
        } else {
            state.copy(isAlive = false)
        }
    }
    override fun getReward(): Int = type.basePoints * 2
}

class RhinocerosBug : Bug(BugType.RHINOCEROS) {
    override fun move(): BugState {
        val newX = state.position.first + type.speed
        val newY = state.position.second + kotlin.math.sin(newX * 0.1).toFloat()
        state = state.copy(position = Pair(newX, newY))
        return state
    }
    override fun onDamage(): BugState = state.copy(isAlive = false)
    override fun getReward(): Int = type.basePoints * 3
}

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

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun GameHandler(navController: NavHostController) {
    val bugFactory = BugFactory
    val configuration = LocalConfiguration.current

    var bugs by remember {
        mutableStateOf(
            Array(2) {
                val bug = bugFactory.createRandomBug()
                bug.setRandomPosition(
                    configuration.screenWidthDp - 80,
                    configuration.screenHeightDp - 80
                )
                bug
            }
        )
    }


    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        bugs.forEach {
            Image(
                painter = it.getImage(),
                contentDescription = "жук",
                modifier = Modifier.size(80.dp).
                    offset(
                        x = it.getPosition().first.dp,
                        y = it.getPosition().second.dp
                    )
            )
        }
    }
}

@Preview
@Composable
fun GameHandlerPreview() {
    GameBugsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val mockNavController = rememberNavController()
            GameHandler(navController = mockNavController)
        }
    }
}