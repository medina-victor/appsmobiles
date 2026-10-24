package duoc.pagosflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.data.TicketRepositorio
import duoc.pagosflex.ui.components.BadgeEstado
import duoc.pagosflex.ui.components.BotonVolver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleTicket(
    idTicket: String,
    alVolverAtras: () -> Unit
) {
    val tickets by TicketRepositorio.tickets.collectAsState()
    val ticket = tickets.find { it.id == idTicket }
    val usuario by SesionUsuarioManager.usuarioActual.collectAsState()

    var nuevoMensaje by remember { mutableStateOf("") }
    val estaCerrado = ticket?.estado == "Resuelto" || ticket?.estado == "Cerrado"

    Scaffold(
        topBar = {
            BotonVolver(onVolver = alVolverAtras, titulo = "Detalle del Ticket ${ticket?.id ?: ""}")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        if (ticket == null) {
            Box(modifier = Modifier.fillMaxSize().padding(valoresPadding), contentAlignment = Alignment.Center) {
                Text("Ticket no encontrado.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresPadding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ticket.asunto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        BadgeEstado(ticket.estado)
                    }
                    Text("Usuario: ${ticket.usuario}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ticket.historialMensajes) { mensaje ->
                    val esMio = mensaje.autor == usuario.nombre || (!mensaje.esAgente && usuario.rol.name == "USUARIO_FINAL")
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (esMio) Alignment.End else Alignment.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (esMio) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = mensaje.autor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (esMio) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = mensaje.texto,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (esMio) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = mensaje.fecha,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (estaCerrado) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 4.dp
                ) {
                    Text(
                        text = "Este ticket se encuentra ${ticket.estado.lowercase()}. No se pueden enviar más mensajes.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = nuevoMensaje,
                            onValueChange = { nuevoMensaje = it },
                            label = { Text("Escribir mensaje...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(
                            onClick = {
                                if (nuevoMensaje.isNotBlank()) {
                                    val esAgente = usuario.rol.name != "USUARIO_FINAL"
                                    TicketRepositorio.agregarSeguimiento(ticket.id, if (esAgente) "Agente PagoFlex" else usuario.nombre, nuevoMensaje, esAgente)
                                    nuevoMensaje = ""
                                }
                            },
                            enabled = nuevoMensaje.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}