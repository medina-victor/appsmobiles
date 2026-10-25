package duoc.pagosflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.data.UsuarioDbHelper
import duoc.pagosflex.model.Rol
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

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = UsuarioDbHelper(application)

    private val _estadoUi = MutableStateFlow<EstadoLogin>(EstadoLogin.Idle)
    val estadoUi: StateFlow<EstadoLogin> = _estadoUi.asStateFlow()

    var usuarioOCorreo = MutableStateFlow("")
    var contrasena = MutableStateFlow("")
    var errorCredenciales = MutableStateFlow<String?>(null)

    val formularioValido: StateFlow<Boolean> = combine(
        usuarioOCorreo, contrasena, _estadoUi
    ) { u, p, estado ->
        u.isNotBlank() && p.isNotBlank() && estado !is EstadoLogin.Cargando
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun alCambiarUsuario(nuevoUsuario: String) {
        usuarioOCorreo.value = nuevoUsuario.trim()
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
            errorCredenciales.value = "Ingresa tu usuario, correo o RUT y contraseña"
            return
        }

        viewModelScope.launch {
            _estadoUi.value = EstadoLogin.Cargando
            delay(800)

            val usuarioEncontrado = dbHelper.verificarCredenciales(u, p)
            if (usuarioEncontrado != null) {
                val tipoRol = when (usuarioEncontrado.rol) {
                    Rol.CLIENTE -> TipoRolUsuario.USUARIO_FINAL
                    Rol.AGENTE -> TipoRolUsuario.AGENTE_PAGOFLEX
                    Rol.EJECUTIVO -> TipoRolUsuario.EJECUTIVO_EMPRESA
                }

                val pinUsuario = dbHelper.obtenerPinUsuario(u)

                SesionUsuarioManager.iniciarSesion(
                    context = getApplication(),
                    nombre = usuarioEncontrado.nombre,
                    rut = usuarioEncontrado.rut,
                    correo = usuarioEncontrado.correo,
                    rol = tipoRol,
                    usuarioLogin = u,
                    pin = pinUsuario
                )

                _estadoUi.value = EstadoLogin.Exito
                alExito()
            } else {
                _estadoUi.value = EstadoLogin.Error("Credenciales incorrectas")
                errorCredenciales.value = "Credenciales incorrectas. Verifique su usuario/RUT y contraseña."
            }
        }
    }
}