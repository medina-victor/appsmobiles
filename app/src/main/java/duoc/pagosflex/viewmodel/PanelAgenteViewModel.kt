package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.Compromiso
import duoc.pagosflex.data.CompromisoRepositorio
import duoc.pagosflex.data.TicketRepositorio
import duoc.pagosflex.data.TicketSoporteDto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PanelAgenteViewModel : ViewModel() {

    val compromisos: StateFlow<List<Compromiso>> = CompromisoRepositorio.compromisos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tickets: StateFlow<List<TicketSoporteDto>> = TicketRepositorio.tickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun responderTicket(idTicket: String, respuesta: String, nuevoEstado: String) {
        TicketRepositorio.responderTicket(idTicket, respuesta, nuevoEstado)
    }
}