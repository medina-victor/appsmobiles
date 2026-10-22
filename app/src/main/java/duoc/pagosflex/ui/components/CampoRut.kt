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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class RutVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val clean = text.text.replace(Regex("[^0-9kK]"), "").uppercase()
        val formatted = if (clean.length > 1) {
            val body = clean.dropLast(1)
            val dv = clean.last()
            val formattedBody = body.reversed().chunked(3).joinToString(".").reversed()
            "$formattedBody-$dv"
        } else {
            clean
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                return formatted.length.coerceAtMost(offset + (formatted.length - clean.length))
            }

            override fun transformedToOriginal(offset: Int): Int {
                return offset.coerceAtMost(text.text.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

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
        onValueChange = { input ->
            val filtered = input.replace(Regex("[^0-9kK.-]"), "").uppercase()
            alCambiarValor(filtered)
        },
        label = { Text(etiqueta) },
        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
        visualTransformation = RutVisualTransformation(),
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