package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.data.UsuarioSesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    private val _compromisosPendientes = MutableStateFlow<List<CompromisoResumen>>(emptyList())
    val compromisosPendientes: StateFlow<List<CompromisoResumen>> = _compromisosPendientes.asStateFlow()

    private val _totalPendiente = MutableStateFlow(0.0)
    val totalPendiente: StateFlow<Double> = _totalPendiente.asStateFlow()

    init {
        cargarDatosInicio()
    }

    private fun cargarDatosInicio() {
        viewModelScope.launch {
            val listaInicial = listOf(
                CompromisoResumen(
                    id = "C-101",
                    institucion = "Universidad Duoc UC",
                    concepto = "Arancel Educación",
                    monto = 145000.0,
                    fechaVencimiento = "05/11/2026",
                    cuotaActual = 5,
                    totalCuotas = 10,
                    estado = "Pendiente"
                ),
                CompromisoResumen(
                    id = "C-102",
                    institucion = "Servicios de Agua",
                    concepto = "Consumo Hogar",
                    monto = 24500.0,
                    fechaVencimiento = "12/11/2026",
                    cuotaActual = 1,
                    totalCuotas = 1,
                    estado = "Pendiente"
                )
            )
            _compromisosPendientes.value = listaInicial
            calcularTotal()
        }
    }

    fun pagarCuotaCompromiso(idCompromiso: String) {
        val listaActualizada = _compromisosPendientes.value.map { compromiso ->
            if (compromiso.id == idCompromiso) {
                val nuevaCuota = compromiso.cuotaActual + 1
                if (nuevaCuota > compromiso.totalCuotas) {
                    compromiso.copy(estado = "Pagado", cuotaActual = compromiso.totalCuotas)
                } else {
                    compromiso.copy(cuotaActual = nuevaCuota)
                }
            } else {
                compromiso
            }
        }.filter { it.estado != "Pagado" }

        _compromisosPendientes.value = listaActualizada
        calcularTotal()
    }

    private fun calcularTotal() {
        _totalPendiente.value = _compromisosPendientes.value.sumOf { it.monto }
    }
}