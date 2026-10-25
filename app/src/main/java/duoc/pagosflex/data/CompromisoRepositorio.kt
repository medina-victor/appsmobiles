package duoc.pagosflex.data

import android.content.Context
import android.content.SharedPreferences
import duoc.pagosflex.util.VerificadorVencimientos
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
    private var prefs: SharedPreferences? = null
    private var appContext: Context? = null

    fun inicializar(context: Context) {
        appContext = context.applicationContext
        prefs = context.applicationContext.getSharedPreferences("pagoflex_repo_prefs", Context.MODE_PRIVATE)
        cargarDatosGuardados()
        appContext?.let { VerificadorVencimientos.verificarYNotificar(it, _compromisos.value) }
    }

    private val _saldoCuenta = MutableStateFlow(2500000.0)
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

    private fun cargarDatosGuardados() {
        val p = prefs ?: return
        val saldoGuardado = p.getFloat("saldo_cuenta", -1f)
        if (saldoGuardado >= 0f) {
            _saldoCuenta.value = saldoGuardado.toDouble()
        }

        val compStr = p.getString("compromisos_data", null)
        if (!compStr.isNullOrBlank()) {
            try {
                val lista = compStr.split(";;").mapNotNull { item ->
                    val parts = item.split("|")
                    if (parts.size >= 8) {
                        Compromiso(parts[0], parts[1], parts[2], parts[3].toDouble(), parts[4], parts[5].toInt(), parts[6].toInt(), parts[7])
                    } else null
                }
                if (lista.isNotEmpty()) {
                    _compromisos.value = lista
                }
            } catch (_: Exception) {
            }
        }

        val histStr = p.getString("historial_data", null)
        if (!histStr.isNullOrBlank()) {
            try {
                val lista = histStr.split(";;").mapNotNull { item ->
                    val parts = item.split("|")
                    if (parts.size >= 5) {
                        ItemHistorial(parts[0], parts[1], parts[2], parts[3], parts[4].toDouble())
                    } else null
                }.toMutableList()
                if (lista.isNotEmpty()) {
                    _historial.value = lista
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun guardarEstado() {
        val p = prefs ?: return
        val editor = p.edit()
        editor.putFloat("saldo_cuenta", _saldoCuenta.value.toFloat())

        val compStr = _compromisos.value.joinToString(";;") {
            "${it.id}|${it.institucion}|${it.concepto}|${it.monto}|${it.fechaVencimiento}|${it.cuotaActual}|${it.totalCuotas}|${it.estado}"
        }
        editor.putString("compromisos_data", compStr)

        val histStr = _historial.value.joinToString(";;") {
            "${it.idTransaccion}|${it.institucion}|${it.concepto}|${it.fecha}|${it.monto}"
        }
        editor.putString("historial_data", histStr)
        editor.apply()
    }

    fun inicializarParaUsuario(esDemo: Boolean) {
        val p = prefs
        val saldoPrevio = p?.getFloat("saldo_cuenta", -1f) ?: -1f
        if (saldoPrevio < 0f) {
            _saldoCuenta.value = 2500000.0
            if (!esDemo) {
                _compromisos.value = emptyList()
            }
            guardarEstado()
        }
        appContext?.let { VerificadorVencimientos.verificarYNotificar(it, _compromisos.value) }
    }

    fun recargarSaldo(monto: Double) {
        _saldoCuenta.value += monto
        guardarEstado()
    }

    fun obtenerCompromiso(id: String): Compromiso? {
        return _compromisos.value.find { it.id == id }
    }

    fun agregarCompromiso(compromiso: Compromiso) {
        val listaActual = _compromisos.value.toMutableList()
        listaActual.add(0, compromiso)
        _compromisos.value = listaActual
        guardarEstado()
        appContext?.let { VerificadorVencimientos.verificarYNotificar(it, _compromisos.value) }
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
        guardarEstado()
        return true
    }
}