package duoc.pagosflex.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.data.TicketRepositorio
import duoc.pagosflex.ui.components.BadgeEstado
import duoc.pagosflex.ui.components.BotonVolver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAyuda(
    alVolverAtras: () -> Unit,
    alNavegarADetalle: (String) -> Unit
) {
    val usuario by SesionUsuarioManager.usuarioActual.collectAsState()
    val tickets by TicketRepositorio.tickets.collectAsState()

    var mostrarDialogoTicket by remember { mutableStateOf(false) }
    var asuntoTicket by remember { mutableStateOf("") }
    var mensajeTicket by remember { mutableStateOf("") }

    val ticketsUsuario = tickets.filter { it.usuario == usuario.nombre || usuario.nombre.isBlank() || it.usuario == "Camila Rojas" }

    Scaffold(
        topBar = {
            BotonVolver(onVolver = alVolverAtras, titulo = "Centro de Ayuda y Soporte")
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis Tickets de Soporte",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { mostrarDialogoTicket = true },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text(" Nuevo Ticket")
                    }
                }
            }

            items(ticketsUsuario) { ticket ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { alNavegarADetalle(ticket.id) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ticket.asunto, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            BadgeEstado(ticket.estado)
                        }
                        Text("Mensaje: ${ticket.mensaje}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (ticket.respuesta.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Respuesta Agente: ${ticket.respuesta}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Text("Toca para abrir chat / seguimiento", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            item {
                Text(
                    text = "Preguntas Frecuentes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("¿Cómo se procesan los pagos?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Mediante pasarela segura simulada para fines académicos (DSY1105).", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (mostrarDialogoTicket) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoTicket = false },
            title = { Text("Crear Ticket de Soporte") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = asuntoTicket,
                        onValueChange = { asuntoTicket = it },
                        label = { Text("Asunto") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = mensajeTicket,
                        onValueChange = { mensajeTicket = it },
                        label = { Text("Describe tu consulta o problema") },
                        modifier = Modifier.height(120.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        TicketRepositorio.crearTicket(
                            usuario = if (usuario.nombre.isNotBlank()) usuario.nombre else "Usuario Cliente",
                            asunto = asuntoTicket,
                            mensaje = mensajeTicket
                        )
                        asuntoTicket = ""
                        mensajeTicket = ""
                        mostrarDialogoTicket = false
                    },
                    enabled = asuntoTicket.isNotBlank() && mensajeTicket.isNotBlank()
                ) {
                    Text("Enviar Ticket")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoTicket = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}