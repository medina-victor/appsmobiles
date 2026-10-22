package duoc.pagosflex.navigation

sealed class Rutas(val ruta: String) {
    object Bienvenida : Rutas("bienvenida")
    object Login : Rutas("login")
    object Registro : Rutas("registro")
    object Inicio : Rutas("inicio")
    object DashboardEmpresa : Rutas("dashboard_empresa")
    object PanelAgente : Rutas("panel_agente")
    object Pago : Rutas("pago/{idCompromiso}") {
        fun crearRuta(idCompromiso: String) = "pago/$idCompromiso"
    }
}