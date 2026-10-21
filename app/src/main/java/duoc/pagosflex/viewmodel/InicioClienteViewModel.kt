package duoc.pagosflex.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EstadoInicioSesion {
    object Inactivo : EstadoInicioSesion
    object Cargando : EstadoInicioSesion
    object Exito : EstadoInicioSesion
    data class Error(val mensaje: String) : EstadoInicioSesion
}

class InicioClienteViewModel : ViewModel() {

    private val _estadoUi = MutableStateFlow<EstadoInicioSesion>(EstadoInicioSesion.Inactivo)
    val estadoUi: StateFlow<EstadoInicioSesion> = _estadoUi.asStateFlow()

    var usuario = MutableStateFlow("")
    var contrasena = MutableStateFlow("")

    var errorUsuario = MutableStateFlow<String?>(null)
    var errorContrasena = MutableStateFlow<String?>(null)

    private val usuarioPrueba = "estudiante"
    private val contrasenaPrueba = "Duoc2026*"

    fun alCambiarUsuario(nuevoUsuario: String) {
        usuario.value = nuevoUsuario
        errorUsuario.value = if (nuevoUsuario.isBlank()) "El usuario no puede estar vacío" else null
    }

    fun alCambiarContrasena(nuevaContrasena: String) {
        contrasena.value = nuevaContrasena
        errorContrasena.value = if (nuevaContrasena.isBlank()) "La contraseña no puede estar vacía" else null
    }

    fun iniciarSesion(alExito: () -> Unit) {
        val usr = usuario.value.trim()
        val pwd = contrasena.value.trim()

        if (usr.isBlank()) {
            errorUsuario.value = "Ingrese su usuario"
            return
        }
        if (pwd.isBlank()) {
            errorContrasena.value = "Ingrese su contraseña"
            return
        }

        viewModelScope.launch {
            _estadoUi.value = EstadoInicioSesion.Cargando
            delay(800)

            if (usr.equals(usuarioPrueba, ignoreCase = true) && pwd == contrasenaPrueba) {
                _estadoUi.value = EstadoInicioSesion.Exito
                alExito()
            } else {
                _estadoUi.value = EstadoInicioSesion.Error("Credenciales incorrectas")
            }
        }
    }
}