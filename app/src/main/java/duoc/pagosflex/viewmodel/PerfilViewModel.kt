package duoc.pagosflex.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.data.UsuarioSesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PerfilViewModel : ViewModel() {

    val usuarioSesion: StateFlow<UsuarioSesion> = SesionUsuarioManager.usuarioActual
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UsuarioSesion()
        )

    var aliasUsuario = MutableStateFlow("")

    fun actualizarFotoPerfil(uri: Uri?) {
        SesionUsuarioManager.actualizarFoto(uri)
    }

    fun guardarAliasUsuario(nuevoAlias: String) {
        aliasUsuario.value = nuevoAlias
        SesionUsuarioManager.actualizarUsuarioLogin(nuevoAlias)
    }

    fun cerrarSesion(alCerrar: () -> Unit) {
        SesionUsuarioManager.cerrarSesion()
        alCerrar()
    }
}