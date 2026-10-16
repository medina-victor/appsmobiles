package duoc.pagosflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import duoc.pagosflex.data.UsuarioDbHelper
import duoc.pagosflex.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SeleccionarPerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = UsuarioDbHelper(application)

    // Estado con la lista de usuarios cargados desde SQLite
    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    // Estado del usuario actualmente seleccionado
    private val _usuarioSeleccionado = MutableStateFlow<Usuario?>(null)
    val usuarioSeleccionado: StateFlow<Usuario?> = _usuarioSeleccionado.asStateFlow()

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        val lista = dbHelper.obtenerUsuarios()
        _usuarios.value = lista
        if (lista.isNotEmpty() && _usuarioSeleccionado.value == null) {
            _usuarioSeleccionado.value = lista.first()
        }
    }

    fun seleccionarUsuario(usuario: Usuario) {
        _usuarioSeleccionado.value = usuario
    }
}