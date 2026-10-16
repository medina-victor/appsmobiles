package duoc.pagosflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.pagosflex.model.Rol
import duoc.pagosflex.ui.theme.*
import duoc.pagosflex.viewmodel.RegistroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(
    onVolver: () -> Unit,
    onRegistroExitoso: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
            .padding(Dimens.espacioPantalla)
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVolver) {
                Text(text = "←", fontSize = 20.sp, color = Primario)
            }
            Text(text = "Registro de Usuario", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
        }

        Spacer(modifier = Modifier.height(Dimens.espacioMedio))

        Text(text = "CREAR CUENTA", fontSize = 11.sp, color = Primario, fontWeight = FontWeight.Bold)
        Text(text = "Únete a PagoFlex", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Primario)
        Spacer(modifier = Modifier.height(Dimens.espacioChico))
        Text(
            text = "Selecciona tu perfil de usuario y completa los datos requeridos.",
            fontSize = 13.sp,
            color = TextoGris
        )

        Spacer(modifier = Modifier.height(Dimens.espacioMedio))

        // Selector de Rol (Segmented / Buttons)
        Text(text = "Selecciona tu Rol:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextoGris)
        Spacer(modifier = Modifier.height(Dimens.espacioChico))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                Rol.CLIENTE to "Usuario Final",
                Rol.AGENTE to "Agente PF",
                Rol.EJECUTIVO to "Ejecutivo"
            ).forEach { (rolItem, etiqueta) ->
                val esSeleccionado = state.rolSeleccionado == rolItem
                Button(
                    onClick = { viewModel.onRolCambiado(rolItem) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (esSeleccionado) Primario else PrimarioClaro,
                        contentColor = if (esSeleccionado) Blanco else Primario
                    ),
                    shape = RoundedCornerShape(Dimens.radioCampo)
                ) {
                    Text(text = etiqueta, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.espacioGrande))

        // Formulario Dinámico según el Rol
        OutlinedTextField(
            value = state.nombre,
            onValueChange = viewModel::onNombreCambiado,
            label = { Text("Nombre completo *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(Dimens.espacioChico))

        OutlinedTextField(
            value = state.rut,
            onValueChange = viewModel::onRutCambiado,
            label = { Text("RUT (ej: 12.345.678-9) *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(Dimens.espacioChico))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailCambiado,
            label = { Text(if (state.rolSeleccionado == Rol.CLIENTE) "Correo electrónico *" else "Correo corporativo *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(Dimens.espacioChico))

        // CAMPOS ESPECÍFICOS SEGÚN ROL
        when (state.rolSeleccionado) {
            Rol.CLIENTE -> {
                OutlinedTextField(
                    value = state.telefono,
                    onValueChange = viewModel::onTelefonoCambiado,
                    label = { Text("Teléfono / Celular") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            Rol.AGENTE -> {
                OutlinedTextField(
                    value = state.areaOEmpresa,
                    onValueChange = viewModel::onAreaOEmpresaCambiado,
                    label = { Text("Área / Departamento (ej: Soporte, Comercial)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(Dimens.espacioChico))
                OutlinedTextField(
                    value = state.codigoAcceso,
                    onValueChange = viewModel::onCodigoAccesoCambiado,
                    label = { Text("Código de Agente / Credencial *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            Rol.EJECUTIVO -> {
                OutlinedTextField(
                    value = state.areaOEmpresa,
                    onValueChange = viewModel::onAreaOEmpresaCambiado,
                    label = { Text("Empresa Cliente / Razón Social *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(Dimens.espacioChico))
                OutlinedTextField(
                    value = state.telefono,
                    onValueChange = viewModel::onTelefonoCambiado,
                    label = { Text("Teléfono de contacto de Empresa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        state.errorMensaje?.let { error ->
            Spacer(modifier = Modifier.height(Dimens.espacioMedio))
            Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(Dimens.espacioGrande))

        Button(
            onClick = { viewModel.registrarCuenta(onRegistroExitoso) },
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.alturaBoton),
            shape = RoundedCornerShape(Dimens.radioBoton),
            colors = ButtonDefaults.buttonColors(containerColor = Primario)
        ) {
            Text(text = "Crear Cuenta e Iniciar", color = Blanco, fontWeight = FontWeight.Bold)
        }
    }
}