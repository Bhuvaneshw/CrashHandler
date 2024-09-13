package com.acutecoder.crashhandler

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.acutecoder.crashhandler.core.ErrorLog
import com.acutecoder.crashhandler.ui.theme.CrashHandlerTheme
import com.acutecoder.crashhandler.util.crashHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

class CustomCrashHandlerActivity : ComponentActivity() {

    private val regexErrorClass = "\\w+(\\.\\w+)+:\\s.*\n".toRegex()
    private val regexFileInfo = "\\([\\w.]+:\\d+\\)".toRegex()
    private val regexMoreErrorInfo = "and \\d+ more errors?".toRegex()
    private val regexTime =
        "Time: [0-9]{2}/[0-9]{2}/[0-9]{4}, [0-9]{2}:[0-9]{2} (AM|PM), [A-Z]{3}(\\+[0-9]{2}:[0-9]{2})?".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CrashHandlerTheme {
                val containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(containerColor)
                        .systemBarsPadding(),
                ) {
                    val context = LocalContext.current
                    var errorLog by remember { mutableStateOf<ErrorLog?>(null) }

                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            delay(3333)
                            errorLog = context.crashHandler.loadErrorLog()
                        }
                    }

                    TopBar(
                        lastErrorTime = errorLog?.lastErrorTime,
                        exitScreen = this@CustomCrashHandlerActivity::finish
                    )

                    ErrorBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        errorLog = errorLog
                    )

                    BottomBar(
                        errorText = { errorLog?.simplifiedLog() ?: "No log found!" },
                        exitScreen = this@CustomCrashHandlerActivity::finish
                    )
                }
            }
        }
    }

    @Composable
    private fun ErrorBox(modifier: Modifier, errorLog: ErrorLog?) {
        errorLog?.let {
            Column(
                modifier = modifier
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState()),
            ) {
                SelectionContainer {
                    Text(
                        text = it.simplifiedLog()
                            .buildAnnotation(
                                regexErrorClass to (MaterialTheme.colorScheme.error style null),
                                regexFileInfo to (MaterialTheme.colorScheme.primary style null),
                                regexMoreErrorInfo to (MaterialTheme.colorScheme.primary style null),
                                regexTime to (MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f) style 11.sp),
                            ),
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        } ?: Box(
            modifier = modifier
                .fillMaxSize()
                .padding(12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun TopBar(lastErrorTime: String?, exitScreen: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Error Log",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        if (lastErrorTime != null) {
            Text(
                text = "Time: $lastErrorTime",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { exitScreen() }
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun BottomBar(errorText: () -> String, exitScreen: () -> Unit) {
    val context = LocalContext.current

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        ErrorButton(
            onClick = {
                context.crashHandler.clearAll()
                exitScreen()
            }
        ) {
            Text(text = "Clear all")
        }

        Spacer(modifier = Modifier.weight(1f))

        ErrorIconButton(
            resId = R.drawable.baseline_content_copy_24,
            contentDescription = "Copy log",
            onClick = {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val data = ClipData(
                    ClipDescription("Error log", arrayOf("text/plain")),
                    ClipData.Item(errorText())
                )
                clipboard.setPrimaryClip(data)
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        ErrorIconButton(
            resId = R.drawable.baseline_share_24,
            contentDescription = "Share log",
            onClick = {
                context.shareLog(context.crashHandler.crashFile)
            }
        )
    }
}

@Composable
private fun ErrorButton(onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
        ),
        content = content
    )
}

@Composable
private fun ErrorIconButton(resId: Int, contentDescription: String?, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
        )
    ) {
        Icon(
            painter = painterResource(resId),
            contentDescription = contentDescription,
            modifier = Modifier.padding(10.dp)
        )
    }
}

private fun Context.shareLog(file: File) {
    if (file.exists()) {
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "Share log file"))
    } else Toast.makeText(this, "Crash file not found!", Toast.LENGTH_SHORT).show()
}

private fun String.buildAnnotation(vararg regexPairs: Pair<Regex, SpanStyle>) =
    buildAnnotatedString {
        var currentIndex = 0

        val matches = mutableListOf<Triple<Int, Int, SpanStyle>>()

        regexPairs.forEach { (regex, highlightColor) ->
            regex.findAll(this@buildAnnotation).forEach { matchResult ->
                val startIndex = matchResult.range.first
                val endIndex = matchResult.range.last + 1
                matches.add(Triple(startIndex, endIndex, highlightColor))
            }
        }

        matches.sortBy { it.first }

        matches.forEach { (startIndex, endIndex, spanStyle) ->
            if (currentIndex <= startIndex) {
                append(this@buildAnnotation.substring(currentIndex, startIndex))

                withStyle(style = spanStyle) {
                    append(this@buildAnnotation.substring(startIndex, endIndex))
                }

                currentIndex = endIndex
            }
        }

        if (currentIndex < this@buildAnnotation.length) {
            append(this@buildAnnotation.substring(currentIndex))
        }
    }

private infix fun Color.style(size: TextUnit?): SpanStyle {
    return if (size != null)
        SpanStyle(color = this, fontSize = size)
    else SpanStyle(color = this)
}
