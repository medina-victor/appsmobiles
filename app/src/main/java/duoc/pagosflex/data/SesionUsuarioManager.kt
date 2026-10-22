package duoc.pagosflex.data

import android.net.Uri
import duoc.pagosflex.viewmodel.TipoRolUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UsuarioSesion(
    val nombre: String = "",
    val rut: String = "",
    val correo: String = "",
    val rol: TipoRolUsuario = TipoRolUsuario.USUARIO_FINAL,
    val fotoUri: Uri? = null,
    val usuarioLogin: String = "",
    val esSesionActiva: Boolean = false
)

object SesionUsuarioManager {
    private val _usuarioActual = MutableStateFlow(UsuarioSesion())
    val usuarioActual: StateFlow<UsuarioSesion> = _usuarioActual.asStateFlow()

    fun iniciarSesion(
        nombre: String,
        rut: String = "",
        correo: String = "",
        rol: TipoRolUsuario = TipoRolUsuario.USUARIO_FINAL,
        fotoUri: Uri? = null,
        usuarioLogin: String = ""
    ) {
        _usuarioActual.value = UsuarioSesion(
            nombre = nombre,
            rut = rut,
            correo = correo,
            rol = rol,
            fotoUri = fotoUri,
            usuarioLogin = if (usuarioLogin.isBlank()) {
                if (correo.contains("@")) correo.substringBefore("@") else nombre
            } else usuarioLogin,
            esSesionActiva = true
        )
    }

    fun actualizarFoto(uri: Uri?) {
        _usuarioActual.value = _usuarioActual.value.copy(fotoUri = uri)
    }

    fun actualizarUsuarioLogin(nuevoUsuario: String) {
        _usuarioActual.value = _usuarioActual.value.copy(usuarioLogin = nuevoUsuario)
    }

    fun cerrarSesion() {
        _usuarioActual.value = UsuarioSesion(esSesionActiva = false)
    }
}