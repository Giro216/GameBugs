package com.example.gamebugs.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.gamebugs.model.Bug
import kotlinx.coroutines.delay

@Composable
fun BugItem(
    modifier: Modifier = Modifier,
    bug: Bug,
    onBugSquashed: (Int) -> Unit,
    screenWidth: Float,
    screenHeight: Float,
    gameSpeed: Float = 1.0f,
    gravityX: Float,
    gravityY: Float
) {
    var position by remember { mutableStateOf(bug.getPosition()) }
    var isAlive by remember { mutableStateOf(bug.isAlive()) }
    var health by remember { mutableIntStateOf(bug.state.health) }
    val bugSize = 80.dp

    val currentGravityX by rememberUpdatedState(gravityX)
    val currentGravityY by rememberUpdatedState(gravityY)

    LaunchedEffect(bug, gameSpeed) {
        while (isAlive) {
            val gx = currentGravityX
            val gy = currentGravityY

            if (gx != 0f || gy != 0f) {
                bug.moveWithGravity(screenWidth, screenHeight, gx, gy)
            } else {
                bug.move(screenWidth, screenHeight)
            }
            position = bug.getPosition()
            delay(16)
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