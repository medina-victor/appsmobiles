package duoc.pagosflex.navigation

sealed class Rutas(val ruta: String) {
    object Bienvenida : Rutas("pantalla_bienvenida")
    object SeleccionarPerfil : Rutas("pantalla_seleccionar_perfil")
    object SeleccionarRol : Rutas("pantalla_seleccionar_rol")
    object InicioCliente : Rutas("pantalla_inicio_cliente")
    object Registro : Rutas("pantalla_registro")
}
