package utn.appmoviles.apptrabajofinal.ui.data.network.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utn.appmoviles.apptrabajofinal.config.Environment
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

fun enviarParaEntrenamiento(audioName: String, categoryName: String, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL("${Environment.BACKEND_URL}/entrenamientoAudio")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")

            // Crear el JSON con el nombre del audio y la categoría
            val jsonBody = """
                {
                    "audioName": "$audioName",
                    "categoryName": "$categoryName"
                }
            """.trimIndent()

            // Enviar el JSON al backend
            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.writeBytes(jsonBody)
            outputStream.flush()
            outputStream.close()

            // Verificar la respuesta del servidor
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                withContext(Dispatchers.Main) {
                    onResult("Audio enviado exitosamente para entrenamiento en la categoría $categoryName")
                }
            } else {
                withContext(Dispatchers.Main) {
                    onResult("Error al enviar audio para entrenamiento: ${connection.responseCode}")
                }
            }
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onResult("Error al enviar audio para entrenamiento")
            }
        }
    }
}

