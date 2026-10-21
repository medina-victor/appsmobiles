package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import duoc.pagosflex.ui.screens.CobroEmpresa

class EmpresaViewModel : ViewModel() {

    private val _cobros = MutableStateFlow<List<CobroEmpresa>>(emptyList())
    val cobros: StateFlow<List<CobroEmpresa>> = _cobros.asStateFlow()

    private val _totalRecaudado = MutableStateFlow(0.0)
    val totalRecaudado: StateFlow<Double> = _totalRecaudado.asStateFlow()

    init {
        cargarCobrosIniciales()
    }

    private fun cargarCobrosIniciales() {
        viewModelScope.launch {
            val lista = listOf(
                CobroEmpresa("CB-901", "Juan Pérez Cotapos", "18.223.114-K", 145000.0, "Pagado"),
                CobroEmpresa("CB-902", "María González Silva", "15.882.301-4", 145000.0, "Pendiente"),
                CobroEmpresa("CB-903", "Carlos Rojas Vera", "19.102.883-2", 145000.0, "Vencido")
            )
            _cobros.value = lista
            calcularTotal()
        }
    }

    fun agregarCobro(deudor: String, rut: String, monto: Double) {
        val nuevoCobro = CobroEmpresa(
            id = "CB-${(100..999).random()}",
            deudor = deudor,
            rut = rut,
            monto = monto,
            estado = "Pendiente"
        )
        _cobros.value = _cobros.value + nuevoCobro
        calcularTotal()
    }

    private fun calcularTotal() {
        _totalRecaudado.value = _cobros.value.filter { it.estado == "Pagado" }.sumOf { it.monto }
    }
}

