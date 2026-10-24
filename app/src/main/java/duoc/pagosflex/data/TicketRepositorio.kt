package duoc.pagosflex.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MensajeTicket(
    val autor: String,
    val texto: String,
    val esAgente: Boolean,
    val fecha: String
)

data class TicketSoporteDto(
    val id: String,
    val usuario: String,
    val asunto: String,
    val mensaje: String,
    val respuesta: String = "",
    val estado: String = "Pendiente",
    val historialMensajes: MutableList<MensajeTicket> = mutableListOf()
)

object TicketRepositorio {
    private val fechaInicial = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date())

    private val _tickets = MutableStateFlow(
        mutableListOf(
            TicketSoporteDto(
                id = "TK-101",
                usuario = "Camila Rojas",
                asunto = "Error al procesar transferencia",
                mensaje = "No puedo completar mi pago de arancel.",
                respuesta = "Verificando con la pasarela de pagos.",
                estado = "En Proceso",
                historialMensajes = mutableListOf(
                    MensajeTicket("Camila Rojas", "No puedo completar mi pago de arancel.", false, fechaInicial),
                    MensajeTicket("Agente PagoFlex", "Verificando con la pasarela de pagos.", true, fechaInicial)
                )
            ),
            TicketSoporteDto(
                id = "TK-102",
                usuario = "María González",
                asunto = "Comprobante no emitido",
                mensaje = "Necesito el comprobante de agua.",
                respuesta = "Comprobante reenviado por correo.",
                estado = "Resuelto",
                historialMensajes = mutableListOf(
                    MensajeTicket("María González", "Necesito el comprobante de agua.", false, fechaInicial),
                    MensajeTicket("Agente PagoFlex", "Comprobante reenviado por correo.", true, fechaInicial)
                )
            )
        )
    )
    val tickets: StateFlow<List<TicketSoporteDto>> = _tickets.asStateFlow()

    fun crearTicket(usuario: String, asunto: String, mensaje: String) {
        val nuevoId = "TK-" + (103..999).random()
        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date())
        val nuevoTicket = TicketSoporteDto(
            id = nuevoId,
            usuario = usuario,
            asunto = asunto,
            mensaje = mensaje,
            respuesta = "",
            estado = "Pendiente",
            historialMensajes = mutableListOf(
                MensajeTicket(usuario, mensaje, false, fechaActual)
            )
        )
        _tickets.value.add(0, nuevoTicket)
    }

    fun responderTicket(idTicket: String, respuesta: String, nuevoEstado: String = "En Proceso") {
        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date())
        val lista = _tickets.value.map { t ->
            if (t.id == idTicket) {
                t.historialMensajes.add(MensajeTicket("Agente PagoFlex", respuesta, true, fechaActual))
                t.copy(respuesta = respuesta, estado = nuevoEstado)
            } else {
                t
            }
        }
        _tickets.value = lista.toMutableList()
    }

    fun cambiarEstadoTicket(idTicket: String, nuevoEstado: String) {
        val lista = _tickets.value.map { t ->
            if (t.id == idTicket) {
                t.copy(estado = nuevoEstado)
            } else {
                t
            }
        }
        _tickets.value = lista.toMutableList()
    }

    fun agregarSeguimiento(idTicket: String, autor: String, mensaje: String, esAgente: Boolean) {
        val ticket = _tickets.value.find { it.id == idTicket }
        if (ticket != null && (ticket.estado == "Resuelto" || ticket.estado == "Cerrado")) {
            return
        }

        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date())
        val lista = _tickets.value.map { t ->
            if (t.id == idTicket) {
                t.historialMensajes.add(MensajeTicket(autor, mensaje, esAgente, fechaActual))
                if (!esAgente) {
                    t.copy(estado = "Pendiente")
                } else {
                    t.copy(respuesta = mensaje)
                }
            } else {
                t
            }
        }
        _tickets.value = lista.toMutableList()
    }
}