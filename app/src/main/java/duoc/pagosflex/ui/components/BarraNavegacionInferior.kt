package duoc.pagosflex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            color = MaterialTheme.colorScheme.surface,
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
                ItemNavegacion(
                    icono = "🏠",
                    etiqueta = "Inicio",
                    activo = itemSeleccionado == 0,
                    onClick = { onTabSeleccionado(0) },
                    modifier = Modifier.weight(1f)
                )

                ItemNavegacion(
                    icono = "💳",
                    etiqueta = "Productos",
                    activo = itemSeleccionado == 1,
                    onClick = { onTabSeleccionado(1) },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.weight(1f))

                ItemNavegacion(
                    icono = "📊",
                    etiqueta = "Historial",
                    activo = itemSeleccionado == 3,
                    onClick = { onTabSeleccionado(3) },
                    modifier = Modifier.weight(1f)
                )

                ItemNavegacion(
                    icono = "⚙️",
                    etiqueta = "Ajustes",
                    activo = itemSeleccionado == 4,
                    onClick = { onTabSeleccionado(4) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

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
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚡", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Pagar",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
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
            color = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}