package com.example.gamebugs.ui.components

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun RulesPanel() {
    val context = LocalContext.current

    AndroidView(
        factory = {
            WebView(context).apply {
                loadUrl("file:///res/raw/rules.html")
            }
        },
        modifier = Modifier.fillMaxSize()
    )

//    Text(
//        text = stringResource(R.string.Rules),
//        style = MaterialTheme.typography.bodyMedium,
//        modifier = Modifier.fillMaxSize()
//    )
}