package utn.appmoviles.apptrabajofinal.ui.data.network.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utn.appmoviles.apptrabajofinal.config.Environment
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

// Método para enviar el audio y el nombre al backend
fun enviarAudioConNombreEnArchivo(audioData: ByteArray, audioName: String, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL("${Environment.BACKEND_URL}/guardarAudio")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=boundary")

            val boundary = "boundary"
            val outputStream = DataOutputStream(connection.outputStream)

            // Agregar el campo 'name' para enviar el nombre del audio
            outputStream.writeBytes("--$boundary\r\n")
            outputStream.writeBytes("Content-Disposition: form-data; name=\"name\"\r\n\r\n")
            outputStream.writeBytes("$audioName\r\n")

            // Agregar el archivo de audio
            outputStream.writeBytes("--$boundary\r\n")
            outputStream.writeBytes("Content-Disposition: form-data; name=\"audio\"; filename=\"$audioName.pcm\"\r\n")
            outputStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n")
            outputStream.write(audioData)
            outputStream.writeBytes("\r\n--$boundary--\r\n")
            outputStream.flush()
            outputStream.close()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                withContext(Dispatchers.Main) {
                    onResult("Audio enviado exitosamente")
                }
            } else {
                withContext(Dispatchers.Main) {
                    onResult("Error al enviar audio: ${connection.responseCode}")
                }
            }
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onResult("Error al enviar audio")
            }
        }
    }
}
