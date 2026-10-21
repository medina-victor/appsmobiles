package duoc.pagosflex.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import duoc.pagosflex.ui.screens.PantallaPrincipalContenedor
import duoc.pagosflex.ui.screens.PantallaBienvenida
import duoc.pagosflex.ui.screens.PantallaDashboardAdmin
import duoc.pagosflex.ui.screens.PantallaDashboardEmpresa
import duoc.pagosflex.ui.screens.PantallaInicioCliente
import duoc.pagosflex.ui.screens.PantallaPago
import duoc.pagosflex.ui.screens.PantallaRegistro

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.Bienvenida.ruta
    ) {
        composable(Rutas.Bienvenida.ruta) {
            PantallaBienvenida(
                alIniciarSesion = { navController.navigate(Rutas.Login.ruta) },
                alRegistrarse = { navController.navigate(Rutas.Registro.ruta) }
            )
        }

        composable(Rutas.Login.ruta) {
            PantallaInicioCliente(
                alNavegarAlInicio = {
                    navController.navigate(Rutas.Inicio.ruta) {
                        popUpTo(Rutas.Bienvenida.ruta) { inclusive = true }
                    }
                },
                alNavegarAlRegistro = { navController.navigate(Rutas.Registro.ruta) }
            )
        }

        composable(Rutas.Registro.ruta) {
            PantallaRegistro(
                alVolverAtras = { navController.popBackStack() },
                alRegistroExitoso = {
                    navController.navigate(Rutas.Inicio.ruta) {
                        popUpTo(Rutas.Registro.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.Inicio.ruta) {
            PantallaPrincipalContenedor(
                alNavegarAPagar = { idCompromiso ->
                    navController.navigate(Rutas.Pago.crearRuta(idCompromiso))
                },
                alCerrarSesion = {
                    navController.navigate(Rutas.Login.ruta) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard_empresa") {
            PantallaDashboardEmpresa()
        }

        composable("dashboard_admin") {
            PantallaDashboardAdmin()
        }

        composable(
            route = Rutas.Pago.ruta,
            arguments = listOf(navArgument("idCompromiso") { type = NavType.StringType })
        ) { backStackEntry ->
            val idCompromiso = backStackEntry.arguments?.getString("idCompromiso") ?: "C-101"
            PantallaPago(
                idCompromiso = idCompromiso,
                alVolverAtras = { navController.popBackStack() }
            )
        }
    }
}