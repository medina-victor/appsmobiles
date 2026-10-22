package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.CompromisoRepositorio
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.data.UsuarioSesion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CompromisoResumen(
    val id: String,
    val institucion: String,
    val concepto: String,
    val monto: Double,
    val fechaVencimiento: String,
    val cuotaActual: Int,
    val totalCuotas: Int,
    val estado: String
)

class InicioViewModel : ViewModel() {

    val usuarioSesion: StateFlow<UsuarioSesion> = SesionUsuarioManager.usuarioActual
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UsuarioSesion()
        )

    val compromisosPendientes: StateFlow<List<CompromisoResumen>> = CompromisoRepositorio.compromisos
        .map { lista ->
            lista.map { c ->
                CompromisoResumen(
                    id = c.id,
                    institucion = c.institucion,
                    concepto = c.concepto,
                    monto = c.monto,
                    fechaVencimiento = c.fechaVencimiento,
                    cuotaActual = c.cuotaActual,
                    totalCuotas = c.totalCuotas,
                    estado = c.estado
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalPendiente: StateFlow<Double> = CompromisoRepositorio.compromisos
        .map { lista -> lista.sumOf { it.monto } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val saldoCuenta: StateFlow<Double> = CompromisoRepositorio.saldoCuenta
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 550000.0
        )

    fun pagarCuotaCompromiso(idCompromiso: String) {
    }
}