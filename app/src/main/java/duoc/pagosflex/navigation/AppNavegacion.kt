package duoc.pagosflex.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.ui.screens.PantallaBienvenida
import duoc.pagosflex.ui.screens.PantallaDashboardEmpresa
import duoc.pagosflex.ui.screens.PantallaHistorial
import duoc.pagosflex.ui.screens.PantallaInicio
import duoc.pagosflex.ui.screens.PantallaLogin
import duoc.pagosflex.ui.screens.PantallaPago
import duoc.pagosflex.ui.screens.PantallaPanelAgente
import duoc.pagosflex.ui.screens.PantallaPerfil
import duoc.pagosflex.ui.screens.PantallaPrincipalContenedor
import duoc.pagosflex.ui.screens.PantallaRegistro
import duoc.pagosflex.viewmodel.TipoRolUsuario

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val usuarioSesion by SesionUsuarioManager.usuarioActual.collectAsState()
    var itemNavegacionSeleccionado by remember { mutableIntStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = "bienvenida"
    ) {
        composable("bienvenida") {
            PantallaBienvenida(
                alIniciarSesion = {
                    navController.navigate("login")
                },
                alRegistrarse = {
                    navController.navigate("registro")
                }
            )
        }

        composable("login") {
            PantallaLogin(
                alVolverAtras = {
                    navController.popBackStack()
                },
                alLoginExitoso = {
                    navController.navigate("contenedor_principal") {
                        popUpTo("bienvenida") { inclusive = true }
                    }
                }
            )
        }

        composable("registro") {
            PantallaRegistro(
                alVolverAtras = {
                    navController.popBackStack()
                },
                alRegistroExitoso = {
                    navController.navigate("contenedor_principal") {
                        popUpTo("bienvenida") { inclusive = true }
                    }
                }
            )
        }

        composable("contenedor_principal") {
            val rolActual = usuarioSesion.rol

            PantallaPrincipalContenedor(
                rolActual = rolActual,
                itemSeleccionado = itemNavegacionSeleccionado,
                onTabSeleccionado = { indice, ruta ->
                    itemNavegacionSeleccionado = indice
                    if (navController.currentDestination?.route != ruta) {
                        navController.navigate(ruta) {
                            popUpTo("contenedor_principal") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onNavegarA = { ruta ->
                    if (navController.currentDestination?.route != ruta) {
                        navController.navigate(ruta)
                    }
                },
                onCerrarSesion = {
                    SesionUsuarioManager.cerrarSesion()
                    navController.navigate("bienvenida") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) { alAbrirDrawer ->
                when (rolActual) {
                    TipoRolUsuario.USUARIO_FINAL -> {
                        PantallaInicio(
                            alAbrirDrawer = alAbrirDrawer,
                            alNavegarAPagar = { compromisoId ->
                                navController.navigate("pago/$compromisoId")
                            },
                            alCerrarSesion = {
                                SesionUsuarioManager.cerrarSesion()
                                navController.navigate("bienvenida") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                    TipoRolUsuario.EJECUTIVO_EMPRESA -> {
                        PantallaDashboardEmpresa()
                    }
                    TipoRolUsuario.AGENTE_PAGOFLEX -> {
                        PantallaPanelAgente()
                    }
                }
            }
        }

        composable("inicio") {
            navController.navigate("contenedor_principal") {
                popUpTo("contenedor_principal") { inclusive = true }
            }
        }

        composable("historial") {
            PantallaHistorial()
        }

        composable("perfil") {
            PantallaPerfil(
                alCerrarSesion = {
                    SesionUsuarioManager.cerrarSesion()
                    navController.navigate("bienvenida") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("pago/{compromisoId}") { backStackEntry ->
            val compromisoId = backStackEntry.arguments?.getString("compromisoId") ?: ""
            PantallaPago(
                idCompromiso = compromisoId,
                alVolverAtras = {
                    navController.popBackStack()
                }
            )
        }
    }
}