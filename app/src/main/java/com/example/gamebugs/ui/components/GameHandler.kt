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
import androidx.compose.runtime.mutableLongStateOf
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
    modifier: Modifier = Modifier,
    bug: Bug,
    onBugSquashed: (Int) -> Unit,
    screenWidth: Float,
    screenHeight: Float,
    gameSpeed: Float = 1.0f,

) {
    var position by remember { mutableStateOf(bug.getPosition()) }
    var isAlive by remember { mutableStateOf(bug.isAlive()) }
    var health by remember { mutableIntStateOf(bug.state.health) }
    val bugSize = 80.dp

    LaunchedEffect(bug, gameSpeed) {
        while (true) {
            delay(16)
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

    val scale by animateFloatAsState(
        targetValue = if (isAlive) 1f else 0f,
        animationSpec = tween(durationMillis = 100)
    )

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

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun GameHandler(
    navController: NavHostController,
    settings: Settings,
    player: Player
) {
    val configuration = LocalConfiguration.current
    var totalScore by remember { mutableIntStateOf(0) }
    var gameState by remember { mutableStateOf("playing") }
    var gameSessionKey by remember { mutableIntStateOf(0) }
    var gameTime by remember { mutableIntStateOf(0) }
    var roundTimeLeft by remember { mutableIntStateOf(settings.roundDuration) }
    var lastUpdateTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var screenSize by remember { mutableStateOf(Pair(configuration.screenWidthDp.toFloat(), configuration.screenHeightDp.toFloat())) }
    val screenWidth = screenSize.first
    val screenHeight = screenSize.second

    val bonusInterval = settings.bonusInterval
    val roundDuration = settings.roundDuration

    var penalty by remember {
        mutableIntStateOf(
        when (settings.gameDifficult){
            1 -> 2
            2 -> 4
            3 -> 10
            4 -> 15
            5 -> 30
            else -> 1
        })
    }

    fun createInitialBugs(): List<Bug> {
        return List(settings.maxBeetles) {
            val bug = BugFactory.createRandomBug(settings.gameSpeed)
            bug.setRandomPosition(
                configuration.screenWidthDp - 80,
                configuration.screenHeightDp - 80
            )
            bug
        }
    }

    var bugs by remember(gameSessionKey) {
        mutableStateOf(createInitialBugs())
    }

    LaunchedEffect(gameState, gameSessionKey) {
        if (gameState == "playing") {
            lastUpdateTime = System.currentTimeMillis()

            while (gameState == "playing") {
                val currentTime = System.currentTimeMillis()
                val elapsedSeconds = (currentTime - lastUpdateTime) / 1000

                if (elapsedSeconds >= 1) {
                    gameTime++
                    roundTimeLeft = roundDuration - gameTime
                    lastUpdateTime = currentTime

//                    if (gameTime % bonusInterval == 0 && gameTime > 0) {
//                        spawnBonusBug()
//                    }

                    val allBugsDead = bugs.all { !it.isAlive() }
                    if (gameTime >= roundDuration || allBugsDead) {
                        gameState = "gameOver"
                        break
                    }
                }

                delay(16)
            }
        }
    }

    fun spawnNewBug() {
        if (bugs.size < settings.maxBeetles) {
            val newBug = BugFactory.createRandomBug(settings.gameSpeed)
            newBug.setRandomPosition(
                configuration.screenWidthDp - 80,
                configuration.screenHeightDp - 80
            )
            bugs = bugs + newBug
        }
    }

    fun restartGame() {
        totalScore = 0
        gameSessionKey++
        gameState = "playing"
        gameTime = 0
        roundTimeLeft = settings.roundDuration
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
        LaunchedEffect(Unit) {
            delay(100)
            navController.navigate(Screens.MainMenu.route)
        }
    }

    @Composable
    fun onGameOver() {
        LaunchedEffect(Unit) {
            delay(3000)
            navController.navigate(Screens.MainMenu.route)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Игра окончена!\nФинальный счет: $totalScore",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onError,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
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
                if (gameState == "playing") {
                    handleMiss()
                }
            }
    ) {
        // Фон
        Image(
            painter = painterResource(R.drawable.lawn2),
            contentDescription = "Фон игры",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Время: ${roundTimeLeft}с",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(vertical = 30.dp)
                .background(
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.shapes.small
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = "Счет: $totalScore",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(30.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.shapes.small
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Image(
            painter = painterResource(android.R.drawable.ic_media_pause),
            contentDescription = "Pause",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = 30.dp, horizontal = 10.dp)
                .size(60.dp)
                .clickable {
                    gameState = "paused"
                }
        )

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
                    key("bug_${gameSessionKey}_$index") {
                        BugItem(
                            bug = bug,
                            onBugSquashed = { reward ->
                                handleHit(reward)
                            },
                            screenWidth = screenWidth,
                            screenHeight = screenHeight,
                            gameSpeed = settings.gameSpeed,
                            modifier = Modifier
                        )
                    }
                }
            }
            "paused" -> {
                onPaused()
            }
            "gameOver" -> {
                onGameOver()
            }
        }
    }
}

private fun spawnBonusBug() {
    TODO("Not yet implemented")
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
                gameSpeed = 1.5f,
                maxBeetles = 8,
                bonusInterval = 10,
                roundDuration = 120
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