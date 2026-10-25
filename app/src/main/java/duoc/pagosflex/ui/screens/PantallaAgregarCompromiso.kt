package duoc.pagosflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.pagosflex.ui.components.BotonPrimario
import duoc.pagosflex.ui.components.BotonVolver
import duoc.pagosflex.viewmodel.AgregarCompromisoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAgregarCompromiso(
    alVolverAtras: () -> Unit,
    viewModel: AgregarCompromisoViewModel = viewModel()
) {
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsState()
    val empresaSeleccionada by viewModel.empresaSeleccionada.collectAsState()
    val numeroCliente by viewModel.numeroCliente.collectAsState()
    val rutTitular by viewModel.rutTitular.collectAsState()
    val monto by viewModel.monto.collectAsState()
    val fechaVencimiento by viewModel.fechaVencimiento.collectAsState()
    val cuotasTotal by viewModel.cuotasTotal.collectAsState()
    val errorFormulario by viewModel.errorFormulario.collectAsState()

    var expandidoCategoria by remember { mutableStateOf(false) }
    var expandidoEmpresa by remember { mutableStateOf(false) }

    val empresasPorCategoria = mapOf(
        "Luz" to listOf("Enel Distribución", "CGE Distribución", "Chilquinta Energía", "Saesa"),
        "Agua" to listOf("Aguas Andinas", "Esval", "Essbio", "Aguas del Valle"),
        "Internet / Teléfono" to listOf("Entel Chile", "Movistar Chile", "WOM", "VTR", "Claro Chile"),
        "Hipotecario" to listOf("BancoEstado Hipotecario", "Banco de Chile", "BCI Hipotecario", "Banco Santander"),
        "Educación" to listOf("Duoc UC Arancel", "Pontificia Univ. Católica", "Univ. de Chile", "Univ. Andrés Bello"),
        "Autopista / Tag" to listOf("Costanera Norte Tag", "Autopista Central Tag", "Vespucio Sur Tag"),
        "Otro" to listOf("Otros Servicios PagoFlex")
    )

    val categorias = empresasPorCategoria.keys.toList()
    val empresasDisponibles = empresasPorCategoria[categoriaSeleccionada] ?: listOf("Empresa de Servicio")

    val errorNumeroCliente = if (numeroCliente.isNotBlank() && !numeroCliente.all { it.isDigit() }) "El número de cliente solo debe contener números" else null

    Scaffold(
        topBar = {
            BotonVolver(onVolver = alVolverAtras, titulo = "Registrar Nueva Cuenta")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expandidoCategoria,
                onExpandedChange = { expandidoCategoria = !expandidoCategoria }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría de Servicio") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoCategoria) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandidoCategoria,
                    onDismissRequest = { expandidoCategoria = false }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                viewModel.categoriaSeleccionada.value = cat
                                viewModel.empresaSeleccionada.value = empresasPorCategoria[cat]?.firstOrNull() ?: "Empresa"
                                expandidoCategoria = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expandidoEmpresa,
                onExpandedChange = { expandidoEmpresa = !expandidoEmpresa }
            ) {
                OutlinedTextField(
                    value = empresaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Empresa o Institución Chilena") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoEmpresa) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandidoEmpresa,
                    onDismissRequest = { expandidoEmpresa = false }
                ) {
                    empresasDisponibles.forEach { emp ->
                        DropdownMenuItem(
                            text = { Text(emp) },
                            onClick = {
                                viewModel.empresaSeleccionada.value = emp
                                expandidoEmpresa = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = numeroCliente,
                onValueChange = { entrada ->
                    if (entrada.all { char -> char.isDigit() }) {
                        viewModel.numeroCliente.value = entrada
                    }
                },
                label = { Text("Número de Cliente / N° Cuenta (Solo Números)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errorNumeroCliente != null,
                supportingText = {
                    if (errorNumeroCliente != null) {
                        Text(text = errorNumeroCliente, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = rutTitular,
                onValueChange = { viewModel.alCambiarRut(it) },
                label = { Text("RUT del Titular de la Cuenta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("Solo números en cuerpo. K únicamente en dígito verificador.", style = MaterialTheme.typography.labelSmall)
                }
            )

            OutlinedTextField(
                value = monto,
                onValueChange = { entrada ->
                    if (entrada.all { char -> char.isDigit() }) {
                        viewModel.monto.value = entrada
                    }
                },
                label = { Text("Monto ($ CLP)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = fechaVencimiento,
                onValueChange = { viewModel.alCambiarFecha(it) },
                label = { Text("Fecha de Vencimiento (dd/mm/yyyy)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("Solo números. Formato automático dd/mm/yyyy (Día: 01-31, Mes: 01-12)", style = MaterialTheme.typography.labelSmall)
                }
            )

            OutlinedTextField(
                value = cuotasTotal,
                onValueChange = { entrada ->
                    if (entrada.all { char -> char.isDigit() }) {
                        viewModel.cuotasTotal.value = entrada
                    }
                },
                label = { Text("Total de Cuotas") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (errorFormulario != null) {
                Text(
                    text = errorFormulario!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            BotonPrimario(
                texto = "Registrar Cuenta",
                onClick = {
                    viewModel.guardarCompromiso(alExito = alVolverAtras)
                },
                habilitado = numeroCliente.isNotBlank() && monto.isNotBlank() && fechaVencimiento.length >= 10 && errorNumeroCliente == null
            )
        }
    }
}