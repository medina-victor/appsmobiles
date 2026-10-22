package duoc.pagosflex.viewmodel



sealed interface EstadoRegistro {
    object Idle : EstadoRegistro
    object Cargando : EstadoRegistro
    object Exito : EstadoRegistro
    data class Error(val mensaje: String) : EstadoRegistro
}