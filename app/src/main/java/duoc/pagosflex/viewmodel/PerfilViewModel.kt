package duoc.pagosflex.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.data.UsuarioDbHelper
import duoc.pagosflex.data.UsuarioSesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    val usuarioSesion: StateFlow<UsuarioSesion> = SesionUsuarioManager.usuarioActual
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = UsuarioSesion()
        )

    var aliasUsuario = MutableStateFlow("")
    var mensajeAlias = MutableStateFlow<String?>(null)
    var nuevoCorreo = MutableStateFlow("")
    var confirmarCorreo = MutableStateFlow("")
    var correosCoinciden = MutableStateFlow(false)

    fun actualizarFotoPerfil(uri: Uri?) {
        SesionUsuarioManager.actualizarFoto(getApplication(), uri)
    }

    fun guardarAliasUsuario(nuevoAlias: String) {
        val identificador = SesionUsuarioManager.usuarioActual.value.correo.ifBlank { SesionUsuarioManager.usuarioActual.value.rut }
        val dbHelper = UsuarioDbHelper(getApplication())
        val exito = dbHelper.actualizarAliasUsuario(identificador, nuevoAlias)
        if (exito) {
            SesionUsuarioManager.actualizarUsuarioLogin(nuevoAlias)
            mensajeAlias.value = "Alias actualizado correctamente"
        } else {
            mensajeAlias.value = "El alias ya está en uso por otro usuario"
        }
    }

    fun alCambiarNuevoCorreo(correo: String) {
        nuevoCorreo.value = correo.trim()
        correosCoinciden.value = nuevoCorreo.value.isNotBlank() && nuevoCorreo.value == confirmarCorreo.value
    }

    fun alCambiarConfirmarCorreo(correo: String) {
        confirmarCorreo.value = correo.trim()
        correosCoinciden.value = confirmarCorreo.value.isNotBlank() && confirmarCorreo.value == nuevoCorreo.value
    }

    fun actualizarCorreo(alExito: () -> Unit) {
        val correo = nuevoCorreo.value
        val esValido = android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
        if (!esValido || !correosCoinciden.value) return

        SesionUsuarioManager.actualizarCorreo(getApplication(), correo)
        nuevoCorreo.value = ""
        confirmarCorreo.value = ""
        alExito()
    }

    fun cerrarSesion(alCerrar: () -> Unit) {
        SesionUsuarioManager.cerrarSesion()
        alCerrar()
    }
}