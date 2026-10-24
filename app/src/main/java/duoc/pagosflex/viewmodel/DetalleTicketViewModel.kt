package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.TicketRepositorio
import duoc.pagosflex.data.TicketSoporteDto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DetalleTicketViewModel : ViewModel() {

    val tickets: StateFlow<List<TicketSoporteDto>> = TicketRepositorio.tickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarSeguimiento(idTicket: String, autor: String, mensaje: String, esAgente: Boolean) {
        TicketRepositorio.agregarSeguimiento(idTicket, autor, mensaje, esAgente)
    }
}