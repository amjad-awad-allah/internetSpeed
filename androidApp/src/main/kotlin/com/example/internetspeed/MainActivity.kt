package com.example.internetspeed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.internetspeed.logic.SpeedTestManager
import com.example.internetspeed.ui.GaugeStyle
import com.example.internetspeed.ui.SpeedGauge
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.text.SimpleDateFormat
import java.util.*

data class TestResult(val speed: Float, val date: String)

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()
    private val speedTestManager = SpeedTestManager(client)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedTestApp(speedTestManager)
        }
    }
}

@Composable
fun SpeedTestApp(manager: SpeedTestManager) {
    val speed by manager.currentSpeed.collectAsState()
    val isTesting by manager.isTesting.collectAsState()
    var selectedStyle by remember { mutableStateOf(GaugeStyle.MODERN_ARC) }
    val history = remember { mutableStateListOf<TestResult>() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F0F0F), Color(0xFF1A1A1A))
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Internet Speed",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Gauge with selected style
        SpeedGauge(speed = speed, style = selectedStyle)

        Spacer(modifier = Modifier.height(32.dp))

        // Style Selector
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.05f))
                .padding(4.dp)
        ) {
            GaugeStyle.values().forEach { style ->
                val isSelected = selectedStyle == style
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) Color(0xFF4FACFE) else Color.Transparent)
                        .clickable { selectedStyle = style }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = style.name.replace("_", " "),
                        color = if (isSelected) Color.White else Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                scope.launch {
                    manager.startDownloadTest("https://speed.cloudflare.com/__down?bytes=25000000")
                    history.add(0, TestResult(manager.currentSpeed.value, SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault()).format(Date())))
                }
            },
            enabled = !isTesting,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4FACFE),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(if (isTesting) "TESTING..." else "START SPEED TEST", fontWeight = FontWeight.ExtraBold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // History Section
        Text(
            text = "Recent History",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(history) { result ->
                HistoryItem(result)
            }
        }
    }
}

@Composable
fun HistoryItem(result: TestResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = result.date, color = Color.Gray, fontSize = 12.sp)
            Text(text = "Download Test", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Text(
            text = "${String.format("%.1f", result.speed)} Mbps",
            color = Color(0xFF00F2FE),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
