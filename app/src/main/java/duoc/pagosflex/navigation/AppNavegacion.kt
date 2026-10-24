package duoc.pagosflex.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.ui.screens.PantallaAgregarCompromiso
import duoc.pagosflex.ui.screens.PantallaAyuda
import duoc.pagosflex.ui.screens.PantallaBienvenida
import duoc.pagosflex.ui.screens.PantallaCasosAgente
import duoc.pagosflex.ui.screens.PantallaDashboardEmpresa
import duoc.pagosflex.ui.screens.PantallaDetalleTicket
import duoc.pagosflex.ui.screens.PantallaHistorial
import duoc.pagosflex.ui.screens.PantallaInicio
import duoc.pagosflex.ui.screens.PantallaLogin
import duoc.pagosflex.ui.screens.PantallaPago
import duoc.pagosflex.ui.screens.PantallaPanelAgente
import duoc.pagosflex.ui.screens.PantallaPerfil
import duoc.pagosflex.ui.screens.PantallaPin
import duoc.pagosflex.ui.screens.PantallaPrincipalContenedor
import duoc.pagosflex.ui.screens.PantallaRegistro
import duoc.pagosflex.ui.screens.PantallaTerminos
import duoc.pagosflex.viewmodel.TipoRolUsuario

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val contexto = LocalContext.current
    val usuarioSesion by SesionUsuarioManager.usuarioActual.collectAsState()
    var itemNavegacionSeleccionado by remember { mutableIntStateOf(0) }

    val sesionActivaInicial = remember {
        val prefs = contexto.getSharedPreferences("pagoflex_sesion_prefs", Context.MODE_PRIVATE)
        prefs.getBoolean("es_activa", false)
    }

    NavHost(
        navController = navController,
        startDestination = if (sesionActivaInicial || usuarioSesion.esSesionActiva) "pin" else "bienvenida"
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
                    if (!navController.popBackStack()) {
                        navController.navigate("bienvenida") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                alLoginExitoso = {
                    navController.navigate("pin") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("registro") {
            PantallaRegistro(
                alVolverAtras = {
                    if (!navController.popBackStack()) {
                        navController.navigate("bienvenida") {
                            popUpTo("registro") { inclusive = true }
                        }
                    }
                },
                alRegistroExitoso = {
                    navController.navigate("pin") {
                        popUpTo("registro") { inclusive = true }
                    }
                }
            )
        }

        composable("pin") {
            val pinCorrecto = usuarioSesion.pin.ifBlank { "1111" }
            PantallaPin(
                pinCorrecto = pinCorrecto,
                alPinCorrecto = {
                    navController.navigate("contenedor_principal") {
                        popUpTo("pin") { inclusive = true }
                    }
                },
                alCerrarSesion = {
                    SesionUsuarioManager.cerrarSesion()
                    navController.navigate("bienvenida") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("contenedor_principal") {
            LaunchedEffect(usuarioSesion.esSesionActiva) {
                if (!usuarioSesion.esSesionActiva) {
                    navController.navigate("login") {
                        popUpTo("bienvenida") { inclusive = true }
                    }
                }
            }

            val rolActual = usuarioSesion.rol

            PantallaPrincipalContenedor(
                rolActual = rolActual,
                itemSeleccionado = itemNavegacionSeleccionado,
                onTabSeleccionado = { indice, ruta ->
                    itemNavegacionSeleccionado = indice
                    if (ruta != "panel_agente" && ruta != "casos_agente" && navController.currentDestination?.route != ruta) {
                        navController.navigate(ruta) {
                            popUpTo("contenedor_principal") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onNavegarA = { ruta ->
                    if (navController.currentDestination?.route != ruta) {
                        navController.navigate(ruta) {
                            launchSingleTop = true
                        }
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
                            alNavegarAAgregar = {
                                navController.navigate("agregar_compromiso")
                            },
                            alNavegarAHistorial = {
                                navController.navigate("historial")
                            },
                            alNavegarAAyuda = {
                                navController.navigate("ayuda")
                            },
                            alCerrarSesion = {
                                SesionUsuarioManager.cerrarSesion()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                    TipoRolUsuario.EJECUTIVO_EMPRESA -> {
                        PantallaDashboardEmpresa()
                    }
                    TipoRolUsuario.AGENTE_PAGOFLEX -> {
                        if (itemNavegacionSeleccionado == 1) {
                            PantallaCasosAgente()
                        } else {
                            PantallaPanelAgente()
                        }
                    }
                }
            }
        }

        composable("agregar_compromiso") {
            LaunchedEffect(usuarioSesion.esSesionActiva) {
                if (!usuarioSesion.esSesionActiva) {
                    navController.navigate("login") { popUpTo("bienvenida") { inclusive = true } }
                }
            }
            PantallaAgregarCompromiso(
                alVolverAtras = {
                    navController.popBackStack()
                }
            )
        }

        composable("ayuda") {
            LaunchedEffect(usuarioSesion.esSesionActiva) {
                if (!usuarioSesion.esSesionActiva) {
                    navController.navigate("login") { popUpTo("bienvenida") { inclusive = true } }
                }
            }
            PantallaAyuda(
                alVolverAtras = {
                    navController.popBackStack()
                },
                alNavegarADetalle = { idTicket ->
                    navController.navigate("detalle_ticket/$idTicket")
                }
            )
        }

        composable("detalle_ticket/{idTicket}") { backStackEntry ->
            LaunchedEffect(usuarioSesion.esSesionActiva) {
                if (!usuarioSesion.esSesionActiva) {
                    navController.navigate("login") { popUpTo("bienvenida") { inclusive = true } }
                }
            }
            val idTicket = backStackEntry.arguments?.getString("idTicket") ?: ""
            PantallaDetalleTicket(
                idTicket = idTicket,
                alVolverAtras = {
                    navController.popBackStack()
                }
            )
        }

        composable("terminos") {
            LaunchedEffect(usuarioSesion.esSesionActiva) {
                if (!usuarioSesion.esSesionActiva) {
                    navController.navigate("login") { popUpTo("bienvenida") { inclusive = true } }
                }
            }
            PantallaTerminos(
                alVolverAtras = {
                    navController.popBackStack()
                }
            )
        }

        composable("inicio") {
            LaunchedEffect(usuarioSesion.esSesionActiva) {
                if (!usuarioSesion.esSesionActiva) {
                    navController.navigate("login") { popUpTo("bienvenida") { inclusive = true } }
                } else {
                    navController.navigate("contenedor_principal") {
                        popUpTo("contenedor_principal") { inclusive = true }
                    }
                }
            }
        }

        composable("historial") {
            LaunchedEffect(usuarioSesion.esSesionActiva) {
                if (!usuarioSesion.esSesionActiva) {
                    navController.navigate("login") { popUpTo("bienvenida") { inclusive = true } }
                }
            }
            PantallaHistorial(
                alVolverAtras = {
                    navController.popBackStack()
                }
            )
        }

        composable("perfil") {
            LaunchedEffect(usuarioSesion.esSesionActiva) {
                if (!usuarioSesion.esSesionActiva) {
                    navController.navigate("login") { popUpTo("bienvenida") { inclusive = true } }
                }
            }
            PantallaPerfil(
                alVolverAtras = {
                    navController.popBackStack()
                },
                alCerrarSesion = {
                    SesionUsuarioManager.cerrarSesion()
                    navController.navigate("bienvenida") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("pago/{compromisoId}") { backStackEntry ->
            LaunchedEffect(usuarioSesion.esSesionActiva) {
                if (!usuarioSesion.esSesionActiva) {
                    navController.navigate("login") { popUpTo("bienvenida") { inclusive = true } }
                }
            }
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