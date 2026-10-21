package duoc.pagosflex.navigation

sealed class Rutas(val ruta: String) {
    object Bienvenida : Rutas("bienvenida")
    object SeleccionarPerfil : Rutas("seleccionar_perfil")
    object Login : Rutas("login")
    object Registro : Rutas("registro")
    object Inicio : Rutas("inicio")
    object Pago : Rutas("pago/{idCompromiso}") {
        fun crearRuta(idCompromiso: String) = "pago/$idCompromiso"
    }
}
