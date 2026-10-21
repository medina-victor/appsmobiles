package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminViewModel : ViewModel() {

    private val _usuariosActivos = MutableStateFlow(1240)
    val usuariosActivos: StateFlow<Int> = _usuariosActivos.asStateFlow()

    private val _transaccionesDiarias = MutableStateFlow(342)
    val transaccionesDiarias: StateFlow<Int> = _transaccionesDiarias.asStateFlow()

    private val _estadoAuditoria = MutableStateFlow("Logs encriptados en cumplimiento Ley 19.628")
    val estadoAuditoria: StateFlow<String> = _estadoAuditoria.asStateFlow()
}

