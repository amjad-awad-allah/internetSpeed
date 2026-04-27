import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.system.measureTimeMillis

fun main() {
    println("======================================")
    println("   Internet Speed Test (Kotlin)      ")
    println("======================================")

    val client = OkHttpClient()
    val testUrl = "https://speed.cloudflare.com/__down?bytes=10000000" // 10MB test file

    println("Connecting to server...")
    
    // 1. Measure Latency (Ping)
    val latencyRequest = Request.Builder()
        .url("https://google.com")
        .head()
        .build()

    try {
        val latencyStart = System.currentTimeMillis()
        client.newCall(latencyRequest).execute().use { response ->
            if (response.isSuccessful) {
                val latency = System.currentTimeMillis() - latencyStart
                println("Latency: ${latency}ms")
            }
        }
    } catch (e: IOException) {
        println("Error measuring latency: ${e.message}")
    }

    // 2. Measure Download Speed
    println("Starting download test (10MB)...")
    val downloadRequest = Request.Builder()
        .url(testUrl)
        .build()

    try {
        var bytesRead: Long = 0
        val timeTaken = measureTimeMillis {
            client.newCall(downloadRequest).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                
                val body = response.body ?: throw IOException("Empty response body")
                val source = body.source()
                val buffer = ByteArray(8192)
                var read: Int
                
                while (source.read(buffer).also { read = it } != -1) {
                    bytesRead += read
                }
            }
        }

        val megabits = (bytesRead * 8) / 1_000_000.0
        val seconds = timeTaken / 1000.0
        val speedMbps = megabits / seconds

        println("--------------------------------------")
        println("Download Result:")
        println("Data downloaded: ${bytesRead / (1024 * 1024)} MB")
        println("Time taken: ${String.format("%.2f", seconds)}s")
        println("Average Speed: ${String.format("%.2f", speedMbps)} Mbps")
        println("--------------------------------------")

    } catch (e: IOException) {
        println("Error during download test: ${e.message}")
    }

    println("Speed test completed.")
}
