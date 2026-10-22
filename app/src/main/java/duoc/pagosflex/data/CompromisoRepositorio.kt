package duoc.pagosflex.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Compromiso(
    val id: String,
    val institucion: String,
    val concepto: String,
    val monto: Double,
    val fechaVencimiento: String,
    val cuotaActual: Int,
    val totalCuotas: Int,
    val estado: String = "Pendiente"
)

data class ItemHistorial(
    val idTransaccion: String,
    val institucion: String,
    val concepto: String,
    val fecha: String,
    val monto: Double
)

object CompromisoRepositorio {
    private val _saldoCuenta = MutableStateFlow(550000.0)
    val saldoCuenta: StateFlow<Double> = _saldoCuenta.asStateFlow()

    private val _compromisos = MutableStateFlow(
        listOf(
            Compromiso("C-101", "PagoFlex Educación", "Arancel Cuota", 145000.0, "05/11/2026", 5, 10),
            Compromiso("C-102", "PagoFlex Servicios", "Consumo Agua", 24500.0, "12/11/2026", 1, 1),
            Compromiso("C-103", "PagoFlex Tránsito", "Tag Mensual", 18900.0, "20/11/2026", 1, 1),
            Compromiso("C-104", "PagoFlex Hipotecario", "Dividendo Cuota", 385000.0, "15/11/2026", 12, 240)
        )
    )
    val compromisos: StateFlow<List<Compromiso>> = _compromisos.asStateFlow()

    private val _historial = MutableStateFlow(
        mutableListOf(
            ItemHistorial("TX-882193", "PagoFlex Educación", "Arancel Cuota", "05/10/2026", 145000.0),
            ItemHistorial("TX-771239", "PagoFlex Servicios", "Consumo Agua", "12/09/2026", 24500.0)
        )
    )
    val historial: StateFlow<List<ItemHistorial>> = _historial.asStateFlow()

    fun inicializarParaUsuario(esDemo: Boolean) {
        if (esDemo) {
            _saldoCuenta.value = 550000.0
            _compromisos.value = listOf(
                Compromiso("C-101", "PagoFlex Educación", "Arancel Cuota", 145000.0, "05/11/2026", 5, 10),
                Compromiso("C-102", "PagoFlex Servicios", "Consumo Agua", 24500.0, "12/11/2026", 1, 1),
                Compromiso("C-103", "PagoFlex Tránsito", "Tag Mensual", 18900.0, "20/11/2026", 1, 1),
                Compromiso("C-104", "PagoFlex Hipotecario", "Dividendo Cuota", 385000.0, "15/11/2026", 12, 240)
            )
        } else {
            _saldoCuenta.value = 150000.0
            _compromisos.value = emptyList()
        }
    }

    fun obtenerCompromiso(id: String): Compromiso? {
        return _compromisos.value.find { it.id == id }
    }

    fun agregarCompromiso(compromiso: Compromiso) {
        val listaActual = _compromisos.value.toMutableList()
        listaActual.add(0, compromiso)
        _compromisos.value = listaActual
    }

    fun registrarPagoExitoso(idCompromiso: String, idTransaccion: String, monto: Double): Boolean {
        if (_saldoCuenta.value < monto) {
            return false
        }
        _saldoCuenta.value -= monto

        val compromiso = obtenerCompromiso(idCompromiso)
        val institucionNombre = compromiso?.institucion ?: "PagoFlex"
        val conceptoNombre = compromiso?.concepto ?: "Pago de Servicio"
        val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL")).format(Date())

        _historial.value.add(
            0,
            ItemHistorial(
                idTransaccion = idTransaccion,
                institucion = institucionNombre,
                concepto = conceptoNombre,
                fecha = fechaActual,
                monto = monto
            )
        )

        val listaActualizada = _compromisos.value.mapNotNull { c ->
            if (c.id == idCompromiso) {
                val nuevaCuota = c.cuotaActual + 1
                if (nuevaCuota > c.totalCuotas) {
                    null
                } else {
                    c.copy(cuotaActual = nuevaCuota, estado = "Pendiente")
                }
            } else {
                c
            }
        }

        _compromisos.value = listaActualizada
        return true
    }
}