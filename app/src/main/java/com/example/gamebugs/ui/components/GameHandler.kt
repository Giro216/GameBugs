package com.example.gamebugs.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gamebugs.R
import com.example.gamebugs.model.Bug
import com.example.gamebugs.model.BugFactory
import com.example.gamebugs.ui.config.Screens
import com.example.gamebugs.ui.theme.GameBugsTheme
import kotlinx.coroutines.delay
import kotlin.math.max

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
fun GameHandler(
    navController: NavHostController,
    settings: Settings,  // Принимаем настройки
    player: Player       // Принимаем игрока
) {
    val configuration = LocalConfiguration.current
    var totalScore by remember { mutableIntStateOf(0) }
    var gameState by remember { mutableStateOf("playing") } // playing, paused, gameOver

    var gameSessionKey by remember { mutableIntStateOf(0) }

    var screenSize by remember { mutableStateOf(Pair(configuration.screenWidthDp.toFloat(), configuration.screenHeightDp.toFloat())) }
    val screenWidth = screenSize.first
    val screenHeight = screenSize.second

    val penalty = 2

    fun createInitialBugs(): List<Bug> {
        return List(10) {
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

    fun handleMiss() {
        totalScore = max(0, totalScore - penalty)
    }

    fun handleHit(reward: Int) {
        totalScore += reward
    }

    @Composable
    fun onPaused() {
        // TODO переделать
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Обработка клика по пустому месту (промах)
                if (gameState == "playing") {
                    handleMiss()
                }
            }
    ) {
        println("Сессия: $gameSessionKey | Жуков: ${bugs.size}")

        Image(
            painter = painterResource(R.drawable.lawn2), // ваша картинка фона
            contentDescription = "Фон игры",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // или FillBounds, FillWidth, etc.
        )

        // Интерфейс игры
        Text(
            text = "Счет: $totalScore",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(30.dp)
                .background(
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.shapes.small
                ),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary
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
                    MaterialTheme.colorScheme.secondary,
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
                                handleHit(reward)
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

            val mockSettings = Settings(
                gameSpeed = 1.0f,
                maxBeetles = 5,
                bonusInterval = 15,
                roundDuration = 60
            )

            val mockPlayer = Player(
                name = "Тестовый Игрок",
                gender = "Муж",
                course = "3 курс",
                difficulty = 3,
                birthDate = System.currentTimeMillis(),
                zodiac = "Овен"
            )

            GameHandler(
                navController = mockNavController,
                settings = mockSettings,
                player = mockPlayer
            )
        }
    }
}