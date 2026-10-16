package duoc.pagosflex.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import duoc.pagosflex.ui.screens.PantallaBienvenida
import duoc.pagosflex.ui.screens.PantallaInicioCliente
import duoc.pagosflex.ui.screens.PantallaRegistro
import duoc.pagosflex.ui.screens.PantallaSeleccionarPerfil

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.Bienvenida.ruta
    ) {
        // 1. Pantalla Inicial Minimalista
        composable(Rutas.Bienvenida.ruta) {
            PantallaBienvenida(
                onIniciarSesionClick = {
                    navController.navigate(Rutas.SeleccionarPerfil.ruta)
                }
            )
        }

        // 2. Selección de Perfil o Crear Nuevo
        composable(Rutas.SeleccionarPerfil.ruta) {
            PantallaSeleccionarPerfil(
                onVolver = {
                    navController.popBackStack()
                },
                onCrearCuentaClick = {
                    navController.navigate(Rutas.Registro.ruta)
                },
                onPerfilSeleccionado = { usuario ->
                    navController.navigate(Rutas.InicioCliente.ruta)
                }
            )
        }

        // 3. Crear Cuenta Nueva
        composable(Rutas.Registro.ruta) {
            PantallaRegistro(
                onVolver = {
                    navController.popBackStack()
                },
                onRegistroExitoso = {
                    navController.navigate(Rutas.InicioCliente.ruta) {
                        popUpTo(Rutas.Bienvenida.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.InicioCliente.ruta) {
            // Tomamos el usuario activo seleccionado previamente
            val usuarioEjemplo = duoc.pagosflex.model.Usuario(
                id = "usr_demo",
                nombre = "Juan Pérez",
                rut = "19.876.543-2",
                rol = duoc.pagosflex.model.Rol.CLIENTE,
                avatarIniciales = "JP"
            )



        }
        composable(Rutas.InicioCliente.ruta) {
            // Usuario demo para pruebas
            val usuarioEjemplo = duoc.pagosflex.model.Usuario(
                id = "usr_demo",
                nombre = "Juan Pérez",
                rut = "19.876.543-2",
                rol = duoc.pagosflex.model.Rol.CLIENTE,
                avatarIniciales = "JP"
            )

            PantallaInicioCliente(
                usuario = usuarioEjemplo,
                onVolver = {
                    // Regresa a la pantalla de selección o bienvenida
                    navController.popBackStack(Rutas.Bienvenida.ruta, inclusive = false)
                },
                onIrAPagarCuenta = { /* Próxima pantalla */ },
                onIrAServicios = { /* Próxima pantalla */ },
                onIrADividendo = { /* Próxima pantalla */ }
            )
        }
    }
}