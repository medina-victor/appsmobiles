package duoc.pagosflex.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BotonTerminosYCondiciones(
    aceptado: Boolean,
    alCambiarAceptado: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { alCambiarAceptado(!aceptado) }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = aceptado,
            onCheckedChange = alCambiarAceptado,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Acepto los términos, condiciones y políticas de privacidad (Ley 19.628)",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}