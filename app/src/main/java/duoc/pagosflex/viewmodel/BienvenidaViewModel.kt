package duoc.pagosflex.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class BienvenidaUiState(
    val canalOficialActivo: Boolean = true,
    val mensajeSeguridad: String = "Canal oficial para pagos y seguimiento.",
    val cargando: Boolean = false
)

class BienvenidaViewModel : ViewModel() {


    private val _uiState = MutableStateFlow(BienvenidaUiState())
    val uiState: StateFlow<BienvenidaUiState> = _uiState.asStateFlow()


    fun onSeleccionarPerfilClick(onNavegar: () -> Unit) {

        onNavegar()
    }


    fun onIngresarSesionClick(onNavegar: () -> Unit) {

        onNavegar()
    }
}