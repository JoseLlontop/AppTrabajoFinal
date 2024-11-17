package utn.appmoviles.apptrabajofinal

import android.util.Log
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import utn.appmoviles.apptrabajofinal.model.Routes
import java.net.HttpURLConnection
import java.net.URL
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import utn.appmoviles.apptrabajofinal.config.Environment
import utn.appmoviles.apptrabajofinal.ui.data.network.services.recibirTodasCategorias
import utn.appmoviles.apptrabajofinal.ui.data.network.services.sendAudioToBackend


class MainActivity : ComponentActivity() {
    private lateinit var audioRecord: AudioRecord
    private var isRecording = false
    private var recordingJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicitar permiso para grabar audio al inicio
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)

        // Instancia de ColorPreferencesManager usando el contexto de la actividad
        val colorPreferencesManager = ColorPreferencesManager(this)

        setContent {
            val navigationController = rememberNavController()
            var audioCategory by remember { mutableStateOf("No detectada") }

            // Estado para almacenar las categorías obtenidas del backend
            val categoriesState = remember { mutableStateOf<List<String>>(emptyList()) }

            // Llamar a fetchCategoriesFromBackend al inicio para cargar las categorías
            LaunchedEffect(Unit) {
                recibirTodasCategorias { categories ->
                    categoriesState.value = categories
                }
            }

            NavHost(
                navController = navigationController,
                startDestination = Routes.PantallaPrincipal.route
            ) {
                composable(Routes.PantallaPrincipal.route) {
                    PantallaPrincipal(
                        navigationController = navigationController,
                        colorPreferencesManager = colorPreferencesManager,
                        audioCategory = audioCategory,
                        categories = categoriesState.value, // Pasar categorías al composable
                        onStartListening = { startContinuousListening { category -> audioCategory = category } },
                        onStopListening = { stopContinuousListening() },
                    )
                }
                composable(Routes.PantallaGrabacion.route) { PantallaGrabacion(navigationController) }
            }
        }
    }

    private fun startContinuousListening(onResult: (String) -> Unit) {
        if (!isRecording) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
                return
            }

            isRecording = true
            recordingJob = CoroutineScope(Dispatchers.Main).launch {
                while (isRecording) {
                    recordAndSendAudio(onResult)
                    delay(3000) // Esperar 3 segundos antes de la próxima grabación
                }
            }
        }
    }

    private suspend fun recordAndSendAudio(onResult: (String) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                // Verifica si el permiso de grabación de audio ha sido concedido
                if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                    // Si no se ha concedido, puedes lanzar una excepción o manejarlo de alguna manera
                    Log.e("MainActivity", "Permiso de grabación de audio no concedido.")
                    return@withContext
                }

                // Configuración para grabar audio en formato PCM
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    16000,  // Tasa de muestreo de 16 kHz
                    android.media.AudioFormat.CHANNEL_IN_MONO,
                    android.media.AudioFormat.ENCODING_PCM_16BIT,
                    1024 * 2
                )

                audioRecord.startRecording()
                val buffer = ByteArray(16000 * 7) // Buffer para 3 segundos de grabación
                audioRecord.read(buffer, 0, buffer.size)

                // Enviar el archivo PCM al backend
                sendAudioToBackend(buffer, onResult)

                audioRecord.stop()
                audioRecord.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun stopContinuousListening() {
        if (isRecording) {
            isRecording = false
            recordingJob?.cancel()
        }
    }
}









