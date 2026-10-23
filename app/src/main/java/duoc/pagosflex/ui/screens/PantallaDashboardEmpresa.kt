package duoc.pagosflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import duoc.pagosflex.ui.components.BadgeEstado
import duoc.pagosflex.ui.components.TarjetaMetrica
import java.text.NumberFormat
import java.util.Locale

data class CobroEmpresa(
    val id: String,
    val deudor: String,
    val rut: String,
    val monto: Double,
    val estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDashboardEmpresa() {
    val cobros = remember {
        mutableStateOf(
            listOf(
                CobroEmpresa("CB-901", "Juan Pérez Cotapos", "18.223.114-K", 145000.0, "Pagado"),
                CobroEmpresa("CB-902", "María González Silva", "15.882.301-4", 145000.0, "Pendiente"),
                CobroEmpresa("CB-903", "Carlos Rojas Vera", "19.102.883-2", 145000.0, "Vencido")
            )
        )
    }

    var mostrarDialogoNuevo by remember { mutableStateOf(false) }
    var nombreDeudor by remember { mutableStateOf("") }
    var rutDeudor by remember { mutableStateOf("") }
    var montoDeudor by remember { mutableStateOf("") }

    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    val totalRecaudado = cobros.value.filter { it.estado == "Pagado" }.sumOf { it.monto }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Panel Institución / Empresa", fontWeight = FontWeight.Bold) })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica(
                    titulo = "Recaudado Total",
                    valor = formatoMoneda.format(totalRecaudado),
                    icono = Icons.Default.AttachMoney,
                    modifier = Modifier.weight(1f)
                )
                TarjetaMetrica(
                    titulo = "Cobros Emitidos",
                    valor = cobros.value.size.toString(),
                    icono = Icons.Default.ReceiptLong,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Nómina de Cobros", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { mostrarDialogoNuevo = true },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text(" Nuevo Cobro")
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(cobros.value) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                Text("RUT: ${item.rut}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(formatoMoneda.format(item.monto), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                BadgeEstado(item.estado)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoNuevo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevo = false },
            title = { Text("Registrar Nuevo Cobro Corporativo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nombreDeudor,
                        onValueChange = { nombreDeudor = it },
                        label = { Text("Nombre del Deudor") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = rutDeudor,
                        onValueChange = { rutDeudor = it },
                        label = { Text("RUT del Deudor") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = montoDeudor,
                        onValueChange = { if (it.all { char -> char.isDigit() }) montoDeudor = it },
                        label = { Text("Monto ($ CLP)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val monto = montoDeudor.toDoubleOrNull() ?: 50000.0
                        val nuevoId = "CB-" + (904..999).random()
                        val nuevoCobro = CobroEmpresa(
                            id = nuevoId,
                            deudor = nombreDeudor.ifBlank { "Cliente Empresa" },
                            rut = rutDeudor.ifBlank { "12.345.678-9" },
                            monto = monto,
                            estado = "Pendiente"
                        )
                        cobros.value = listOf(nuevoCobro) + cobros.value
                        nombreDeudor = ""
                        rutDeudor = ""
                        montoDeudor = ""
                        mostrarDialogoNuevo = false
                    },
                    enabled = nombreDeudor.isNotBlank() && montoDeudor.isNotBlank()
                ) {
                    Text("Emitir Cobro")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoNuevo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}