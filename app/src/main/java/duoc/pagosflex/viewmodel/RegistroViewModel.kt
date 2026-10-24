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

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = UsuarioDbHelper(application)

    private val _estadoUi = MutableStateFlow<EstadoRegistro>(EstadoRegistro.Idle)
    val estadoUi: StateFlow<EstadoRegistro> = _estadoUi.asStateFlow()

    var nombre = MutableStateFlow("")
    var rut = MutableStateFlow("")
    var correo = MutableStateFlow("")
    var contrasena = MutableStateFlow("")
    var confirmarContrasena = MutableStateFlow("")
    var pinSeguridad = MutableStateFlow("")
    var confirmarPin = MutableStateFlow("")
    var rolSeleccionado = MutableStateFlow(TipoRolUsuario.USUARIO_FINAL)

    var errorNombre = MutableStateFlow<String?>(null)
    var errorRut = MutableStateFlow<String?>(null)
    var errorCorreo = MutableStateFlow<String?>(null)

    var tieneMinimoCaracteres = MutableStateFlow(false)
    var tieneMayuscula = MutableStateFlow(false)
    var tieneMinuscula = MutableStateFlow(false)
    var tieneNumero = MutableStateFlow(false)
    var tieneCaracterEspecial = MutableStateFlow(false)
    var contrasenasCoinciden = MutableStateFlow(false)
    var pinesCoinciden = MutableStateFlow(false)
    var terminosAceptados = MutableStateFlow(false)

    val formularioValido: StateFlow<Boolean> = combine(
        nombre, rut, correo, contrasena, confirmarContrasena, pinSeguridad, confirmarPin,
        terminosAceptados, tieneMinimoCaracteres, tieneMayuscula,
        tieneMinuscula, tieneNumero, tieneCaracterEspecial, contrasenasCoinciden, pinesCoinciden,
        errorNombre, errorRut, errorCorreo
    ) { params ->
        val nom = params[0] as String
        val r = params[1] as String
        val cor = params[2] as String
        val pin = params[5] as String
        val cPin = params[6] as String
        val term = params[7] as Boolean
        val minChar = params[8] as Boolean
        val mayus = params[9] as Boolean
        val minus = params[10] as Boolean
        val num = params[11] as Boolean
        val esp = params[12] as Boolean
        val coinc = params[13] as Boolean
        val pCoinc = params[14] as Boolean
        val errNom = params[15] as String?
        val errRut = params[16] as String?
        val errCor = params[17] as String?

        nom.isNotBlank() && errNom == null &&
                r.isNotBlank() && errRut == null &&
                cor.isNotBlank() && errCor == null &&
                pin.length == 4 && cPin.length == 4 && pCoinc &&
                term && minChar && mayus && minus && num && esp && coinc
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun alCambiarRol(nuevoRol: TipoRolUsuario) {
        rolSeleccionado.value = nuevoRol
        rut.value = ""
        errorRut.value = null
        alCambiarNombre(nombre.value)
    }

    fun alCambiarNombre(nuevoNombre: String) {
        nombre.value = nuevoNombre
        val esEmpresa = rolSeleccionado.value == TipoRolUsuario.EJECUTIVO_EMPRESA

        when {
            nuevoNombre.isBlank() -> {
                errorNombre.value = "Este campo es obligatorio"
            }
            !esEmpresa && nuevoNombre.any { it.isDigit() } -> {
                errorNombre.value = "El nombre del usuario no puede contener números"
            }
            nuevoNombre.trim().length < 3 -> {
                errorNombre.value = "Debe tener al menos 3 caracteres"
            }
            else -> {
                errorNombre.value = null
            }
        }
    }

    fun alCambiarRut(nuevoRut: String) {
        val limpio = nuevoRut.replace(Regex("[^0-9kK]"), "").uppercase()
        rut.value = limpio
        val esEmpresa = rolSeleccionado.value == TipoRolUsuario.EJECUTIVO_EMPRESA
        val rutConFormato = formatearRut(limpio)

        when {
            limpio.isBlank() -> {
                errorRut.value = "El RUT es obligatorio"
            }
            !esEmpresa && !validarRutChileno(rutConFormato) -> {
                errorRut.value = "RUT o dígito verificador inválido"
            }
            else -> {
                errorRut.value = null
            }
        }
    }

    fun alCambiarCorreo(nuevoCorreo: String) {
        correo.value = nuevoCorreo
        val esValido = android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoCorreo).matches()
        val dominiosProhibidos = listOf("yopmail.com", "mailinator.com", "10minutemail.com", "trashmail.com")
        val dominio = nuevoCorreo.substringAfter("@", "").lowercase()

        when {
            nuevoCorreo.isBlank() -> {
                errorCorreo.value = "El correo es obligatorio"
            }
            !esValido -> {
                errorCorreo.value = "Ingrese un correo válido"
            }
            dominiosProhibidos.contains(dominio) -> {
                errorCorreo.value = "No se permiten correos temporales"
            }
            else -> {
                errorCorreo.value = null
            }
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

    fun alCambiarPin(nuevoPin: String) {
        if (nuevoPin.length <= 4 && nuevoPin.all { it.isDigit() }) {
            pinSeguridad.value = nuevoPin
            pinesCoinciden.value = nuevoPin.isNotEmpty() && nuevoPin == confirmarPin.value
        }
    }

    fun alCambiarConfirmarPin(nuevoPin: String) {
        if (nuevoPin.length <= 4 && nuevoPin.all { it.isDigit() }) {
            confirmarPin.value = nuevoPin
            pinesCoinciden.value = nuevoPin.isNotEmpty() && nuevoPin == pinSeguridad.value
        }
    }

    fun alCambiarTerminos(aceptado: Boolean) {
        terminosAceptados.value = aceptado
    }

    fun registrarUsuario(alExito: () -> Unit) {
        if (!formularioValido.value) return

        if (dbHelper.existeRutOCorreo(formatearRut(rut.value), correo.value.trim())) {
            _estadoUi.value = EstadoRegistro.Error("Este RUT o correo ya se encuentra registrado.")
            errorRut.value = "RUT o correo ya registrado"
            errorCorreo.value = "RUT o correo ya registrado"
            return
        }

        viewModelScope.launch {
            _estadoUi.value = EstadoRegistro.Cargando
            delay(1000)

            val rolDb = when (rolSeleccionado.value) {
                TipoRolUsuario.USUARIO_FINAL -> Rol.CLIENTE
                TipoRolUsuario.AGENTE_PAGOFLEX -> Rol.AGENTE
                TipoRolUsuario.EJECUTIVO_EMPRESA -> Rol.EJECUTIVO
            }

            val guardadoExitoso = dbHelper.registrarUsuario(
                nombre = nombre.value.trim(),
                rut = formatearRut(rut.value),
                correo = correo.value.trim(),
                contrasena = contrasena.value,
                pin = pinSeguridad.value,
                rol = rolDb,
                codigo = if (rolSeleccionado.value == TipoRolUsuario.USUARIO_FINAL) "UF-REG" else "EMP-REG",
                detalle = rolSeleccionado.value.titulo,
                avatar = nombre.value.take(2).uppercase()
            )

            if (guardadoExitoso) {
                SesionUsuarioManager.iniciarSesion(
                    context = getApplication(),
                    nombre = nombre.value.trim(),
                    rut = formatearRut(rut.value),
                    correo = correo.value.trim(),
                    rol = rolSeleccionado.value,
                    usuarioLogin = correo.value.trim(),
                    pin = pinSeguridad.value
                )
                _estadoUi.value = EstadoRegistro.Exito
                alExito()
            } else {
                _estadoUi.value = EstadoRegistro.Error("No se pudo guardar el registro en la base de datos.")
            }
        }
    }

    private fun formatearRut(rut: String): String {
        val limpio = rut.replace(Regex("[^0-9kK]"), "").uppercase()
        if (limpio.isEmpty()) return ""
        return if (limpio.length > 1) {
            val cuerpo = limpio.dropLast(1)
            val dv = limpio.last()
            val cuerpoFormateado = cuerpo.reversed().chunked(3).joinToString(".").reversed()
            "$cuerpoFormateado-$dv"
        } else {
            limpio
        }
    }

    private fun validarRutChileno(rut: String): Boolean {
        val limpio = rut.replace(".", "").replace("-", "").uppercase()
        if (limpio.length < 8) return false
        val cuerpo = limpio.dropLast(1)
        val dvIngresado = limpio.last()

        var suma = 0
        var multiplicador = 2
        for (i in cuerpo.reversed()) {
            if (!i.isDigit()) return false
            suma += (i.toString().toInt()) * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }
        val resto = suma % 11
        val resultado = 11 - resto
        val dvEsperado = when (resultado) {
            11 -> '0'
            10 -> 'K'
            else -> resultado.toString()[0]
        }
        return dvIngresado == dvEsperado
    }
}