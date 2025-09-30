package com.example.gamebugs.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gamebugs.R
import com.example.gamebugs.ui.config.Screens
import com.example.gamebugs.ui.theme.GameBugsTheme
import kotlinx.coroutines.delay
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
    RHINOCEROS(R.drawable.rhinoceros, 35, 0.3f)
}

// Основной класс Bug
abstract class Bug(
    val type: BugType,
    var state: BugState = BugState()
) {
    abstract fun move(screenWidth: Float, screenHeight: Float): BugState
    abstract fun onDamage(): BugState
    abstract fun getReward(): Int

    // TODO дописать логику штрафов за промахи

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
        if (Math.random() < 0.01) {
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

@Composable
fun BugItem(
    bug: Bug,
    onBugSquashed: (Int) -> Unit,
    screenWidth: Float,
    screenHeight: Float,
    modifier: Modifier = Modifier
) {
    // Независимые состояния для каждого жука
    var position by remember { mutableStateOf(bug.getPosition()) }
    var isAlive by remember { mutableStateOf(bug.isAlive()) }
    var health by remember { mutableIntStateOf(bug.state.health) }
    val bugSize = 80.dp

//     Анимация движения
    LaunchedEffect(bug) {
        while (true) {
            delay(16) // ~60 FPS
            if (isAlive) {
                bug.move(screenWidth, screenHeight)
                    position = bug.getPosition()
            } else {
                break
            }
        }
    }

    LaunchedEffect(bug) {
        position = bug.getPosition()
        isAlive = bug.isAlive()
        health = bug.state.health
    }

    // Анимация исчезновения
    val scale by animateFloatAsState(
        targetValue = if (isAlive) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    // Показываем пока не исчезнет полностью
    if (scale > 0.01f) {
        Image(
            painter = bug.getImage(),
            contentDescription = "жук",
            modifier = modifier
                .size(bugSize)
                .offset(x = position.first.dp, y = position.second.dp)
                .scale(scale)
                .clickable {
                    if (isAlive) {
                        bug.onDamage()
                        isAlive = bug.isAlive()
                        health = bug.state.health

                        if (!isAlive) {
                            onBugSquashed(bug.getReward())
                        }
                    }
                }
        )
    }
}

// TODO использовать настройки из главного меню
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun GameHandler(navController: NavHostController) {
    val configuration = LocalConfiguration.current
    var totalScore by remember { mutableIntStateOf(0) }
    var gameState by remember { mutableStateOf("playing") } // playing, paused, gameOver

    var gameSessionKey by remember { mutableIntStateOf(0) }

    var screenSize by remember { mutableStateOf(Pair(configuration.screenWidthDp.toFloat(), configuration.screenHeightDp.toFloat())) }
    val screenWidth = screenSize.first
    val screenHeight = screenSize.second

    fun createInitialBugs(): List<Bug> {
        return List(5) {
            val bug = BugFactory.createRandomBug()
            bug.setRandomPosition(
                configuration.screenWidthDp - 80,
                configuration.screenHeightDp - 80
            )
            bug
        }
    }

    // Используем ключ в remember для принудительного пересоздания
    var bugs by remember(gameSessionKey) {
        mutableStateOf(createInitialBugs())
    }

    fun restartGame() {
        totalScore = 0
        gameSessionKey++
        gameState = "playing"

        bugs = createInitialBugs()
    }

    @Composable
    fun onPaused() {
        // TODO вылетает
        // Добавляем небольшую задержку для стабильности навигации
        LaunchedEffect(Unit) {
            delay(100)
            navController.navigate(Screens.MainMenu.route)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        println("Сессия: $gameSessionKey | Жуков: ${bugs.size}")

        // Интерфейс игры
        Text(
            text = "Счет: $totalScore",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(30.dp),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Управление игрой
        Image(
            painter = painterResource(android.R.drawable.ic_media_pause),
            contentDescription = "Pause",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 30.dp, horizontal = 10.dp)
                .size(60.dp)
                .clickable(){
                    gameState = "paused"
                }
        )

        // Кнопка перезапуска
        Text(
            text = "Новая игра",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .clickable { restartGame() }
                .background(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.shapes.small
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

        // Основная логика автомата
        when(gameState) {
            "playing" -> {
                bugs.forEachIndexed { index, bug ->
                    key("bug_${gameSessionKey}_$index") { // Уникальный ключ
                        BugItem(
                            bug = bug,
                            onBugSquashed = { reward ->
                                totalScore += reward
                            },
                            screenWidth = screenWidth,
                            screenHeight = screenHeight,
                            modifier = Modifier
                        )
                    }
                }
            }
            "paused" -> {
                onPaused()
            }

            "gameOver" -> {
                // TODO дописать выход из игры с сохранением результата
            }
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