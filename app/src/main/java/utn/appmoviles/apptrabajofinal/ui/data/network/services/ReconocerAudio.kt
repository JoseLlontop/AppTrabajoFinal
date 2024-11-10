package utn.appmoviles.apptrabajofinal.ui.data.network.services

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import utn.appmoviles.apptrabajofinal.config.Environment
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

fun sendAudioToBackend(audioData: ByteArray, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL("${Environment.BACKEND_URL}/upload")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=boundary")

            val boundary = "boundary"
            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.writeBytes("--$boundary\r\n")
            outputStream.writeBytes("Content-Disposition: form-data; name=\"audio\"; filename=\"audio.pcm\"\r\n")
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n")
            outputStream.write(audioData)
            outputStream.writeBytes("\r\n--$boundary--\r\n")
            outputStream.flush()
            outputStream.close()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val result = connection.inputStream.bufferedReader().readText()
                withContext(Dispatchers.Main) {
                    // Parsear el resultado JSON
                    val jsonObject = JSONObject(result)
                    val categoryIndex = jsonObject.getInt("category")

                    // Mapear el índice a la categoría
                    val categoryName = when (categoryIndex) {
                        0 -> "sonidos de bomberos"
                        1 -> "sonidos de policia"
                        10 -> "No detecta"
                        else -> ""
                    }

                    onResult(categoryName)
                }
            } else {
                Log.e("MainActivity", "Error al enviar audio: ${connection.responseCode}")
            }
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}