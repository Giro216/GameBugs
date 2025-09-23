package com.example.gamebugs.ui.components

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.gamebugs.R

@Composable
fun RulesPanel() {
    val context = LocalContext.current

    Text(
        text = stringResource(R.string.Rules),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxSize()
    )
}