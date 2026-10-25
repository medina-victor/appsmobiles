package duoc.pagosflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.pagosflex.data.CobroEmpresaDetalle
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.ui.components.BadgeEstado
import duoc.pagosflex.ui.components.TarjetaMetrica
import duoc.pagosflex.util.aMonedaCLP
import duoc.pagosflex.viewmodel.EmpresaViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDashboardEmpresa(
    alAbrirDrawer: () -> Unit = {},
    abrirDialogoNuevo: Boolean = false,
    pestanaInicial: Int = 0,
    alCerrarDialogoNuevo: () -> Unit = {},
    viewModel: EmpresaViewModel = viewModel()
) {
    val cobros by viewModel.cobros.collectAsState()
    val cobrosFiltrados by viewModel.cobrosFiltrados.collectAsState()
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val filtroEstado by viewModel.filtroEstado.collectAsState()

    val usuario by SesionUsuarioManager.usuarioActual.collectAsState()

    var pestanaSeleccionada by remember { mutableStateOf(pestanaInicial) }
    var mostrarDialogoNuevo by remember { mutableStateOf(abrirDialogoNuevo) }
    var mostrarDialogoCargaMasiva by remember { mutableStateOf(false) }
    var mostrarDialogoReporte by remember { mutableStateOf(false) }
    var cobroSeleccionado by remember { mutableStateOf<CobroEmpresaDetalle?>(null) }
    var mostrarDialogoAnular by remember { mutableStateOf(false) }

    LaunchedEffect(abrirDialogoNuevo, pestanaInicial) {
        if (abrirDialogoNuevo) {
            mostrarDialogoNuevo = true
        }
        pestanaSeleccionada = pestanaInicial
    }

    var nombreDeudor by remember { mutableStateOf("") }
    var rutDeudor by remember { mutableStateOf("") }
    var correoDeudor by remember { mutableStateOf("") }
    var telefonoDeudor by remember { mutableStateOf("") }
    var conceptoCobro by remember { mutableStateOf("") }
    var montoCobro by remember { mutableStateOf("") }
    var fechaVenc by remember { mutableStateOf("") }
    var cuotaActual by remember { mutableStateOf("1") }
    var totalCuotas by remember { mutableStateOf("1") }

    val errorNombre = if (nombreDeudor.any { it.isDigit() }) "El nombre solo debe contener letras" else null
    val errorTelefono = if (telefonoDeudor.any { !it.isDigit() && it != '+' }) "El teléfono solo debe contener números" else null
    val errorMonto = if (montoCobro.any { !it.isDigit() }) "El monto solo debe contener números" else null
    val errorCorreo = if (correoDeudor.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(correoDeudor).matches()) "Correo electrónico inválido" else null

    var motivoAnulacion by remember { mutableStateOf("Repactación") }
    var comentarioAnulacion by remember { mutableStateOf("") }

    val totalRecaudado = cobros.filter { it.estado == "Pagado" }.sumOf { it.monto }
    val totalEmitido = cobros.sumOf { it.monto }
    val pagadosCount = cobros.count { it.estado == "Pagado" }
    val pendientesCount = cobros.count { it.estado == "Pendiente" }
    val vencidosCount = cobros.count { it.estado == "Vencido" }
    val revisionCount = cobros.count { it.estado == "En revisión" }
    val anuladosCount = cobros.count { it.estado == "Anulado" }
    val totalMes = cobros.size
    val denominadorTasa = totalMes - anuladosCount
    val tasaCobranza = if (denominadorTasa > 0) (pagadosCount.toDouble() / denominadorTasa.toDouble()) * 100.0 else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Ejecutivo Corporativo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = alAbrirDrawer) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaMetrica(
                        titulo = "Recaudado Total",
                        valor = totalRecaudado.aMonedaCLP(),
                        icono = Icons.Default.AttachMoney,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaMetrica(
                        titulo = "Tasa Cobranza",
                        valor = String.format(Locale("es", "CL"), "%.1f%%", tasaCobranza),
                        icono = Icons.Default.ReceiptLong,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                PrimaryTabRow(selectedTabIndex = pestanaSeleccionada) {
                    Tab(
                        selected = pestanaSeleccionada == 0,
                        onClick = { pestanaSeleccionada = 0 },
                        text = { Text("Nómina") }
                    )
                    Tab(
                        selected = pestanaSeleccionada == 1,
                        onClick = { pestanaSeleccionada = 1 },
                        text = { Text("Carga Masiva") }
                    )
                    Tab(
                        selected = pestanaSeleccionada == 2,
                        onClick = { pestanaSeleccionada = 2 },
                        text = { Text("Reportes & Gráficos") }
                    )
                }
            }

            when (pestanaSeleccionada) {
                0 -> {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Gestión de Compromisos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = { mostrarDialogoNuevo = true },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Text(" Nuevo Cobro")
                                }
                            }

                            OutlinedTextField(
                                value = textoBusqueda,
                                onValueChange = { viewModel.textoBusqueda.value = it },
                                label = { Text("Buscar por nombre o RUT...") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val filtros = listOf("Todos", "Pendiente", "Vencido", "Pagado", "En revisión", "Anulado")
                                items(filtros) { filtro ->
                                    FilterChip(
                                        selected = filtroEstado == filtro,
                                        onClick = { viewModel.filtroEstado.value = filtro },
                                        label = { Text(filtro) }
                                    )
                                }
                            }
                        }
                    }

                    if (cobrosFiltrados.isEmpty()) {
                        item {
                            Text("No se encontraron compromisos de pago.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        items(cobrosFiltrados, key = { it.id }) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { cobroSeleccionado = item },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.deudor, fontWeight = FontWeight.Bold)
                                        Text("RUT: ${item.rut} • ${item.concepto}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Vence: ${item.fechaVencimiento} (Cuota ${item.cuotaActual}/${item.totalCuotas})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(item.monto.aMonedaCLP(), fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        BadgeEstado(item.estado)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Carga Masiva de Convenios y Cobros",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Suba un archivo en formato CSV o Excel (.xlsx) con la nómina de clientes y montos a cobrar.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { mostrarDialogoCargaMasiva = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null)
                                Text(" Seleccionar Archivo Nómina")
                            }
                        }
                    }
                }
                2 -> {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Reportes Gráficos y Estadísticas de Cobranza",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Indicadores Financieros Globales", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text("Total Emitido: ${totalEmitido.aMonedaCLP()}")
                                    Text("Total Recaudado: ${totalRecaudado.aMonedaCLP()}")
                                    Text("Tasa de Cobranza: ${String.format(Locale("es", "CL"), "%.1f%%", tasaCobranza)}")

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Progreso de Tasa de Cobranza", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    LinearProgressIndicator(
                                        progress = { (tasaCobranza / 100.0).toFloat().coerceIn(0f, 1f) },
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    )
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Distribución de Estados de Cobros ($totalMes total)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

                                    val maxVal = if (totalMes > 0) totalMes.toFloat() else 1f

                                    GraficoBarraEstado("Pagados ($pagadosCount)", pagadosCount.toFloat(), maxVal, Color(0xFF2E7D32))
                                    GraficoBarraEstado("Pendientes ($pendientesCount)", pendientesCount.toFloat(), maxVal, Color(0xFFF57F17))
                                    GraficoBarraEstado("Vencidos ($vencidosCount)", vencidosCount.toFloat(), maxVal, Color(0xFFC62828))
                                    GraficoBarraEstado("En Revisión ($revisionCount)", revisionCount.toFloat(), maxVal, Color(0xFF0277BD))
                                    GraficoBarraEstado("Anulados ($anuladosCount)", anuladosCount.toFloat(), maxVal, Color(0xFF616161))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { mostrarDialogoReporte = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null)
                                Spacer(modifier = Modifier.padding(start = 8.dp))
                                Text("Exportar Reporte de Cobranza")
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoNuevo) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoNuevo = false
                alCerrarDialogoNuevo()
            },
            title = { Text("Registrar Nuevo Compromiso") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nombreDeudor,
                        onValueChange = { nombreDeudor = it },
                        label = { Text("Nombre de la Persona") },
                        singleLine = true,
                        isError = errorNombre != null,
                        supportingText = {
                            if (errorNombre != null) {
                                Text(text = errorNombre, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    OutlinedTextField(
                        value = rutDeudor,
                        onValueChange = { rutDeudor = it },
                        label = { Text("RUT de la Persona") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = correoDeudor,
                        onValueChange = { correoDeudor = it },
                        label = { Text("Correo Electrónico") },
                        singleLine = true,
                        isError = errorCorreo != null,
                        supportingText = {
                            if (errorCorreo != null) {
                                Text(text = errorCorreo, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    OutlinedTextField(
                        value = telefonoDeudor,
                        onValueChange = { telefonoDeudor = it },
                        label = { Text("Teléfono de Contacto") },
                        singleLine = true,
                        isError = errorTelefono != null,
                        supportingText = {
                            if (errorTelefono != null) {
                                Text(text = errorTelefono, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    OutlinedTextField(value = conceptoCobro, onValueChange = { conceptoCobro = it }, label = { Text("Concepto del Cobro") }, singleLine = true)
                    OutlinedTextField(
                        value = montoCobro,
                        onValueChange = { montoCobro = it },
                        label = { Text("Monto ($ CLP)") },
                        singleLine = true,
                        isError = errorMonto != null,
                        supportingText = {
                            if (errorMonto != null) {
                                Text(text = errorMonto, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    OutlinedTextField(value = fechaVenc, onValueChange = { fechaVenc = it }, label = { Text("Fecha Vencimiento (dd/MM/yyyy)") }, singleLine = true)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = cuotaActual, onValueChange = { if (it.all { c -> c.isDigit() }) cuotaActual = it }, label = { Text("N° Cuota") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = totalCuotas, onValueChange = { if (it.all { c -> c.isDigit() }) totalCuotas = it }, label = { Text("Total Cuotas") }, modifier = Modifier.weight(1f), singleLine = true)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val monto = montoCobro.toDoubleOrNull() ?: 50000.0
                        viewModel.registrarCobro(
                            deudor = nombreDeudor,
                            rut = rutDeudor,
                            correo = correoDeudor,
                            telefono = telefonoDeudor,
                            concepto = conceptoCobro,
                            monto = monto,
                            fechaVencimiento = fechaVenc,
                            cuotaActual = cuotaActual.toIntOrNull() ?: 1,
                            totalCuotas = totalCuotas.toIntOrNull() ?: 1
                        )
                        nombreDeudor = ""
                        rutDeudor = ""
                        correoDeudor = ""
                        telefonoDeudor = ""
                        conceptoCobro = ""
                        montoCobro = ""
                        fechaVenc = ""
                        mostrarDialogoNuevo = false
                        alCerrarDialogoNuevo()
                    },
                    enabled = nombreDeudor.isNotBlank() && rutDeudor.isNotBlank() && montoCobro.isNotBlank() && fechaVenc.isNotBlank() &&
                            errorNombre == null && errorTelefono == null && errorMonto == null && errorCorreo == null
                ) {
                    Text("Registrar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoNuevo = false
                        alCerrarDialogoNuevo()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (cobroSeleccionado != null) {
        val item = cobroSeleccionado!!
        AlertDialog(
            onDismissRequest = { cobroSeleccionado = null },
            title = { Text("Detalle de Compromiso ${item.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Deudor: ${item.deudor}", fontWeight = FontWeight.Bold)
                    Text("RUT: ${item.rut}")
                    Text("Contacto: ${item.correo} • ${item.telefono}")
                    Text("Concepto: ${item.concepto}")
                    Text("Monto: ${item.monto.aMonedaCLP()}")
                    Text("Vencimiento: ${item.fechaVencimiento} (Cuota ${item.cuotaActual}/${item.totalCuotas})")
                    Text("Estado: ${item.estado}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    if (item.estado == "Anulado") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Motivo anulación: ${item.motivoAnulacion}", style = MaterialTheme.typography.bodySmall)
                        Text("Anulado por: ${item.anuladoPor} el ${item.fechaAnulacion}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                if (item.estado != "Pagado" && item.estado != "Anulado") {
                    Button(
                        onClick = { mostrarDialogoAnular = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.padding(start = 4.dp))
                        Text("Anular Compromiso")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { cobroSeleccionado = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (mostrarDialogoAnular && cobroSeleccionado != null) {
        val item = cobroSeleccionado!!
        AlertDialog(
            onDismissRequest = { mostrarDialogoAnular = false },
            title = { Text("Anular Compromiso") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Seleccione motivo obligatorio:")
                    listOf("Repactación", "Error de registro", "Pagado directamente a la empresa", "Otro").forEach { motivo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { motivoAnulacion = motivo }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (motivoAnulacion == motivo) "• $motivo" else "  $motivo", fontWeight = if (motivoAnulacion == motivo) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                    if (motivoAnulacion == "Otro") {
                        OutlinedTextField(
                            value = comentarioAnulacion,
                            onValueChange = { comentarioAnulacion = it },
                            label = { Text("Especifique comentario...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val motivoFinal = if (motivoAnulacion == "Otro") "Otro: $comentarioAnulacion" else motivoAnulacion
                        viewModel.anularCobro(item.id, motivoFinal, usuario.nombre.ifBlank { "Ejecutivo Empresa" })
                        mostrarDialogoAnular = false
                        cobroSeleccionado = null
                        comentarioAnulacion = ""
                    }
                ) {
                    Text("Confirmar Anulación")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoAnular = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarDialogoCargaMasiva) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCargaMasiva = false },
            title = { Text("Carga Masiva Exitosa") },
            text = { Text("Se han procesado 15 registros de deudores desde la nómina corporativa con éxito.") },
            confirmButton = {
                Button(onClick = { mostrarDialogoCargaMasiva = false }) {
                    Text("Aceptar")
                }
            }
        )
    }

    if (mostrarDialogoReporte) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoReporte = false },
            title = { Text("Reporte Exportado") },
            text = { Text("El reporte financiero institucional ha sido generado y descargado con éxito en formato PDF.") },
            confirmButton = {
                Button(onClick = { mostrarDialogoReporte = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
fun GraficoBarraEstado(
    titulo: String,
    valor: Float,
    maximo: Float,
    colorBarra: Color
) {
    val porcentaje = if (maximo > 0f) (valor / maximo).coerceIn(0f, 1f) else 0f

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = titulo, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text(text = "${valor.toInt()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.4f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(porcentaje)
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(colorBarra)
            )
        }
    }
}