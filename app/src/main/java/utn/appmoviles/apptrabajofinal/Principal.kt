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
fun MostrarPantallaPrincipal() {
    PantallaPrincipal(null)
}

@Composable
fun PantallaPrincipal(navigationController: NavHostController?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF5F5F5)) // Fondo suave gris claro
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Sonidos Detectados",
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333) // Color de texto más elegante
        )

        Spacer(modifier = Modifier.padding(24.dp))

        // Lista de Sonidos
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            SonidoItem(Sonido("Sirena Policía", Color(0xFF3F51B5))) // Azul elegante
            SonidoItem(Sonido("Bocina Auto", Color(0xFF795548)))    // Marrón oscuro
            SonidoItem(Sonido("Bocina Camión", Color(0xFF4CAF50)))  // Verde suave
            SonidoItem(Sonido("Sirena Bomberos", Color(0xFFD32F2F))) // Rojo intenso
        }

        Spacer(modifier = Modifier.padding(25.dp))

        // Botones de Control
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { /* Acción iniciar */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(130.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Iniciar",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Button(
                onClick = { /* Acción detener */ },
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

        // Botón de Configuración
        Button(
            onClick = { navigationController?.navigate(Routes.PantallaConfiguracion.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier.padding(top = 24.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "Configuración",
                tint = Color(0xFF666666),
                modifier = Modifier.size(35.dp)
            )
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
        colors = CardDefaults.cardColors(containerColor = Color.White), // Corregido
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

