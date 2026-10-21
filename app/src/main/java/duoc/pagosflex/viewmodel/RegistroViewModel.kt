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

class RegistroViewModel : ViewModel() {

    private val _estadoUi = MutableStateFlow<EstadoRegistro>(EstadoRegistro.Inactivo)
    val estadoUi: StateFlow<EstadoRegistro> = _estadoUi.asStateFlow()

    var nombre = MutableStateFlow("")
    var rut = MutableStateFlow("")
    var correo = MutableStateFlow("")
    var contrasena = MutableStateFlow("")
    var confirmarContrasena = MutableStateFlow("")
    var rolSeleccionado = MutableStateFlow(TipoRolUsuario.CLIENTE)

    var errorNombre = MutableStateFlow<String?>(null)
    var errorRut = MutableStateFlow<String?>(null)
    var errorCorreo = MutableStateFlow<String?>(null)

    var tieneMinimoCaracteres = MutableStateFlow(false)
    var tieneMayuscula = MutableStateFlow(false)
    var tieneMinuscula = MutableStateFlow(false)
    var tieneNumero = MutableStateFlow(false)
    var tieneCaracterEspecial = MutableStateFlow(false)
    var contrasenasCoinciden = MutableStateFlow(false)
    var terminosAceptados = MutableStateFlow(false)

    val formularioValido: StateFlow<Boolean> = combine(
        nombre, rut, correo, contrasena, confirmarContrasena,
        terminosAceptados, tieneMinimoCaracteres, tieneMayuscula,
        tieneMinuscula, tieneNumero, tieneCaracterEspecial, contrasenasCoinciden
    ) { params ->
        val nom = params[0] as String
        val r = params[1] as String
        val cor = params[2] as String
        val term = params[5] as Boolean
        val minChar = params[6] as Boolean
        val mayus = params[7] as Boolean
        val minus = params[8] as Boolean
        val num = params[9] as Boolean
        val esp = params[10] as Boolean
        val coinc = params[11] as Boolean

        nom.isNotBlank() && r.length >= 8 && cor.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(cor).matches() &&
                term && minChar && mayus && minus && num && esp && coinc
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun alCambiarRol(nuevoRol: TipoRolUsuario) {
        rolSeleccionado.value = nuevoRol
        rut.value = ""
        errorRut.value = null
    }

    fun alCambiarNombre(nuevoNombre: String) {
        nombre.value = nuevoNombre
        when {
            nuevoNombre.isBlank() -> errorNombre.value = "Campo obligatorio"
            else -> errorNombre.value = null
        }
    }

    fun alCambiarRut(nuevoRut: String) {
        rut.value = nuevoRut
        when {
            nuevoRut.isBlank() -> errorRut.value = "El RUT es obligatorio"
            nuevoRut.length < 8 -> errorRut.value = "Ingrese un RUT válido"
            else -> errorRut.value = null
        }
    }

    fun alCambiarCorreo(nuevoCorreo: String) {
        correo.value = nuevoCorreo
        val esValido = android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoCorreo).matches()
        when {
            nuevoCorreo.isBlank() -> errorCorreo.value = "El correo es obligatorio"
            !esValido -> errorCorreo.value = "Ingrese un correo válido"
            else -> errorCorreo.value = null
        }
    }

    fun alCambiarContrasena(nuevaContrasena: String) {
        contrasena.value = nuevaContrasena
        tieneMinimoCaracteres.value = nuevaContrasena.length >= 8
        tieneMayuscula.value = nuevaContrasena.any { it.isUpperCase() }
        tieneMinuscula.value = nuevaContrasena.any { it.isLowerCase() }
        tieneNumero.value = nuevaContrasena.any { it.isDigit() }
        tieneCaracterEspecial.value = nuevaContrasena.any { !it.isLetterOrDigit() }
        contrasenasCoinciden.value = nuevaContrasena.isNotEmpty() && nuevaContrasena == confirmarContrasena.value
    }

    fun alCambiarConfirmarContrasena(nuevaConfirmacion: String) {
        confirmarContrasena.value = nuevaConfirmacion
        contrasenasCoinciden.value = nuevaConfirmacion.isNotEmpty() && nuevaConfirmacion == contrasena.value
    }

    fun alCambiarTerminos(aceptado: Boolean) {
        terminosAceptados.value = aceptado
    }

    fun registrarUsuario(alExito: () -> Unit) {
        if (!formularioValido.value) return

        viewModelScope.launch {
            _estadoUi.value = EstadoRegistro.Cargando
            delay(1200)

            SesionUsuarioManager.iniciarSesion(
                nombre = nombre.value,
                correo = correo.value,
                rol = rolSeleccionado.value
            )

            _estadoUi.value = EstadoRegistro.Exito
            alExito()
        }
    }
}