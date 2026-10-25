package duoc.pagosflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.pagosflex.data.CompromisoRepositorio
import duoc.pagosflex.data.TicketRepositorio
import duoc.pagosflex.data.TicketSoporteDto
import duoc.pagosflex.ui.components.BadgeEstado
import duoc.pagosflex.ui.components.TarjetaMetrica
import duoc.pagosflex.util.aMonedaCLP
import duoc.pagosflex.viewmodel.PanelAgenteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPanelAgente(
    alAbrirDrawer: () -> Unit = {},
    viewModel: PanelAgenteViewModel = viewModel()
) {
    val compromisos by viewModel.compromisos.collectAsState()
    val tickets by viewModel.tickets.collectAsState()

    var ticketSeleccionado by remember { mutableStateOf<TicketSoporteDto?>(null) }
    var textoRespuesta by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Control Agente", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = alAbrirDrawer) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
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
                        titulo = "Tickets Abiertos",
                        valor = tickets.count { it.estado != "Resuelto" && it.estado != "Cerrado" }.toString(),
                        icono = Icons.Default.ReportProblem,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaMetrica(
                        titulo = "Cuentas Registradas",
                        valor = compromisos.size.toString(),
                        icono = Icons.Default.AssignmentTurnedIn,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "Cuentas y Solicitudes de Clientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (compromisos.isEmpty()) {
                item {
                    Text("No hay cuentas registradas.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(compromisos, key = { it.id }) { comp ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(comp.institucion, fontWeight = FontWeight.Bold)
                                Text(comp.concepto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(comp.monto.aMonedaCLP(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bandeja de Soporte de Usuarios",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (tickets.isEmpty()) {
                item {
                    Text("No hay tickets de soporte.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(tickets, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.asunto, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                BadgeEstado(item.estado)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Usuario: ${item.usuario} • ID: ${item.id}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Mensaje: ${item.mensaje}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))

                            if (item.respuesta.isNotBlank()) {
                                Text("Respuesta: ${item.respuesta}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        ticketSeleccionado = item
                                        textoRespuesta = item.respuesta
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Pending, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.padding(start = 4.dp))
                                    Text("Responder")
                                }
                                if (item.estado != "Resuelto") {
                                    OutlinedButton(
                                        onClick = { TicketRepositorio.cambiarEstadoTicket(item.id, "Resuelto") },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.padding(start = 4.dp))
                                        Text("Resolver")
                                    }
                                }
                                if (item.estado != "Cerrado") {
                                    OutlinedButton(
                                        onClick = { TicketRepositorio.cambiarEstadoTicket(item.id, "Cerrado") },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.padding(start = 4.dp))
                                        Text("Cerrar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (ticketSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { ticketSeleccionado = null },
            title = { Text("Responder Ticket ${ticketSeleccionado?.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Usuario: ${ticketSeleccionado?.usuario}", fontWeight = FontWeight.Bold)
                    Text("Asunto: ${ticketSeleccionado?.asunto}")
                    Text("Mensaje: ${ticketSeleccionado?.mensaje}")
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = textoRespuesta,
                        onValueChange = { textoRespuesta = it },
                        label = { Text("Escribir respuesta al cliente...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        ticketSeleccionado?.let {
                            viewModel.responderTicket(it.id, textoRespuesta, "En Proceso")
                        }
                        ticketSeleccionado = null
                        textoRespuesta = ""
                    },
                    enabled = textoRespuesta.isNotBlank()
                ) {
                    Text("Enviar (En Proceso)")
                }
            },
            dismissButton = {
                TextButton(onClick = { ticketSeleccionado = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}