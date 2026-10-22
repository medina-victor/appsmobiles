package duoc.pagosflex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import duoc.pagosflex.data.Compromiso
import duoc.pagosflex.data.CompromisoRepositorio
import duoc.pagosflex.ui.components.BotonPrimario
import duoc.pagosflex.ui.components.BotonVolver
import duoc.pagosflex.ui.components.CampoDeTexto
import duoc.pagosflex.ui.components.CampoRut

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAgregarCompromiso(
    alVolverAtras: () -> Unit
) {
    val categorias = listOf("Electricidad / Luz", "Internet / Fibra", "Telefonía / Móvil", "Agua", "Gas", "Dividendo / Hipotecario", "Otro")
    val proveedoresPorCategoria = mapOf(
        "Electricidad / Luz" to listOf("Chilectra", "CGE", "Enel", "Saesa"),
        "Internet / Fibra" to listOf("Movistar", "Entel", "Mundo Pacífico", "VTR", "Claro"),
        "Telefonía / Móvil" to listOf("WOM", "Claro", "Movistar", "Entel"),
        "Agua" to listOf("Aguas Andinas", "Esval", "Essbio", "Suralis"),
        "Gas" to listOf("Metrogas", "Abastible", "Lipigas", "Gasco"),
        "Dividendo / Hipotecario" to listOf("Banco Santander", "Banco de Chile", "BancoEstado", "Scotiabank"),
        "Otro" to listOf("Institución Genérica")
    )

    var categoriaSeleccionada by remember { mutableStateOf(categorias[0]) }
    var expandidoCategoria by remember { mutableStateOf(false) }

    val proveedoresDisponibles = proveedoresPorCategoria[categoriaSeleccionada] ?: emptyList()
    var proveedorSeleccionado by remember { mutableStateOf(proveedoresDisponibles.firstOrNull() ?: "") }
    var expandidoProveedor by remember { mutableStateOf(false) }

    var rutTitular by remember { mutableStateOf("") }
    var concepto by remember { mutableStateOf("") }
    var montoStr by remember { mutableStateOf("") }
    var vencimiento by remember { mutableStateOf("15/12/2026") }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(categoriaSeleccionada) {
        val lista = proveedoresPorCategoria[categoriaSeleccionada] ?: emptyList()
        proveedorSeleccionado = lista.firstOrNull() ?: ""
    }

    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        topBar = {
            BotonVolver(onVolver = alVolverAtras, titulo = "Agregar Nueva Cuenta o Servicio")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(valoresPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Seleccione Categoría de Servicio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                ExposedDropdownMenuBox(
                    expanded = expandidoCategoria,
                    onExpandedChange = { expandidoCategoria = !expandidoCategoria }
                ) {
                    OutlinedTextField(
                        value = categoriaSeleccionada,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoCategoria) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    )
                    ExposedDropdownMenu(
                        expanded = expandidoCategoria,
                        onDismissRequest = { expandidoCategoria = false }
                    ) {
                        categorias.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    categoriaSeleccionada = cat
                                    expandidoCategoria = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Seleccione Institución / Empresa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                ExposedDropdownMenuBox(
                    expanded = expandidoProveedor,
                    onExpandedChange = { expandidoProveedor = !expandidoProveedor }
                ) {
                    OutlinedTextField(
                        value = proveedorSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Proveedor / Empresa") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoProveedor) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    )
                    ExposedDropdownMenu(
                        expanded = expandidoProveedor,
                        onDismissRequest = { expandidoProveedor = false }
                    ) {
                        proveedoresDisponibles.forEach { prov ->
                            DropdownMenuItem(
                                text = { Text(prov) },
                                onClick = {
                                    proveedorSeleccionado = prov
                                    expandidoProveedor = false
                                }
                            )
                        }
                    }
                }

                CampoRut(
                    valor = rutTitular,
                    alCambiarValor = { rutTitular = it },
                    etiqueta = "RUT Asociado a la Boleta / Cuenta (Obligatorio)"
                )

                CampoDeTexto(
                    valor = concepto,
                    alCambiarValor = { concepto = it },
                    etiqueta = "N° de Cliente / Suministro",
                    icono = Icons.Default.Description
                )

                CampoDeTexto(
                    valor = montoStr,
                    alCambiarValor = { if (it.all { char -> char.isDigit() }) montoStr = it },
                    etiqueta = "Monto a Pagar ($ CLP)",
                    icono = Icons.Default.AttachMoney
                )

                CampoDeTexto(
                    valor = vencimiento,
                    alCambiarValor = { vencimiento = it },
                    etiqueta = "Fecha de Vencimiento (DD/MM/AAAA)",
                    icono = Icons.Default.DateRange
                )

                Spacer(modifier = Modifier.height(8.dp))

                BotonPrimario(
                    texto = "Registrar Cuenta",
                    onClick = {
                        val monto = montoStr.toDoubleOrNull() ?: 25000.0
                        val nuevoId = "C-" + (100..999).random()
                        val nuevoCompromiso = Compromiso(
                            id = nuevoId,
                            institucion = proveedorSeleccionado,
                            concepto = concepto.ifBlank { "$categoriaSeleccionada - $proveedorSeleccionado" },
                            monto = monto,
                            fechaVencimiento = vencimiento,
                            cuotaActual = 1,
                            totalCuotas = if (categoriaSeleccionada == "Dividendo / Hipotecario") 240 else 1,
                            estado = "Pendiente"
                        )
                        CompromisoRepositorio.agregarCompromiso(nuevoCompromiso)
                        alVolverAtras()
                    },
                    habilitado = rutTitular.isNotBlank() && montoStr.isNotBlank()
                )
            }
        }
    }
}