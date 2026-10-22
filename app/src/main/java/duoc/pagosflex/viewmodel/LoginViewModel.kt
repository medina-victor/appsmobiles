package duoc.pagosflex.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.SesionUsuarioManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class EstadoLogin {
    object Idle : EstadoLogin()
    object Cargando : EstadoLogin()
    object Exito : EstadoLogin()
    data class Error(val mensaje: String) : EstadoLogin()
}

class LoginViewModel : ViewModel() {

    private val _estadoUi = MutableStateFlow<EstadoLogin>(EstadoLogin.Idle)
    val estadoUi: StateFlow<EstadoLogin> = _estadoUi.asStateFlow()

    var usuarioOCorreo = MutableStateFlow("")
    var contrasena = MutableStateFlow("")
    var errorCredenciales = MutableStateFlow<String?>(null)

    val formularioValido: StateFlow<Boolean> = combine(
        usuarioOCorreo, contrasena, _estadoUi
    ) { u, p, estado ->
        u.isNotBlank() && p.isNotBlank() && p.length >= 6 && estado !is EstadoLogin.Cargando
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun alCambiarUsuario(nuevoUsuario: String) {
        usuarioOCorreo.value = nuevoUsuario
        errorCredenciales.value = null
    }

    fun alCambiarContrasena(nuevaContrasena: String) {
        contrasena.value = nuevaContrasena
        errorCredenciales.value = null
    }

    fun iniciarSesion(alExito: () -> Unit) {
        val u = usuarioOCorreo.value.trim()
        val p = contrasena.value.trim()

        if (u.isBlank() || p.isBlank()) {
            errorCredenciales.value = "Ingresa tu usuario/correo/RUT y contraseña"
            return
        }

        viewModelScope.launch {
            _estadoUi.value = EstadoLogin.Cargando
            delay(1000)

            SesionUsuarioManager.iniciarSesion(
                nombre = if (u.contains("@")) u.substringBefore("@") else u,
                correo = if (u.contains("@")) u else "$u@pagoflex.cl",
                rut = if (!u.contains("@")) u else "12.345.678-9",
                rol = TipoRolUsuario.USUARIO_FINAL,
                usuarioLogin = u
            )

            _estadoUi.value = EstadoLogin.Exito
            alExito()
        }
    }
}