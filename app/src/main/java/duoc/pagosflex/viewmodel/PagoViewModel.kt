package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.Compromiso
import duoc.pagosflex.data.CompromisoRepositorio
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.Calendar

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

    var metodoPago = MutableStateFlow("Saldo PagoFlex")
    var numeroTarjeta = MutableStateFlow("")
    var cvv = MutableStateFlow("")
    var expiracion = MutableStateFlow("")

    val saldoCuenta: StateFlow<Double> = CompromisoRepositorio.saldoCuenta

    fun obtenerCompromiso(id: String): Compromiso? {
        return CompromisoRepositorio.obtenerCompromiso(id)
    }

    fun alCambiarMetodoPago(nuevoMetodo: String) {
        metodoPago.value = nuevoMetodo
    }

    fun alCambiarNumeroTarjeta(nuevoNumero: String) {
        if (nuevoNumero.length <= 16 && nuevoNumero.all { it.isDigit() }) {
            numeroTarjeta.value = nuevoNumero
        }
    }

    fun alCambiarCvv(nuevoCvv: String) {
        if (nuevoCvv.length <= 4 && nuevoCvv.all { it.isDigit() }) {
            cvv.value = nuevoCvv
        }
    }

    fun alCambiarExpiracion(nuevaExpiracion: String) {
        if (nuevaExpiracion.length <= 5) {
            expiracion.value = nuevaExpiracion
        }
    }

    private fun validarExpiracion(exp: String): Boolean {
        if (exp.length != 5 || !exp.contains("/")) return false
        val partes = exp.split("/")
        val mes = partes[0].toIntOrNull() ?: return false
        val anio = partes[1].toIntOrNull() ?: return false
        val anioCompleto = 2000 + anio

        val calActual = Calendar.getInstance()
        val mesActual = calActual.get(Calendar.MONTH) + 1
        val anioActual = calActual.get(Calendar.YEAR)

        if (mes < 1 || mes > 12) return false
        if (anioCompleto < anioActual) return false
        if (anioCompleto == anioActual && mes < mesActual) return false
        return true
    }

    fun procesarPago(idCompromiso: String, monto: Double, concepto: String) {
        if (_estaProcesando.value) return

        if (monto <= 0) {
            _estadoUi.value = EstadoPago.Error("El monto a pagar debe ser mayor a $0.")
            return
        }

        if (metodoPago.value == "Tarjeta") {
            if (numeroTarjeta.value.length < 15 || cvv.value.length < 3) {
                _estadoUi.value = EstadoPago.Error("Ingrese datos de tarjeta válidos.")
                return
            }
            if (!validarExpiracion(expiracion.value)) {
                _estadoUi.value = EstadoPago.Error("La tarjeta está vencida o el formato (MM/AA) es inválido.")
                return
            }
        }

        if (metodoPago.value == "Saldo PagoFlex" && saldoCuenta.value < monto) {
            _estadoUi.value = EstadoPago.Error("Saldo insuficiente en su cuenta PagoFlex.")
            return
        }

        viewModelScope.launch {
            _estaProcesando.value = true
            _estadoUi.value = EstadoPago.Procesando

            try {
                delay(1200)
                val idTransaccionSimulada = "TX-" + UUID.randomUUID().toString().take(8).uppercase()
                
                val registrado = CompromisoRepositorio.registrarPagoExitoso(idCompromiso, idTransaccionSimulada, monto)
                if (registrado) {
                    _estadoUi.value = EstadoPago.Exito(
                        idTransaccion = idTransaccionSimulada,
                        monto = monto,
                        concepto = concepto
                    )
                } else {
                    _estadoUi.value = EstadoPago.Error("No se pudo procesar el pago o saldo insuficiente.")
                }
            } catch (_: Exception) {
                _estadoUi.value = EstadoPago.Error("Error al procesar la transacción. Intente nuevamente.")
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