package duoc.pagosflex.ui.components
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CampoContrasena(
    valor: String,
    alCambiarValor: (String) -> Unit,
    etiqueta: String = "Contraseña",
    modifier: Modifier = Modifier,
    mensajeError: String? = null,
    opcionesTeclado: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
) {
    var mostrarContrasena by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = alCambiarValor,
        label = { Text(etiqueta) },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Lock, contentDescription = null)
        },
        trailingIcon = {
            IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                Icon(
                    imageVector = if (mostrarContrasena) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (mostrarContrasena) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        },
        visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
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