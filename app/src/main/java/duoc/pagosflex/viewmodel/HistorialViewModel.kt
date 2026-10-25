package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.CompromisoRepositorio
import duoc.pagosflex.data.ItemHistorial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HistorialViewModel : ViewModel() {

    private val historialBase: StateFlow<List<ItemHistorial>> = CompromisoRepositorio.historial
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var textoBusqueda = MutableStateFlow("")

    val historialFiltrado: StateFlow<List<ItemHistorial>> = combine(
        historialBase, textoBusqueda
    ) { lista, busqueda ->
        if (busqueda.isBlank()) {
            lista
        } else {
            lista.filter {
                it.institucion.contains(busqueda, ignoreCase = true) ||
                        it.concepto.contains(busqueda, ignoreCase = true) ||
                        it.idTransaccion.contains(busqueda, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun alBuscar(texto: String) {
        textoBusqueda.value = texto.trim()
    }
}