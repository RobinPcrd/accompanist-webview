/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.accompanist.sample.webview

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import io.github.robinpcrd.accompanist.web.LoadingState
import io.github.robinpcrd.accompanist.web.WebView
import io.github.robinpcrd.accompanist.web.rememberSaveableWebViewState
import io.github.robinpcrd.accompanist.web.rememberWebViewNavigator
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class WebViewJsSample : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold {
                    Surface(modifier = Modifier.padding(it)) {
                        Column(Modifier.fillMaxSize()) {
                            Home()
                        }
                    }
                }
            }
        }
    }
}

@Composable
@SuppressLint("SetJavaScriptEnabled")
private fun Home() {
    val webViewState = rememberSaveableWebViewState()
    val navigator = rememberWebViewNavigator()

    LaunchedEffect(navigator) {
        val bundle = webViewState.viewState
        if (bundle == null) {
            // This is the first time load, so load the home page.
            navigator.loadUrl("https://wikipedia.org")
        }
    }

    LaunchedEffect(webViewState) {
        snapshotFlow { webViewState.loadingState }
            .collect {
                // Scroll to bottom when finished loading
                if (it is LoadingState.Finished) {
                    delay(1.seconds)
                    webViewState.webView?.evaluateJavascript(
                        "window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});",
                        null
                    )
                    delay(1.seconds)
                    webViewState.webView?.evaluateJavascript(
                        "window.scrollTo({top: 0, behavior: 'smooth'});",
                        null
                    )
                }
            }
    }

    WebView(
        state = webViewState,
        navigator = navigator,
        modifier = Modifier.fillMaxSize(),
        onCreated = {
            it.settings.javaScriptEnabled = true
        },
    )
}