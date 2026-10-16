package duoc.pagosflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import duoc.pagosflex.data.UsuarioDbHelper
import duoc.pagosflex.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RegistroUiState(
    val rolSeleccionado: Rol = Rol.CLIENTE,
    val nombre: String = "",
    val rut: String = "",
    val email: String = "",
    val telefono: String = "",
    val areaOEmpresa: String = "",
    val codigoAcceso: String = "",
    val errorMensaje: String? = null,
    val registroExitoso: Boolean = false
)

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = UsuarioDbHelper(application)

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onRolCambiado(nuevoRol: Rol) {
        _uiState.value = _uiState.value.copy(
            rolSeleccionado = nuevoRol,
            errorMensaje = null
        )
    }

    fun onNombreCambiado(valor: String) { _uiState.value = _uiState.value.copy(nombre = valor) }
    fun onRutCambiado(valor: String) { _uiState.value = _uiState.value.copy(rut = valor) }
    fun onEmailCambiado(valor: String) { _uiState.value = _uiState.value.copy(email = valor) }
    fun onTelefonoCambiado(valor: String) { _uiState.value = _uiState.value.copy(telefono = valor) }
    fun onAreaOEmpresaCambiado(valor: String) { _uiState.value = _uiState.value.copy(areaOEmpresa = valor) }
    fun onCodigoAccesoCambiado(valor: String) { _uiState.value = _uiState.value.copy(codigoAcceso = valor) }

    fun registrarCuenta(onExito: () -> Unit) {
        val state = _uiState.value

        // Validaciones básicas
        if (state.nombre.isBlank() || state.rut.isBlank() || state.email.isBlank()) {
            _uiState.value = state.copy(errorMensaje = "Por favor completa los campos obligatorios.")
            return
        }

        if (state.rolSeleccionado == Rol.EJECUTIVO && state.areaOEmpresa.isBlank()) {
            _uiState.value = state.copy(errorMensaje = "Indica la Empresa o Razón Social.")
            return
        }

        if (state.rolSeleccionado == Rol.AGENTE && state.codigoAcceso.isBlank()) {
            _uiState.value = state.copy(errorMensaje = "Ingresa tu Código de Agente PagoFlex.")
            return
        }

        // Obtener iniciales para el Avatar
        val iniciales = state.nombre
            .split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")

        // Guardar en la SQLite
        val insertado = dbHelper.registrarUsuario(
            nombre = state.nombre,
            rut = state.rut,
            rol = state.rolSeleccionado,
            codigo = if (state.rolSeleccionado == Rol.AGENTE) state.codigoAcceso else state.rut.take(4),
            detalle = if (state.rolSeleccionado == Rol.CLIENTE) "Cliente Final" else state.areaOEmpresa,
            avatar = if (iniciales.isNotEmpty()) iniciales else "PF"
        )

        if (insertado) {
            _uiState.value = state.copy(registroExitoso = true, errorMensaje = null)
            onExito()
        } else {
            _uiState.value = state.copy(errorMensaje = "Error al guardar el usuario en la base de datos.")
        }
    }
}