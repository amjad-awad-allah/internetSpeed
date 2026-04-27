package com.example.internetspeed.logic

import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException

class SpeedTestManager(private val client: OkHttpClient) {
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                
                val body = response.body ?: return
                val source = body.source()
                val buffer = ByteArray(8192)
                var bytesRead = 0L
                val startTime = System.currentTimeMillis()

                while (true) {
                    val read = source.read(buffer)
                    if (read == -1) break
                    bytesRead += read
                    
                    val elapsed = (System.currentTimeMillis() - startTime) / 1000f
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isTesting.value = false
        }
    }
}
