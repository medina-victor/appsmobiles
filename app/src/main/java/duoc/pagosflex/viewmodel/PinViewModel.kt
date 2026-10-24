package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PinViewModel : ViewModel() {

    private val _pinIngresado = MutableStateFlow("")
    val pinIngresado: StateFlow<String> = _pinIngresado.asStateFlow()

    private val _errorPin = MutableStateFlow(false)
    val errorPin: StateFlow<Boolean> = _errorPin.asStateFlow()

    fun ingresarDigito(digito: String, pinCorrecto: String, alExito: () -> Unit) {
        _errorPin.value = false
        val actual = _pinIngresado.value
        if (actual.length < 4) {
            val nuevo = actual + digito
            _pinIngresado.value = nuevo
            if (nuevo.length == 4) {
                if (nuevo == pinCorrecto) {
                    alExito()
                } else {
                    _errorPin.value = true
                    _pinIngresado.value = ""
                }
            }
        }
    }

    fun limpiar() {
        _pinIngresado.value = ""
        _errorPin.value = false
    }

    fun borrarUltimo() {
        if (_pinIngresado.value.isNotEmpty()) {
            _pinIngresado.value = _pinIngresado.value.dropLast(1)
            _errorPin.value = false
        }
    }
}