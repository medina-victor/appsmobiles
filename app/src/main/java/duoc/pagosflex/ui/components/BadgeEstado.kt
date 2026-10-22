package duoc.pagosflex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BadgeEstado(estado: String) {
    val (colorFondo, colorTexto) = when (estado) {
        "Pagado", "Resuelto" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        "Pendiente", "En Revisión" -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
        "Vencido" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        else -> Color(0xFFEEEEEE) to Color(0xFF616161)
    }

    Box(
        modifier = Modifier
            .background(colorFondo, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = estado,
            color = colorTexto,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

