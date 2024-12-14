package com.genix.notifications_example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : ComponentActivity() {
    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val token: MutableState<String?> = mutableStateOf(null)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String> ->
            token.value = task.result
        }

        setContent {
            val clipboardManager: ClipboardManager = LocalClipboardManager.current

            MaterialTheme(
                // Save your eyes, use dark theme.
                colorScheme = darkColorScheme(),
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(8.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            },
                        ) {
                            Text("ask for notification permission")
                        }
                        Text(
                            text = "TOKEN"
                        )
                        Text(
                            "${token.value}"
                        )
                        TextButton(
                            onClick = {
                                token.value?.let {
                                    clipboardManager.setText(AnnotatedString(it))
                                }
                            },
                        ) {
                            Text("copy to clipboard")
                        }
                        TextButton(
                            onClick = {
                                token.value?.let {
                                    sendToken(it)
                                }
                            },
                        ) {
                            Text("send token to local service (:8080/token)")
                        }
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendToken(token: String) = GlobalScope.launch {
        // This is your localhost:8080,
        // see: https://stackoverflow.com/a/5495789
        val url = URL("http://10.0.2.2:8080/token")

        val httpCon: HttpURLConnection = url.openConnection() as HttpURLConnection
        httpCon.requestMethod = "PUT"
        val out = OutputStreamWriter(
            httpCon.outputStream
        )

        out.write(token)
        out.close()
        httpCon.responseCode
    }

}
