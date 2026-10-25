package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import duoc.pagosflex.data.Compromiso
import duoc.pagosflex.data.CompromisoRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AgregarCompromisoViewModel : ViewModel() {

    var categoriaSeleccionada = MutableStateFlow("Luz")
    var empresaSeleccionada = MutableStateFlow("Enel Distribución")
    var numeroCliente = MutableStateFlow("")
    var rutTitular = MutableStateFlow("")
    var monto = MutableStateFlow("")
    var fechaVencimiento = MutableStateFlow("")
    var cuotasTotal = MutableStateFlow("1")

    private val _errorFormulario = MutableStateFlow<String?>(null)
    val errorFormulario: StateFlow<String?> = _errorFormulario.asStateFlow()

    fun alCambiarFecha(entrada: String) {
        val soloNumeros = entrada.replace(Regex("[^0-9]"), "").take(8)
        
        if (soloNumeros.length >= 2) {
            val d = soloNumeros.take(2).toIntOrNull() ?: 0
            if (d < 1 || d > 31) return
        }
        if (soloNumeros.length >= 4) {
            val m = soloNumeros.substring(2, 4).toIntOrNull() ?: 0
            if (m < 1 || m > 12) return
        }

        val formateada = when {
            soloNumeros.length <= 2 -> soloNumeros
            soloNumeros.length <= 4 -> "${soloNumeros.substring(0, 2)}/${soloNumeros.substring(2)}"
            else -> "${soloNumeros.substring(0, 2)}/${soloNumeros.substring(2, 4)}/${soloNumeros.substring(4)}"
        }
        fechaVencimiento.value = formateada
    }

    fun alCambiarRut(entrada: String) {
        val limpio = entrada.replace(".", "").replace("-", "")
        if (limpio.isEmpty()) {
            rutTitular.value = ""
            return
        }

        val cuerpo = limpio.dropLast(1).filter { it.isDigit() }
        val dvChar = limpio.last().uppercaseChar()

        if (limpio.length == 1) {
            if (dvChar.isDigit() || dvChar == 'K') {
                rutTitular.value = dvChar.toString()
            }
            return
        }

        if (!dvChar.isDigit() && dvChar != 'K') return

        val rutFormateado = if (cuerpo.isNotEmpty()) {
            val cuerpoFormat = cuerpo.reversed().chunked(3).joinToString(".").reversed()
            "$cuerpoFormat-$dvChar"
        } else {
            dvChar.toString()
        }

        rutTitular.value = rutFormateado
    }

    fun guardarCompromiso(alExito: () -> Unit) {
        val emp = empresaSeleccionada.value.trim()
        val numCli = numeroCliente.value.trim()
        val mnt = monto.value.toDoubleOrNull() ?: 0.0
        val fecha = fechaVencimiento.value.trim()
        val cuotas = cuotasTotal.value.toIntOrNull() ?: 1

        if (fecha.length < 10) {
            _errorFormulario.value = "Ingrese una fecha válida en formato dd/mm/yyyy."
            return
        }

        val partesFecha = fecha.split("/")
        val dia = partesFecha.getOrNull(0)?.toIntOrNull() ?: 0
        val mes = partesFecha.getOrNull(1)?.toIntOrNull() ?: 0
        val anio = partesFecha.getOrNull(2)?.toIntOrNull() ?: 0

        if (dia !in 1..31 || mes !in 1..12 || anio !in 2024..2035) {
            _errorFormulario.value = "Fecha de vencimiento inválida. Verifique día, mes y año."
            return
        }

        if (numCli.isBlank() || mnt <= 0.0) {
            _errorFormulario.value = "Complete el número de cliente y un monto válido."
            return
        }

        val nuevoId = "C-" + (105..999).random()
        val nuevoCompromiso = Compromiso(
            id = nuevoId,
            institucion = emp,
            concepto = "N° Cliente: $numCli",
            monto = mnt,
            fechaVencimiento = fecha,
            cuotaActual = 1,
            totalCuotas = cuotas,
            estado = "Pendiente"
        )

        CompromisoRepositorio.agregarCompromiso(nuevoCompromiso)
        alExito()
    }
}