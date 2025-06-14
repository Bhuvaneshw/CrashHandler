package com.acutecoder.crashhandler

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.acutecoder.crashhandler.core.ErrorLog
import com.acutecoder.crashhandler.util.crashHandler

class CrashHandlerActivity : ComponentActivity() {

    private lateinit var time: TextView
    private lateinit var logView: TextView
    private lateinit var close: TextView
    private lateinit var clearAll: Button
    private lateinit var copyLog: Button
    private lateinit var shareLog: Button
    private lateinit var progressBar: ProgressBar
    private var errorLog: ErrorLog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crash_handler)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        time = findViewById(R.id.time)
        logView = findViewById(R.id.logView)
        close = findViewById(R.id.close)
        clearAll = findViewById(R.id.clearAll)
        copyLog = findViewById(R.id.copyLog)
        shareLog = findViewById(R.id.shareLog)
        progressBar = findViewById(R.id.progressBar)

        init()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {}
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
    }

    private fun init() {
        Thread {
            val log = crashHandler.loadErrorLog()
            errorLog = log

            runOnUiThread {
                time.text = log.lastErrorTime
                logView.text = log.simplifiedLog()
                progressBar.visibility = View.GONE
            }
        }.start()

        shareLog.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(
                Intent.EXTRA_TEXT,
                errorLog?.errors?.joinToString(separator = crashHandler.separator) { it })
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share Error log")
            startActivity(Intent.createChooser(intent, "Share"))
        }

        copyLog.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val data = ClipData(
                ClipDescription("Error log", arrayOf("text/plain")),
                ClipData.Item(logView.text)
            )
            clipboard.setPrimaryClip(data)
        }

        clearAll.setOnClickListener {
            crashHandler.clearAll()
            finish()
        }

        close.setOnClickListener {
            finish()
        }
    }

}
