package duoc.pagosflex.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import duoc.pagosflex.ui.theme.Dimens
import duoc.pagosflex.ui.theme.Primario
import duoc.pagosflex.ui.theme.TextoOscuro

@Composable
fun BotonVolver(
    onVolver: () -> Unit,
    titulo: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.espacioChico),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onVolver,
            modifier = Modifier.size(48.dp) // Tamaño del área táctil estándar
        ) {
            Text(
                text = "←",
                fontSize = 28.sp, // Flecha más grande y legible
                fontWeight = FontWeight.Bold,
                color = Primario
            )
        }

        if (!titulo.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(Dimens.espacioChico))
            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
        }
    }
}