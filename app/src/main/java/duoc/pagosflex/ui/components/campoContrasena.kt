package duoc.pagosflex.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

import duoc.pagosflex.R
import duoc.pagosflex.ui.styles.EstiloCampo
import duoc.pagosflex.ui.theme.Dimens




@Composable
fun CampoContrasena(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    visible: Boolean,
    onAlternarVisible: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        modifier = modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_candado),
                contentDescription = null,
                modifier = Modifier.size(Dimens.tamanoIcono)
            )
        },
        trailingIcon = {
            IconButton(onClick = onAlternarVisible) {
                Icon(
                    painter = painterResource(
                        if (visible) R.drawable.ic_ojo_tachado else R.drawable.ic_ojo
                    ),
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                    modifier = Modifier.size(Dimens.tamanoIcono)
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = EstiloCampo.forma,
        colors = EstiloCampo.colores()
    )
}