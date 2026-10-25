package duoc.pagosflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import duoc.pagosflex.data.CobroEmpresaDetalle
import duoc.pagosflex.data.EmpresaRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class EmpresaViewModel(application: Application) : AndroidViewModel(application) {

    val cobros: StateFlow<List<CobroEmpresaDetalle>> = EmpresaRepositorio.cobrosEmpresa
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var textoBusqueda = MutableStateFlow("")
    var filtroEstado = MutableStateFlow("Todos")
    var mesFiltro = MutableStateFlow("Todos")

    val cobrosFiltrados: StateFlow<List<CobroEmpresaDetalle>> = combine(
        cobros, textoBusqueda, filtroEstado, mesFiltro
    ) { lista, busqueda, estado, mes ->
        lista.filter { cobro ->
            val coincideTexto = cobro.deudor.contains(busqueda, ignoreCase = true) ||
                    cobro.rut.contains(busqueda, ignoreCase = true) ||
                    cobro.concepto.contains(busqueda, ignoreCase = true)
            val coincideEstado = estado.equals("Todos", true) || cobro.estado.equals(estado, true)
            val coincideMes = mes.equals("Todos", true) || cobro.fechaVencimiento.endsWith(mes) || cobro.fechaVencimiento.contains("/$mes/")
            coincideTexto && coincideEstado && coincideMes
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun registrarCobro(
        deudor: String,
        rut: String,
        correo: String,
        telefono: String,
        concepto: String,
        monto: Double,
        fechaVencimiento: String,
        cuotaActual: Int,
        totalCuotas: Int
    ): Boolean {
        return EmpresaRepositorio.registrarNuevoCompromiso(
            deudor = deudor,
            rut = rut,
            correo = correo,
            telefono = telefono,
            concepto = concepto,
            monto = monto,
            fechaVencimiento = fechaVencimiento,
            cuotaActual = cuotaActual,
            totalCuotas = totalCuotas
        )
    }

    fun anularCobro(id: String, motivo: String, usuarioAnula: String): Boolean {
        return EmpresaRepositorio.anularCompromiso(id, motivo, usuarioAnula)
    }
}