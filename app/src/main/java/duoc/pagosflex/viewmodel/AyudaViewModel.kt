package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.TicketRepositorio
import duoc.pagosflex.data.TicketSoporteDto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AyudaViewModel : ViewModel() {

    val tickets: StateFlow<List<TicketSoporteDto>> = TicketRepositorio.tickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun crearTicket(usuario: String, asunto: String, mensaje: String) {
        TicketRepositorio.crearTicket(usuario, asunto, mensaje)
    }
}