package duoc.pagosflex.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.data.UsuarioSesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    val usuarioSesion: StateFlow<UsuarioSesion> = SesionUsuarioManager.usuarioActual
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UsuarioSesion()
        )

    var aliasUsuario = MutableStateFlow("")

    fun actualizarFotoPerfil(uri: Uri?) {
        SesionUsuarioManager.actualizarFoto(getApplication(), uri)
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