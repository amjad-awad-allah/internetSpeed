package com.example.internetspeed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.internetspeed.logic.SpeedTestManager
import com.example.internetspeed.ui.SpeedGauge
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()
    private val speedTestManager = SpeedTestManager(client)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedTestScreen(speedTestManager)
        }
    }
}

@Composable
fun SpeedTestScreen(manager: SpeedTestManager) {
    val speed by manager.currentSpeed.collectAsState()
    val isTesting by manager.isTesting.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F)) // Dark background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SpeedGauge(speed = speed)

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = {
                scope.launch {
                    manager.startDownloadTest("https://speed.cloudflare.com/__down?bytes=25000000")
                }
            },
            enabled = !isTesting,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4FACFE),
                disabledContainerColor = Color.Gray
            ),
            modifier = Modifier.width(200.dp)
        ) {
            Text(if (isTesting) "Testing..." else "START TEST")
        }
    }
}
