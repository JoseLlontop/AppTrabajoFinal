package utn.appmoviles.apptrabajofinal;

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import android.Manifest
import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import utn.appmoviles.apptrabajofinal.ui.data.network.services.enviarAudioConNombreEnArchivo
import utn.appmoviles.apptrabajofinal.ui.data.network.services.enviarParaEntrenamiento
import utn.appmoviles.apptrabajofinal.ui.data.network.services.enviarPorWhatsApp
import utn.appmoviles.apptrabajofinal.ui.data.network.services.recibirTodosLosAudios
import java.io.ByteArrayOutputStream


private var audioRecord: AudioRecord? = null
private var isRecording = false
private lateinit var recordingBuffer: ByteArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGrabacion(navigationController: NavHostController?) {
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var selectedAudioName by remember { mutableStateOf<String?>(null) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    var isRecording by remember { mutableStateOf(false) }
    var audioName by remember { mutableStateOf(TextFieldValue("")) }
    var audioList by remember { mutableStateOf<List<String>>(emptyList()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Cargar la lista de audios al iniciar
    LaunchedEffect(Unit) {
        recibirTodosLosAudios { result ->
            audioList = result
            Toast.makeText(context, "Lista de audios actualizada", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Grabador de Audio",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3F51B5),
            modifier = Modifier.padding(vertical = 42.dp)
        )

        OutlinedTextField(
            value = audioName,
            onValueChange = { audioName = it },
            label = { Text("Nombre del audio") },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, shape = MaterialTheme.shapes.medium),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(38.dp))

        Button(
            onClick = {
                isRecording = !isRecording
                if (isRecording) {
                    startRecording(context)
                    Toast.makeText(context, "Grabando audio...", Toast.LENGTH_SHORT).show()
                } else {
                    stopRecording()
                    Toast.makeText(context, "Grabación detenida", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRecording) Color(0xFFFFC107) else Color(0xFF6200EA)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(8.dp, shape = MaterialTheme.shapes.medium),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = if (isRecording) "Detener Grabación" else "Iniciar Grabación",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                if (audioName.text.isNotEmpty()) {
                    coroutineScope.launch {
                        val audioData = recordingBuffer ?: ByteArray(0)
                        enviarAudioConNombreEnArchivo(audioData, audioName.text) { result ->
                            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                            recibirTodosLosAudios { updatedList ->
                                audioList = updatedList
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Por favor, ingrese un nombre para el audio", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(8.dp, shape = MaterialTheme.shapes.medium),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Guardar y Enviar Audio",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(42.dp))

        Text(
            text = "Lista de Grabaciones",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3F51B5)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(top = 16.dp)
        ) {
            items(audioList) { audio ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                    elevation = CardDefaults.elevatedCardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Audio",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = audio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        // Icono de WhatsApp con acción para abrir el diálogo
                        IconButton(
                            onClick = {
                                selectedAudioName = audio  // Almacena el nombre del audio actual
                                showPhoneDialog = true     // Muestra el diálogo de WhatsApp
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.whatsapp),
                                contentDescription = "Enviar por WhatsApp",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                selectedAudioName = audio  // Almacena el nombre del audio actual
                                showCategoryDialog = true  // Muestra el diálogo para asignar categoría
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = "Asignar categoría",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "Entrenar Audio",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Envía el audio para poder reconocerlo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        resultMessage?.let { message ->
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(message)
                resultMessage = null
            }
        }
    }

    // Diálogo para ingresar el número de teléfono
    if (showPhoneDialog) {
        PromptPhoneNumberDialog(
            onConfirm = { phoneNumber ->
                showPhoneDialog = false
                selectedAudioName?.let { audioName ->
                    enviarPorWhatsApp(audioName, phoneNumber) { result ->
                        Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDismiss = { showPhoneDialog = false }
        )
    }

    // Diálogo para asignar categoría, con lógica similar si es necesario
    if (showCategoryDialog) {
        PromptCategoryNameDialog(
            onConfirm = { categoryName ->
                showCategoryDialog = false
                selectedAudioName?.let { audioName ->
                    enviarParaEntrenamiento(audioName, categoryName) { result ->
                        Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDismiss = { showCategoryDialog = false }
        )
    }
}

private fun startRecording(context: Context) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        return
    }

    val sampleRate = 16000
    val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
    recordingBuffer = ByteArray(bufferSize)

    // Crear un OutputStream para almacenar los datos grabados
    val byteArrayOutputStream = ByteArrayOutputStream()

    audioRecord = AudioRecord.Builder()
        .setAudioSource(MediaRecorder.AudioSource.MIC)
        .setAudioFormat(
            AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build()
        )
        .setBufferSizeInBytes(bufferSize)
        .build()

    audioRecord?.startRecording()
    isRecording = true

    // Iniciar la grabación en un hilo aparte
    Thread {
        while (isRecording) {
            val read = audioRecord?.read(recordingBuffer, 0, bufferSize) ?: 0
            if (read > 0) {
                // Almacenar los datos leídos en el OutputStream
                byteArrayOutputStream.write(recordingBuffer, 0, read)
            }
        }

        // Guardar los datos grabados en una variable global para enviarlos
        recordingBuffer = byteArrayOutputStream.toByteArray()
    }.start()
}

private fun stopRecording() {
    isRecording = false
    audioRecord?.stop()
    audioRecord?.release()
    audioRecord = null

    // Aca se puede realizar la lógica de envío al backend
    // usando `recordingBuffer`, que ahora contiene el audio completo
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaGrabacion() {
    // Datos de prueba
    val dummyAudioList = listOf("Audio_1", "Audio_2", "Audio_3")
    val navController = rememberNavController()

    // Componente principal de la grabadora con lista de grabaciones
    PantallaGrabacion(navigationController = navController)
}


