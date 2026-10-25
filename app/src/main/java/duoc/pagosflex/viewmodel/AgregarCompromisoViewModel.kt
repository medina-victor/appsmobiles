package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import duoc.pagosflex.data.Compromiso
import duoc.pagosflex.data.CompromisoRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AgregarCompromisoViewModel : ViewModel() {

    var institucion = MutableStateFlow("")
    var concepto = MutableStateFlow("")
    var monto = MutableStateFlow("")
    var fechaVencimiento = MutableStateFlow("")
    var cuotasTotal = MutableStateFlow("1")

    private val _errorFormulario = MutableStateFlow<String?>(null)
    val errorFormulario: StateFlow<String?> = _errorFormulario.asStateFlow()

    fun guardarCompromiso(alExito: () -> Unit) {
        val inst = institucion.value.trim()
        val conc = concepto.value.trim()
        val mnt = monto.value.toDoubleOrNull() ?: 0.0
        val fecha = fechaVencimiento.value.trim()
        val cuotas = cuotasTotal.value.toIntOrNull() ?: 1

        if (inst.isBlank() || conc.isBlank() || mnt <= 0.0 || fecha.isBlank()) {
            _errorFormulario.value = "Complete todos los campos obligatorios con valores válidos."
            return
        }

        val nuevoId = "C-" + (105..999).random()
        val nuevoCompromiso = Compromiso(
            id = nuevoId,
            institucion = inst,
            concepto = conc,
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