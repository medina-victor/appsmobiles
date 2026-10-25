package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TerminosViewModel : ViewModel() {
    private val _aceptado = MutableStateFlow(true)
    val aceptado: StateFlow<Boolean> = _aceptado.asStateFlow()
}