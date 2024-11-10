package utn.appmoviles.apptrabajofinal

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import utn.appmoviles.apptrabajofinal.model.Routes


// Extensión para DataStore
val Context.dataStore by preferencesDataStore(name = "color_preferences")

// Definición de claves de preferencias
val COLOR_PREFERENCE_BOMBEROS = intPreferencesKey("color_bomberos")
val COLOR_PREFERENCE_POLICIA = intPreferencesKey("color_policia")

// Clase de manejo de preferencias de color
class ColorPreferencesManager(private val context: Context) {
    suspend fun saveColorPreference(categoryKey: Preferences.Key<Int>, color: Color) {
        context.dataStore.edit { preferences ->
            preferences[categoryKey] = color.toArgb()
        }
    }

    fun getColorPreference(categoryKey: Preferences.Key<Int>, defaultColor: Color): Flow<Color> {
        return context.dataStore.data.map { preferences ->
            val colorArgb = preferences[categoryKey] ?: defaultColor.toArgb()
            Color(colorArgb)
        }
    }
}

@Composable
fun PantallaPrincipal(
    navigationController: NavHostController?,
    audioCategory: String,  // Audio detectado recibido de MainActivity
    colorPreferencesManager: ColorPreferencesManager,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit
) {
    // Definir las categorías de audio y sus claves de color
    val categories = listOf(
        Pair("Sonidos de bomberos", COLOR_PREFERENCE_BOMBEROS),
        Pair("Sonidos de policía", COLOR_PREFERENCE_POLICIA)
    )
    val defaultColors = listOf(Color.Red, Color.Blue)

    // Obtener los colores guardados para cada categoría
    val colors = categories.mapIndexed { index, category ->
        val colorFlow = colorPreferencesManager.getColorPreference(category.second, defaultColors[index])
        colorFlow.collectAsState(initial = defaultColors[index])
    }

    // Variables de estado para controlar la grabación y el parpadeo
    var isRecording by remember { mutableStateOf(false) }
    var isFlashing by remember { mutableStateOf(false) }

    // Seleccionar el color basado en audioCategory
    val backgroundColor = when (audioCategory) {
        "sonidos de bomberos" -> colors[0].value
        "sonidos de policia" -> colors[1].value
        else -> Color.White  // "No detecta" por defecto es blanco
    }

    // Efecto de parpadeo
    var flashColor by remember { mutableStateOf(backgroundColor) }
    LaunchedEffect(isFlashing, backgroundColor) {
        while (isFlashing) {
            flashColor = if (flashColor == backgroundColor) Color.White else backgroundColor
            delay(500)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(flashColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxWidth(), // Hace que el Box ocupe todoo el ancho de la pantalla
            contentAlignment = Alignment.Center // Centra el contenido dentro del Box
        ) {
            Text(
                text = "Detección de sonidos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3F51B5),
                modifier = Modifier.padding(vertical = 35.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories.size) { index ->
                val (categoryName, categoryKey) = categories[index]
                val selectedColor = colors[index].value

                // Estado para el desplegable de color
                var expanded by remember { mutableStateOf(false) }
                val colorOptions = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta)
                val colorNames = listOf("Rojo", "Verde", "Azul", "Amarillo", "Magenta")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = categoryName,
                            fontSize = 18.sp,
                            color = Color(0xFF333333)
                        )

                        // Botón para seleccionar color
                        Box {
                            Button(
                                onClick = { expanded = true },
                                colors = ButtonDefaults.buttonColors(containerColor = selectedColor)
                            ) {
                                Text(
                                    text = "Seleccionar color",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                colorOptions.forEachIndexed { i, colorOption ->
                                    DropdownMenuItem(
                                        text = { Text(colorNames[i]) },
                                        onClick = {
                                            expanded = false
                                            CoroutineScope(Dispatchers.IO).launch {
                                                colorPreferencesManager.saveColorPreference(categoryKey, colorOption)
                                            }
                                        },
                                        leadingIcon = {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(colorOption)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Botón para añadir nuevo sonido y navegar a PantallaGrabacion
        Button(
            onClick = { navigationController?.navigate(Routes.PantallaGrabacion.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text(
                text = "Añadir nuevo sonido",
                fontSize = 18.sp,
                color = Color.White
            )
        }

        // Botones Iniciar y Detener
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 20.dp)
        ) {
            Button(
                onClick = {
                    if (!isRecording) {
                        isRecording = true
                        isFlashing = true
                        onStartListening()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color(0xFFFFC107) else Color(0xFF6200EA)
                ),
                modifier = Modifier
                    .weight(1.4f)
                    .height(50.dp)
            ) {
                Text(
                    text = if (isRecording) "Grabando..." else "Iniciar",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    if (isRecording) {
                        isRecording = false
                        isFlashing = false
                        onStopListening()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020)),
                modifier = Modifier
                    .weight(1.4f)
                    .height(50.dp)
            ) {
                Text(
                    text = "Detener",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}







