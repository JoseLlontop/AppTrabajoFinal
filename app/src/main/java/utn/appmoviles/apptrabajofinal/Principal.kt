package utn.appmoviles.apptrabajofinal

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import utn.appmoviles.apptrabajofinal.model.Routes
import utn.appmoviles.apptrabajofinal.ui.theme.colorBoton
import utn.appmoviles.apptrabajofinal.ui.theme.colorTitulo


data class Sonido(val nombre: String, val color: Color)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaDeteccion() {
    PantallaPrincipal(
        audioCategory = "Sirena Policía",
        onStartListening = { /* Acción iniciar detección */ },
        onStopListening = { /* Acción detener detección */ },
        navigationController = null // Puedes pasar null para el preview
    )
}

@Composable
fun PantallaPrincipal(
    navigationController: NavHostController?,
    audioCategory: String,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = audioCategory,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3F51B5)
        )

        Spacer(modifier = Modifier.padding(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (!isRecording) {
                        isRecording = true
                        onStartListening()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color(0xFFFFC107) else Color(0xFF6200EA)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(130.dp)
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
                        onStopListening()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(130.dp)
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



@Composable
fun SonidoItem(sonido: Sonido) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .shadow(4.dp, shape = RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = sonido.nombre,
                fontSize = 20.sp,
                color = sonido.color,
                fontWeight = FontWeight.Medium
            )

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(sonido.color, shape = CircleShape)
            )
        }
    }
}



