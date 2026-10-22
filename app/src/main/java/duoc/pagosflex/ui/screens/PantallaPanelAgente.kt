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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import duoc.pagosflex.data.CompromisoRepositorio
import duoc.pagosflex.ui.components.BadgeEstado
import duoc.pagosflex.ui.components.TarjetaMetrica
import duoc.pagosflex.util.aMonedaCLP

data class TicketSoporte(
    val id: String,
    val usuario: String,
    val asunto: String,
    var estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPanelAgente() {
    val compromisos by CompromisoRepositorio.compromisos.collectAsState()
    val tickets = remember {
        mutableStateListOf(
            TicketSoporte("TK-101", "Juan Pérez", "Error al procesar transferencia", "Pendiente"),
            TicketSoporte("TK-102", "María González", "Comprobante no emitido en PDF", "En Revisión"),
            TicketSoporte("TK-103", "Carlos Rojas", "Solicitud cambio de fecha vencimiento", "Resuelto")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Panel de Control Agente PagoFlex", fontWeight = FontWeight.Bold) })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresPadding)
                .padding(16.dp)
                .verticalScrollIfNeeded(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica(
                    titulo = "Tickets Abiertos",
                    valor = tickets.count { it.estado != "Resuelto" }.toString(),
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

            Text(
                text = "Cuentas y Solicitudes de Clientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.height(180.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(compromisos) { comp ->
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

            Text(
                text = "Bandeja de Soporte de Usuarios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.height(220.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tickets) { item ->
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
                            Text("Usuario: ${item.usuario} • ID Ticket: ${item.id}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            if (item.estado != "Resuelto") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (item.estado == "Pendiente") {
                                        Button(
                                            onClick = { item.estado = "En Revisión" },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Pending, contentDescription = null)
                                            Spacer(modifier = Modifier.padding(start = 4.dp))
                                            Text("Atender")
                                        }
                                    }
                                    Button(
                                        onClick = { item.estado = "Resuelto" },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                        Spacer(modifier = Modifier.padding(start = 4.dp))
                                        Text("Resolver")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Modifier.verticalScrollIfNeeded(): Modifier {
    return this.verticalScroll(rememberScrollState())
}