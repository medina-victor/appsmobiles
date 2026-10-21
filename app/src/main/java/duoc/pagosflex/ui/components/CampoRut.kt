package duoc.pagosflex.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun CampoRut(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String,
    mensajeError: String? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiarValor,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
        isError = mensajeError != null,
        supportingText = {
            if (mensajeError != null) {
                Text(text = mensajeError, color = MaterialTheme.colorScheme.error)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

