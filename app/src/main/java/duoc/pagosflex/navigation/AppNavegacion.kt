package duoc.pagosflex.navigation

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
    var pestanaEjecutivoInicial by remember { mutableIntStateOf(0) }
    var ultimoClickNavegacion by remember { mutableLongStateOf(0L) }

    fun navegarSeguro(accion: () -> Unit) {
        val ahora = System.currentTimeMillis()
        if (ahora - ultimoClickNavegacion > 400L) {
            ultimoClickNavegacion = ahora
            try {
                accion()
            } catch (_: Exception) {
            }
        }
    }

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
                    navegarSeguro { navController.navigate("login") }
                },
                alRegistrarse = {
                    navegarSeguro { navController.navigate("registro") }
                }
            )
        }

        composable("login") {
            PantallaLogin(
                alVolverAtras = {
                    navegarSeguro {
                        if (!navController.popBackStack()) {
                            navController.navigate("bienvenida") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                },
                alLoginExitoso = {
                    navegarSeguro {
                        navController.navigate("pin") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("registro") {
            PantallaRegistro(
                alVolverAtras = {
                    navegarSeguro {
                        if (!navController.popBackStack()) {
                            navController.navigate("bienvenida") {
                                popUpTo("registro") { inclusive = true }
                            }
                        }
                    }
                },
                alRegistroExitoso = {
                    navegarSeguro {
                        navController.navigate("pin") {
                            popUpTo("registro") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("pin") {
            val pinCorrecto = usuarioSesion.pin.ifBlank { "1111" }
            PantallaPin(
                pinCorrecto = pinCorrecto,
                alPinCorrecto = {
                    navegarSeguro {
                        navController.navigate("contenedor_principal") {
                            popUpTo("pin") { inclusive = true }
                        }
                    }
                },
                alCerrarSesion = {
                    navegarSeguro {
                        SesionUsuarioManager.cerrarSesion()
                        navController.navigate("bienvenida") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("contenedor_principal") {
            BackHandler(enabled = true) {
            }

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
                    if (ruta == "perfil" || ruta == "historial" || ruta.startsWith("pago/")) {
                        if (navController.currentDestination?.route != ruta) {
                            navegarSeguro {
                                navController.navigate(ruta) {
                                    launchSingleTop = true
                                }
                            }
                        }
                    } else {
                        if (navController.currentDestination?.route != "contenedor_principal") {
                            navegarSeguro {
                                navController.popBackStack("contenedor_principal", false)
                            }
                        }
                    }
                },
                onNavegarA = { ruta ->
                    when (ruta) {
                        "reportes_empresa" -> {
                            pestanaEjecutivoInicial = 2
                            itemNavegacionSeleccionado = 0
                            if (navController.currentDestination?.route != "contenedor_principal") {
                                navegarSeguro {
                                    navController.popBackStack("contenedor_principal", false)
                                }
                            }
                        }
                        "carga_empresa" -> {
                            pestanaEjecutivoInicial = 1
                            itemNavegacionSeleccionado = 0
                            if (navController.currentDestination?.route != "contenedor_principal") {
                                navegarSeguro {
                                    navController.popBackStack("contenedor_principal", false)
                                }
                            }
                        }
                        "contenedor_principal" -> {
                            pestanaEjecutivoInicial = 0
                            itemNavegacionSeleccionado = 0
                            if (navController.currentDestination?.route != "contenedor_principal") {
                                navegarSeguro {
                                    navController.popBackStack("contenedor_principal", false)
                                }
                            }
                        }
                        else -> {
                            if (navController.currentDestination?.route != ruta) {
                                navegarSeguro {
                                    navController.navigate(ruta) {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        }
                    }
                },
                onCerrarSesion = {
                    navegarSeguro {
                        SesionUsuarioManager.cerrarSesion()
                        navController.navigate("bienvenida") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            ) { alAbrirDrawer ->
                when (rolActual) {
                    TipoRolUsuario.USUARIO_FINAL -> {
                        if (itemNavegacionSeleccionado == 1) {
                            PantallaPago(idCompromiso = "C-101", alVolverAtras = { itemNavegacionSeleccionado = 0 })
                        } else {
                            PantallaInicio(
                                alAbrirDrawer = alAbrirDrawer,
                                alNavegarAPagar = { compromisoId ->
                                    navegarSeguro { navController.navigate("pago/$compromisoId") }
                                },
                                alNavegarAAgregar = {
                                    navegarSeguro { navController.navigate("agregar_compromiso") }
                                },
                                alNavegarAHistorial = {
                                    navegarSeguro { navController.navigate("historial") }
                                },
                                alNavegarAAyuda = {
                                    navegarSeguro { navController.navigate("ayuda") }
                                },
                                alCerrarSesion = {
                                    navegarSeguro {
                                        SesionUsuarioManager.cerrarSesion()
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                    }
                    TipoRolUsuario.EJECUTIVO_EMPRESA -> {
                        when (itemNavegacionSeleccionado) {
                            1 -> PantallaDashboardEmpresa(
                                alAbrirDrawer = alAbrirDrawer,
                                abrirDialogoNuevo = true,
                                pestanaInicial = 0,
                                alCerrarDialogoNuevo = { itemNavegacionSeleccionado = 0 }
                            )
                            2 -> PantallaDashboardEmpresa(
                                alAbrirDrawer = alAbrirDrawer,
                                pestanaInicial = 2
                            )
                            else -> PantallaDashboardEmpresa(
                                alAbrirDrawer = alAbrirDrawer,
                                pestanaInicial = pestanaEjecutivoInicial
                            )
                        }
                    }
                    TipoRolUsuario.AGENTE_PAGOFLEX -> {
                        if (itemNavegacionSeleccionado == 1) {
                            PantallaCasosAgente(alAbrirDrawer = alAbrirDrawer)
                        } else {
                            PantallaPanelAgente(alAbrirDrawer = alAbrirDrawer)
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
                    navegarSeguro {
                        if (!navController.popBackStack()) {
                            navController.navigate("contenedor_principal")
                        }
                    }
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
                    navegarSeguro {
                        if (!navController.popBackStack()) {
                            navController.navigate("contenedor_principal")
                        }
                    }
                },
                alNavegarADetalle = { idTicket ->
                    navegarSeguro { navController.navigate("detalle_ticket/$idTicket") }
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
                    navegarSeguro {
                        if (!navController.popBackStack()) {
                            navController.navigate("contenedor_principal")
                        }
                    }
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
                    navegarSeguro {
                        if (!navController.popBackStack()) {
                            navController.navigate("contenedor_principal")
                        }
                    }
                }
            )
        }

        composable("inicio") {
            LaunchedEffect(usuarioSesion.esSesionActiva) {
                if (!usuarioSesion.esSesionActiva) {
                    navController.navigate("login") { popUpTo("bienvenida") { inclusive = true } }
                } else {
                    navegarSeguro {
                        navController.navigate("contenedor_principal") {
                            popUpTo("contenedor_principal") { inclusive = false }
                        }
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
                    navegarSeguro {
                        if (!navController.popBackStack()) {
                            navController.navigate("contenedor_principal")
                        }
                    }
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
                    navegarSeguro {
                        if (!navController.popBackStack()) {
                            navController.navigate("contenedor_principal")
                        }
                    }
                },
                alCerrarSesion = {
                    navegarSeguro {
                        SesionUsuarioManager.cerrarSesion()
                        navController.navigate("bienvenida") {
                            popUpTo(0) { inclusive = true }
                        }
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
                    navegarSeguro {
                        if (!navController.popBackStack()) {
                            navController.navigate("contenedor_principal")
                        }
                    }
                }
            )
        }
    }
}