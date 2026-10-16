package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import duoc.pagosflex.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProductoCuenta(
    val id: String,
    val tipo: String, // ej: "Cuenta Corriente", "Cuenta de Ahorro"
    val numeroCuenta: String,
    val saldoDisponible: Double,
    val icono: String
)

data class InicioClienteUiState(
    val usuario: Usuario? = null,
    val productos: List<ProductoCuenta> = emptyList(),
    val tabSeleccionado: Int = 0,
    val cargando: Boolean = false
)

class InicioClienteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InicioClienteUiState())
    val uiState: StateFlow<InicioClienteUiState> = _uiState.asStateFlow()

    fun cargarDatosCliente(usuario: Usuario) {
        // Generar el número de cuenta formato "0" + RUT sin puntos
        val rutLimpio = usuario.rut.replace(".", "").replace("-", "")
        val numCuentaPrincipal = "0$rutLimpio"
        val numCuentaAhorro = "09$rutLimpio"

        val listaProductos = listOf(
            ProductoCuenta(
                id = "cta_1",
                tipo = "Cuenta Principal Flex",
                numeroCuenta = numCuentaPrincipal,
                saldoDisponible = 1250800.0,
                icono = "💳"
            ),
            ProductoCuenta(
                id = "cta_2",
                tipo = "Cuenta de Ahorro Flex",
                numeroCuenta = numCuentaAhorro,
                saldoDisponible = 450000.0,
                icono = "🐷"
            )
        )

        _uiState.value = InicioClienteUiState(
            usuario = usuario,
            productos = listaProductos
        )
    }

    fun onTabCambiar(nuevoTab: Int) {
        _uiState.value = _uiState.value.copy(tabSeleccionado = nuevoTab)
    }
}