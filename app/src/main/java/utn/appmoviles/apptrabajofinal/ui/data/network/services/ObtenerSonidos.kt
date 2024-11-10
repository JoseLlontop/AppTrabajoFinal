package utn.appmoviles.apptrabajofinal.ui.data.network.services

import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import utn.appmoviles.apptrabajofinal.config.Environment
import java.net.HttpURLConnection
import java.net.URL

// Método para recibir todos los audios desde el backend
fun recibirTodosLosAudios(onResult: (List<String>) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL("${Environment.BACKEND_URL}/getAudios")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.doInput = true

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val audioList = mutableListOf<String>()

                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    audioList.add(jsonArray.getString(i))
                }

                withContext(Dispatchers.Main) {
                    onResult(audioList)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onResult(emptyList())
                }
            }
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onResult(emptyList())
            }
        }
    }
}