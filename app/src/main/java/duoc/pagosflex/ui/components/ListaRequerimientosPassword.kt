package duoc.pagosflex.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import duoc.pagosflex.ui.theme.EstadoExito

@Composable
fun ListaRequerimientosPassword(
    minChar: Boolean,
    mayus: Boolean,
    minus: Boolean,
    num: Boolean,
    esp: Boolean,
    coinciden: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "La contraseña debe contener:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        ItemRequerimientoIndividual(cumple = minChar, texto = "Mínimo 8 caracteres")
        ItemRequerimientoIndividual(cumple = mayus, texto = "Una letra mayúscula")
        ItemRequerimientoIndividual(cumple = minus, texto = "Una letra minúscula")
        ItemRequerimientoIndividual(cumple = num, texto = "Un número")
        ItemRequerimientoIndividual(cumple = esp, texto = "Un carácter especial (Ej: *, @, #, $)")
        ItemRequerimientoIndividual(cumple = coinciden, texto = "Las contraseñas deben coincidir")
    }
}

@Composable
private fun ItemRequerimientoIndividual(cumple: Boolean, texto: String) {
    val colorIcono by animateColorAsState(if (cumple) EstadoExito else Color.Gray, label = "")
    val colorTexto by animateColorAsState(if (cumple) MaterialTheme.colorScheme.onBackground else Color.Gray, label = "")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = colorIcono,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            fontSize = 13.sp,
            color = colorTexto,
            fontWeight = if (cumple) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}