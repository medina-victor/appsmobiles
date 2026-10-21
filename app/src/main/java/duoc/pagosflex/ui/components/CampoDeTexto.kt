package duoc.pagosflex.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CampoDeTexto(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    mensajeError: String? = null,
    opcionesTeclado: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiarValor,
        label = { Text(etiqueta) },
        leadingIcon = icono?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        isError = mensajeError != null,
        supportingText = {
            mensajeError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        },
        singleLine = true,
        keyboardOptions = opcionesTeclado,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error
        )
    )
}