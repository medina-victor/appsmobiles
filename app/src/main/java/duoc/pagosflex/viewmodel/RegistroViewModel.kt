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

    private val _estadoUi = MutableStateFlow<EstadoRegistro>(EstadoRegistro.Idle)
    val estadoUi: StateFlow<EstadoRegistro> = _estadoUi.asStateFlow()

    var nombre = MutableStateFlow("")
    var rut = MutableStateFlow("")
    var correo = MutableStateFlow("")
    var contrasena = MutableStateFlow("")
    var confirmarContrasena = MutableStateFlow("")
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
    var terminosAceptados = MutableStateFlow(false)

    val formularioValido: StateFlow<Boolean> = combine(
        nombre, rut, correo, contrasena, confirmarContrasena,
        terminosAceptados, tieneMinimoCaracteres, tieneMayuscula,
        tieneMinuscula, tieneNumero, tieneCaracterEspecial, contrasenasCoinciden,
        errorNombre, errorRut, errorCorreo
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
        val errNom = params[12] as String?
        val errRut = params[13] as String?
        val errCor = params[14] as String?

        nom.isNotBlank() && errNom == null &&
                r.isNotBlank() && errRut == null &&
                cor.isNotBlank() && errCor == null &&
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
        val contieneNumeros = nuevoNombre.any { it.isDigit() }
        val soloLetrasYEspacios = nuevoNombre.all { it.isLetter() || it.isWhitespace() }

        when {
            nuevoNombre.isBlank() -> {
                errorNombre.value = "El nombre es obligatorio"
            }
            contieneNumeros -> {
                errorNombre.value = "El nombre no puede contener números"
            }
            !soloLetrasYEspacios -> {
                errorNombre.value = "El nombre solo debe contener letras"
            }
            nuevoNombre.trim().length < 15 -> {
                errorNombre.value = "Debe tener al menos 15 caracteres (llevas ${nuevoNombre.trim().length})"
            }
            else -> {
                errorNombre.value = null
            }
        }
    }

    fun alCambiarRut(nuevoRut: String) {
        val rutFormateado = formatearRut(nuevoRut)
        rut.value = rutFormateado
        val esEmpresa = rolSeleccionado.value == TipoRolUsuario.EJECUTIVO_EMPRESA

        when {
            rutFormateado.isBlank() -> {
                errorRut.value = "El RUT es obligatorio"
            }
            !validarRutChileno(rutFormateado) -> {
                errorRut.value = "RUT o dígito verificador inválido"
            }
            else -> {
                val numeroRut = rutFormateado.replace(".", "").split("-")[0].toIntOrNull() ?: 0
                if (esEmpresa && numeroRut < 50000000) {
                    errorRut.value = "Debe ingresar RUT de Empresa (Persona Jurídica)"
                } else if (!esEmpresa && numeroRut >= 50000000) {
                    errorRut.value = "El RUT pertenece a Empresa, use el rol adecuado"
                } else {
                    errorRut.value = null
                }
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

    fun alCambiarTerminos(aceptado: Boolean) {
        terminosAceptados.value = aceptado
    }

    fun registrarUsuario(alExito: () -> Unit) {
        if (!formularioValido.value) return

        viewModelScope.launch {
            _estadoUi.value = EstadoRegistro.Cargando
            delay(1200)

            SesionUsuarioManager.iniciarSesion(
                nombre = nombre.value.trim(),
                rut = rut.value,
                correo = correo.value,
                rol = rolSeleccionado.value
            )

            _estadoUi.value = EstadoRegistro.Exito
            alExito()
        }
    }

    private fun formatearRut(rut: String): String {
        val limpio = rut.replace(".", "").replace("-", "").uppercase()
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
        val dvEsperado = when (val resultado = 11 - resto) {
            11 -> '0'
            10 -> 'K'
            else -> resultado.toString()[0]
        }
        return dvIngresado == dvEsperado
    }
}