package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.Compromiso
import duoc.pagosflex.data.CompromisoRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CasosAgenteViewModel : ViewModel() {

    val compromisos: StateFlow<List<Compromiso>> = CompromisoRepositorio.compromisos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}