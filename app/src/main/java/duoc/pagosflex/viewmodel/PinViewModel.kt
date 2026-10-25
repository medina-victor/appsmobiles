package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PinViewModel : ViewModel() {

    private val _pinIngresado = MutableStateFlow("")
    val pinIngresado: StateFlow<String> = _pinIngresado.asStateFlow()

    private val _errorPin = MutableStateFlow(false)
    val errorPin: StateFlow<Boolean> = _errorPin.asStateFlow()

    private val _intentosFallidos = MutableStateFlow(0)
    val intentosFallidos: StateFlow<Int> = _intentosFallidos.asStateFlow()

    private val _estaSuspendido = MutableStateFlow(false)
    val estaSuspendido: StateFlow<Boolean> = _estaSuspendido.asStateFlow()

    private val _segundosRestantes = MutableStateFlow(0)
    val segundosRestantes: StateFlow<Int> = _segundosRestantes.asStateFlow()

    fun ingresarDigito(digito: String, pinCorrecto: String, alExito: () -> Unit) {
        if (_estaSuspendido.value) return

        _errorPin.value = false
        val actual = _pinIngresado.value
        if (actual.length < 4) {
            val nuevo = actual + digito
            _pinIngresado.value = nuevo
            if (nuevo.length == 4) {
                if (nuevo == pinCorrecto) {
                    _intentosFallidos.value = 0
                    alExito()
                } else {
                    _errorPin.value = true
                    _pinIngresado.value = ""
                    _intentosFallidos.value += 1
                    if (_intentosFallidos.value >= 4) {
                        iniciarSuspension()
                    }
                }
            }
        }
    }

    private fun iniciarSuspension() {
        _estaSuspendido.value = true
        _segundosRestantes.value = 60

        viewModelScope.launch {
            while (_segundosRestantes.value > 0) {
                delay(1000)
                _segundosRestantes.value -= 1
            }
            _estaSuspendido.value = false
            _intentosFallidos.value = 0
        }
    }

    fun limpiar() {
        if (_estaSuspendido.value) return
        _pinIngresado.value = ""
        _errorPin.value = false
    }

    fun borrarUltimo() {
        if (_estaSuspendido.value) return
        if (_pinIngresado.value.isNotEmpty()) {
            _pinIngresado.value = _pinIngresado.value.dropLast(1)
            _errorPin.value = false
        }
    }
}