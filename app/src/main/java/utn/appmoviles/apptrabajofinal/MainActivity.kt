package utn.appmoviles.apptrabajofinal

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.io.IOException
import java.nio.ByteOrder
import utn.appmoviles.apptrabajofinal.model.Routes


class MainActivity : ComponentActivity() {

    private lateinit var interpreter: Interpreter
    private lateinit var audioRecord: AudioRecord
    private var isRecording = false
    private var inputSize: Int = 0  // Inicializa aquí

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Mensaje de log antes de cargar el modelo
            Log.d("MainActivity", "Intentando cargar el modelo TFLite")
            interpreter = Interpreter(loadModelFile())

            // Obtener el tamaño del tensor de entrada del modelo después de inicializar el interpreter
            val inputShape = interpreter.getInputTensor(0).shape()  // Obtener la forma de entrada
            inputSize = inputShape[1]  // Tamaño esperado en el eje de las muestras de audio

            // Mensaje de log después de cargar el modelo
            Log.d("MainActivity", "Modelo TFLite cargado correctamente")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar el modelo: ${e.message}", Toast.LENGTH_LONG).show()
            return
        }

        setContent {

            // NAVEGACION DE LA APLICACION

            val navigationController = rememberNavController()

            NavHost (
                navController = navigationController,
                startDestination = Routes.PantallaLogin.route
            )
            {
                composable(Routes.PantallaLogin.route){ PantallaLogin(navigationController)}
                composable(Routes.PantallaPrincipal.route) {PantallaPrincipal(navigationController)}
                composable(Routes.PantallaConfiguracion.route){PantallaConfiguracion(navigationController)}
                composable(Routes.PantallaEntrenamientoSonido.route){PantallaEntrenamientoSonido(navigationController)}
                //composable(Routes.PantallaEnvioWhastsApp.route){PantallaEnvioWhastsApp(navigationController)}

            }

            // MANEJO DE SONIDOS CON EL MODELO

            var audioCategory by remember { mutableStateOf("Categoría: No detectada") }

            val context = LocalContext.current
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    // Manejar denegación de permiso
                }
            }

            // Verificar permisos de audio
            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }

            // UI del botón
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = audioCategory)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { startListening { category -> audioCategory = "Categoría: $category" } }
                ) {
                    Text(text = "Comenzar detección")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { stopListening() }) {
                    Text(text = "Detener detección")
                }
            }
        }
    }

    private fun startListening(onResult: (String) -> Unit) {
        if (!isRecording) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Solicitar permisos si no están concedidos
                return
            }
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                16000,  // Tasa de muestreo
                android.media.AudioFormat.CHANNEL_IN_MONO,
                android.media.AudioFormat.ENCODING_PCM_16BIT,
                1024 * 2
            )

            audioRecord.startRecording()
            isRecording = true

            // Hilo para capturar y procesar el audio
            Thread {
                val buffer = ByteBuffer.allocateDirect(1024 * 2)
                while (isRecording) {
                    val readSize = audioRecord.read(buffer, 1024)
                    if (readSize > 0) {
                        val processedBuffer = padOrTrim(buffer, inputSize)  // Ajustar al tamaño del tensor
                        val category = processAudio(processedBuffer)

                        if (category != "No detectada") {
                            // Mostrar la categoría y detener detección por 10 segundos
                            onResult(category)
                            Thread.sleep(10_000)  // Pausa la detección por 10 segundos
                            onResult("Categoría: No detectada")  // Reinicia el estado después de 10 segundos
                        }
                    }
                }
            }.start()
        }
    }


    // Asegúrate de que el buffer de audio coincida con el tamaño esperado por el modelo
    private fun padOrTrim(buffer: ByteBuffer, targetLength: Int): ByteBuffer {
        val bufferLength = buffer.limit()

        return if (bufferLength > targetLength) {
            // Recortar si es demasiado largo
            val newBuffer = ByteBuffer.allocateDirect(targetLength)
            buffer.position(0)
            buffer.limit(targetLength)
            newBuffer.put(buffer)
            newBuffer
        } else {
            // Rellenar si es demasiado corto
            val paddingSize = targetLength - bufferLength
            val newBuffer = ByteBuffer.allocateDirect(targetLength)
            newBuffer.put(buffer)
            newBuffer.position(bufferLength)
            for (i in 0 until paddingSize) {
                newBuffer.put(0) // Rellenar con ceros
            }
            newBuffer
        }
    }

    private fun stopListening() {
        if (isRecording) {
            audioRecord.stop()
            audioRecord.release()
            isRecording = false
        }
    }

    private fun processAudio(buffer: ByteBuffer): String {
        // Asegúrate de que el tamaño del buffer coincida con lo que el modelo espera
        val inputBuffer = ByteBuffer.allocateDirect(12504)  // Cambia según el tamaño que espera el modelo
        inputBuffer.order(ByteOrder.nativeOrder())

        // Copia los datos del buffer de audio al inputBuffer
        buffer.rewind()  // Asegura que el buffer original esté en el estado correcto
        inputBuffer.put(buffer)

        // Cambia el tamaño del arreglo de salida según la salida esperada del modelo
        val output = Array(1) { FloatArray(2) }  // Cambiar a 2 clases

        // Realiza la inferencia
        interpreter.run(inputBuffer, output)

        // Encuentra el índice de la categoría con mayor probabilidad
        val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val confidence = output[0][maxIndex]  // Obtiene la confianza (probabilidad)

        // Lista de categorías
        val categories = listOf("sonidos_bomberos", "sonidos_policia")

        // Umbral de confianza: solo mostrar si la probabilidad
        return if (confidence >= 0.6) {
            categories[maxIndex]  // Retorna la categoría detectada si supera el umbral
        } else {
            "No detectada"  // Retorna "No detectada" si no se alcanza el umbral
        }
    }

    private fun loadModelFile(): ByteBuffer {
        return try {
            // Cargar el archivo del modelo desde res/raw
            val inputStream = resources.openRawResource(R.raw.sirenas_model)
            val byteArray = ByteArray(inputStream.available())
            inputStream.read(byteArray)
            inputStream.close()

            // Crear un ByteBuffer con el tamaño adecuado y en el orden de bytes nativo
            val byteBuffer = ByteBuffer.allocateDirect(byteArray.size)
            byteBuffer.order(ByteOrder.nativeOrder())  // Asegurarse de que esté en el orden de bytes nativo
            byteBuffer.put(byteArray)
            byteBuffer.rewind()  // Asegurarse de que el buffer esté en la posición inicial
            byteBuffer
        } catch (e: IOException) {
            throw RuntimeException("Error al cargar el modelo: ${e.message}")
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MostrarPantallaLogin(){
    PantallaLogin(null)
}

@Composable
fun PantallaLogin(navigationController: NavHostController?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally


    ) {
        //Titulo Login
        Text(
            text = "Login",
            fontSize = 24.sp
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navigationController?.navigate(Routes.PantallaPrincipal.route) },
                modifier = Modifier
                    .width(130.dp)
                    .height(45.dp)
            ) {
                Text(
                    text = "Iniciar",
                    fontSize = 18.sp
                )
            }

        }
    }
}






