package duoc.pagosflex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import duoc.pagosflex.ui.theme.*

@Composable
fun BarraNavegacionInferior(
    itemSeleccionado: Int = 0,
    onTabSeleccionado: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Fondo blanco de la barra
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            color = Blanco,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(top = 12.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item 0: Inicio
                ItemNavegacion(
                    icono = "🏠",
                    etiqueta = "Inicio",
                    activo = itemSeleccionado == 0,
                    onClick = { onTabSeleccionado(0) },
                    modifier = Modifier.weight(1f)
                )

                // Item 1: Productos
                ItemNavegacion(
                    icono = "💳",
                    etiqueta = "Productos",
                    activo = itemSeleccionado == 1,
                    onClick = { onTabSeleccionado(1) },
                    modifier = Modifier.weight(1f)
                )

                // Espacio reservado para el botón central de Pagar
                Spacer(modifier = Modifier.weight(1f))

                // Item 3: Historial
                ItemNavegacion(
                    icono = "📊",
                    etiqueta = "Historial",
                    activo = itemSeleccionado == 3,
                    onClick = { onTabSeleccionado(3) },
                    modifier = Modifier.weight(1f)
                )

                // Item 4: Ajustes
                ItemNavegacion(
                    icono = "⚙️",
                    etiqueta = "Ajustes",
                    activo = itemSeleccionado == 4,
                    onClick = { onTabSeleccionado(4) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- BOTÓN FLOTANTE CENTRAL ("Pagar Rápido") ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset(y = (-18).dp)
                .clickable { onTabSeleccionado(2) }
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Primario),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚡", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Pagar",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Primario
            )
        }
    }
}

@Composable
private fun ItemNavegacion(
    icono: String,
    etiqueta: String,
    activo: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Text(text = icono, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = etiqueta,
            fontSize = 10.sp,
            fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal,
            color = if (activo) Primario else TextoGris
        )
    }
}