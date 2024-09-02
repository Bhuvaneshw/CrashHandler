package com.acutecoder.crashhandler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.acutecoder.crashhandler.helper.ErrorLog
import com.acutecoder.crashhandler.ui.theme.CrashHandlerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class CustomCrashHandlerActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CrashHandlerTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .systemBarsPadding(),
                    topBar = { TopAppBar(title = { Text(text = "Error Log") }) },
                    bottomBar = { BottomBar() }
                ) { paddingValues ->
                    val context = LocalContext.current
                    var errorLog by remember { mutableStateOf<ErrorLog?>(null) }

                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            errorLog = context.crashHandler.loadErrorLog()
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        errorLog?.let {
                            Text(text = "Time: ${it.lastErrorTime}")
                            Text(
                                text = it.simplifiedLog(),
                                modifier = Modifier
                                    .padding(12.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .verticalScroll(rememberScrollState())
                                    .horizontalScroll(rememberScrollState())
                                    .padding(12.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        } ?: CircularProgressIndicator()
                    }
                }
            }
        }
    }

    @Composable
    private fun BottomBar() {
        val context = LocalContext.current

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Button(onClick = {
                context.crashHandler.clearAll()
                finish()
            }) {
                Text(text = "Clear all")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = {
                // To share file use context.crashHandler.crashFile
            }) {
                Text(text = "Share log")
            }
        }
    }

}
