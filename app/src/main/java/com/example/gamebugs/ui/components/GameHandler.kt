package com.example.gamebugs.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gamebugs.R
import com.example.gamebugs.dataBase.model.PlayerEntity
import com.example.gamebugs.dataBase.repository.MockPlayerRepository
import com.example.gamebugs.dataBase.repository.MockRecordsRepository
import com.example.gamebugs.model.Bug
import com.example.gamebugs.model.BugFactory
import com.example.gamebugs.model.BugType
import com.example.gamebugs.model.viewModel.CurrencyViewModel
import com.example.gamebugs.model.viewModel.GameViewModel
import com.example.gamebugs.model.viewModel.PlayerViewModel
import com.example.gamebugs.network.repository.MockMetalCurrencyRepository
import com.example.gamebugs.ui.config.Screens
import com.example.gamebugs.ui.theme.GameBugsTheme
import kotlinx.coroutines.delay
import kotlin.math.max

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun GameHandler(
    navController: NavHostController,
    settings: Settings,
    player: PlayerEntity,
    gameViewModel: GameViewModel,
    playerViewModel: PlayerViewModel,
    currencyViewModel: CurrencyViewModel
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
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

    var isGravityEffectActive by remember { mutableStateOf(false) }
    var gravityEffectEndTime by remember { mutableLongStateOf(0L) }

    var accelerometerX by remember { mutableFloatStateOf(0f) }
    var accelerometerY by remember { mutableFloatStateOf(0f) }

    val audioAttributes = remember {
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
    }


    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()
    }


    var screamSoundId by remember { mutableIntStateOf(0) }
    var soundsLoaded by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        try {
            screamSoundId = soundPool.load(context, R.raw.falling_scream, 1)
            currencyViewModel.loadGoldPrice()
        } catch (e: Exception) {
            print(e.message)
        }
    }


    DisposableEffect(soundPool) {
        val listener = SoundPool.OnLoadCompleteListener { _, _, status ->
            if (status == 0) soundsLoaded = true
        }
        soundPool.setOnLoadCompleteListener(listener)


        onDispose {
            soundPool.unload(screamSoundId)
            soundPool.release()
        }
    }


    fun activateGravityEffect() {
        isGravityEffectActive = true
        gravityEffectEndTime = System.currentTimeMillis() + 5000
        if (soundsLoaded && screamSoundId != 0) {
            soundPool.play(screamSoundId, 0.5f, 0.5f, 1, 0, 1f)
        }
    }

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val sensorListener = remember {
        object : SensorEventListener {
            private var lastSensorUpdate = 0L
            override fun onSensorChanged(event: SensorEvent?) {
                val now = System.currentTimeMillis()
                if (now - lastSensorUpdate < 50L) return
                lastSensorUpdate = now
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && isGravityEffectActive) {
                    accelerometerX = - event.values[0]
                    accelerometerY = event.values[1]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(
            sensorListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME
        )

        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    BackHandler(enabled = gameState == "playing") {
        gameState = "paused"
    }

    BackHandler(enabled = gameState == "paused") {
        gameState = "playing"
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

    fun addBonusBug() {
        if (bugs.count { bug -> bug.isAlive() } < settings.maxBeetles) {
            if (bugs.none { bug -> bug.type == BugType.BONUSBUG && bug.isAlive() }){
                val bonusBug = BugFactory.createBonusBug(onBonusActivated = ::activateGravityEffect)
                bonusBug.setRandomPosition(
                    configuration.screenWidthDp - 80,
                    configuration.screenHeightDp - 80
                )
                bugs = bugs + bonusBug
            }
        }
    }

    fun addGoldBug(){
        if (bugs.count { bug -> bug.isAlive() } < settings.maxBeetles) {
            if (bugs.none { bug -> bug.type == BugType.GOLDBUG && bug.isAlive() }){
                val goldBug = BugFactory.createGoldBug(currencyViewModel.getGoldReward())
                goldBug.setRandomPosition(
                    configuration.screenWidthDp - 80,
                    configuration.screenHeightDp - 80
                )
                bugs = bugs + goldBug
            }
        }
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

                    val allBugsDead = bugs.all { !it.isAlive() }
                    if (gameTime >= roundDuration || allBugsDead) {
                        gameState = "gameOver"
                        break
                    }
                }

                if (isGravityEffectActive){
                    if (currentTime > gravityEffectEndTime){
                        isGravityEffectActive = false
                        accelerometerX = 0f
                        accelerometerY = 0f
                    }
                }
                delay(16)
            }
        }
    }

    var lastBonusInterval by remember { mutableIntStateOf(0) }
    var lastGoldenCockroachTime by remember { mutableIntStateOf(0) }
    LaunchedEffect(gameState, gameTime) {
        if (gameState == "playing") {
            val goldenInterval = 20
            val currentBonusInterval = gameTime / bonusInterval
            val currentGoldInterval = gameTime / goldenInterval

            if (gameTime > 0 && currentBonusInterval > lastBonusInterval && !isGravityEffectActive) {
                lastBonusInterval = currentBonusInterval
                addBonusBug()
            }

            if (gameTime > 0 && currentGoldInterval > lastGoldenCockroachTime) {
                lastGoldenCockroachTime = currentGoldInterval
                addGoldBug()
            }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = "ПАУЗА",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = "Счет: $totalScore",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = "Время: ${roundTimeLeft}с",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Button(
                    onClick = { gameState = "playing" }
                ) {
                    Text("Продолжить")
                }

                Button(
                    onClick = { restartGame() }
                ){
                    Text("Новая игра")
                }

                Button(
                    onClick = {
                        navController.popBackStack(Screens.MainMenu.route, false)
                    }
                ) {
                    Text("В главное меню")
                }
            }
        }
    }

    @Composable
    fun onGameOver() {
        LaunchedEffect(Unit) {
            delay(3000)
            gameViewModel.saveRecord(
                playerId = player.id,
                score = totalScore,
                difficulty = settings.gameDifficult,
                playerName = player.name
            )
            navController.popBackStack(Screens.MainMenu.route, inclusive = false)
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
                textAlign = TextAlign.Center
            )
        }
    }

    // Основная разметка
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

        if (gameState == "playing") {
            Text(
                text = "Время: ${roundTimeLeft}с",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
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
                    .statusBarsPadding()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.shapes.small
                    ),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Image(
            painter = painterResource(
                if (gameState == "paused") android.R.drawable.ic_media_play
                else android.R.drawable.ic_media_pause
            ),
            contentDescription = if (gameState == "paused") "Continue" else "Pause",
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(horizontal = 10.dp)
                .size(50.dp)
                .clickable {
                    when (gameState) {
                        "playing" -> gameState = "paused"
                        "paused" -> gameState = "playing"
                    }
                }
        )

        // Основная логика автомата
        when(gameState) {
            "playing" -> {
                bugs.forEachIndexed { index, bug ->
                    key("bug_${gameSessionKey}_$index") {
                        BugItem(
                            bug = bug,
                            onBugSquashed = { reward -> handleHit(reward) },
                            screenWidth = screenWidth,
                            screenHeight = screenHeight,
                            modifier = Modifier,
                            gravityX = if (isGravityEffectActive) accelerometerX else 0f,
                            gravityY = if (isGravityEffectActive) accelerometerY else 0f
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

@SuppressLint("ViewModelConstructorInComposable")
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

            val mockPlayer = PlayerEntity(
                name = "Тестовый Игрок",
                gender = "Муж",
                course = "3 курс",
                difficulty = 3,
                birthDate = System.currentTimeMillis(),
                zodiac = "Овен"
            )

            val gameViewModel = GameViewModel(
                repository = MockRecordsRepository()
            )

            val playerViewModel = PlayerViewModel(
                repository = MockPlayerRepository()
            )

            val currencyViewModel = CurrencyViewModel(
                repository = MockMetalCurrencyRepository()
            )

            GameHandler(
                navController = mockNavController,
                settings = mockSettings,
                player = mockPlayer,
                gameViewModel = gameViewModel,
                playerViewModel = playerViewModel,
                currencyViewModel = currencyViewModel
            )
        }
    }
}

