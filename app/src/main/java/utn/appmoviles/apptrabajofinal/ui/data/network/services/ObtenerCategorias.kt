package utn.appmoviles.apptrabajofinal.ui.data.network.services

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import utn.appmoviles.apptrabajofinal.config.Environment
import java.net.HttpURLConnection
import java.net.URL

fun recibirTodasCategorias(onResult: (List<String>) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL("${Environment.BACKEND_URL}/categories")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val result = connection.inputStream.bufferedReader().readText()
                withContext(Dispatchers.Main) {
                    // Parsear el JSON de respuesta
                    val jsonObject = JSONObject(result)
                    val categories = jsonObject.getJSONArray("categories")

                    // Convertir el JSONArray a una lista de strings
                    val categoryList = List(categories.length()) { index ->
                        categories.getString(index)
                    }

                    // Retornar la lista de categorías
                    onResult(categoryList)
                }
            } else {
                Log.e("FetchCategories", "Error al obtener categorías: ${connection.responseCode}")
            }
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}