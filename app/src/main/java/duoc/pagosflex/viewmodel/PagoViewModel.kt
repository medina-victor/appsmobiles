package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface EstadoPago {
    object Inactivo : EstadoPago
    object Procesando : EstadoPago
    data class Exito(val idTransaccion: String, val monto: Double, val concepto: String) : EstadoPago
    data class Error(val mensaje: String) : EstadoPago
}

class PagoViewModel : ViewModel() {

    private val _estadoUi = MutableStateFlow<EstadoPago>(EstadoPago.Inactivo)
    val estadoUi: StateFlow<EstadoPago> = _estadoUi.asStateFlow()

    private val _estaProcesando = MutableStateFlow(false)
    val estaProcesando: StateFlow<Boolean> = _estaProcesando.asStateFlow()

    var numeroTarjeta = MutableStateFlow("")
    var cvv = MutableStateFlow("")
    var expiracion = MutableStateFlow("")

    fun alCambiarNumeroTarjeta(nuevoNumero: String) {
        if (nuevoNumero.length <= 16) {
            numeroTarjeta.value = nuevoNumero
        }
    }

    fun alCambiarCvv(nuevoCvv: String) {
        if (nuevoCvv.length <= 4) {
            cvv.value = nuevoCvv
        }
    }

    fun alCambiarExpiracion(nuevaExpiracion: String) {
        if (nuevaExpiracion.length <= 5) {
            expiracion.value = nuevaExpiracion
        }
    }

    fun procesarPago(idCompromiso: String, monto: Double, concepto: String) {
        if (_estaProcesando.value) return

        if (monto <= 0) {
            _estadoUi.value = EstadoPago.Error("El monto a pagar debe ser mayor a $0.")
            return
        }

        viewModelScope.launch {
            _estaProcesando.value = true
            _estadoUi.value = EstadoPago.Procesando

            try {
                delay(2000)
                val idTransaccionSimulada = "TX-" + UUID.randomUUID().toString().take(8).uppercase()
                _estadoUi.value = EstadoPago.Exito(
                    idTransaccion = idTransaccionSimulada,
                    monto = monto,
                    concepto = concepto
                )
            } catch (e: Exception) {
                _estadoUi.value =
                    EstadoPago.Error("Error al procesar la transacción. Intente nuevamente.")
            } finally {
                _estaProcesando.value = false
            }
        }
    }

    fun reiniciarEstado() {
        _estadoUi.value = EstadoPago.Inactivo
        numeroTarjeta.value = ""
        cvv.value = ""
        expiracion.value = ""
    }
}