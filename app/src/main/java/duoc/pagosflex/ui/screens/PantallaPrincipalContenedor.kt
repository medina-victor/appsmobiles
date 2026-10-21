package duoc.pagosflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import duoc.pagosflex.ui.components.BarraNavegacionInferior
import duoc.pagosflex.ui.screens.PantallaHistorial
import duoc.pagosflex.ui.screens.PantallaInicio
import duoc.pagosflex.ui.screens.PantallaPerfil
import kotlinx.coroutines.launch

@Composable
fun PantallaPrincipalContenedor(
    alNavegarAPagar: (String) -> Unit,
    alCerrarSesion: () -> Unit
) {
    var tabSeleccionado by remember { mutableStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Usuario PagoFlex",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "RUT: 12.345.678-9",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                    label = { Text("Mis Compromisos") },
                    selected = tabSeleccionado == 0,
                    onClick = {
                        tabSeleccionado = 0
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("Historial de Pagos") },
                    selected = tabSeleccionado == 3,
                    onClick = {
                        tabSeleccionado = 3
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Security, contentDescription = null) },
                    label = { Text("Seguridad y Privacidad") },
                    selected = tabSeleccionado == 4,
                    onClick = {
                        tabSeleccionado = 4
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Help, contentDescription = null) },
                    label = { Text("Centro de Ayuda") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    label = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        alCerrarSesion()
                    }
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                BarraNavegacionInferior(
                    itemSeleccionado = tabSeleccionado,
                    onTabSeleccionado = { indice ->
                        if (indice == 2) {
                            alNavegarAPagar("C-101")
                        } else {
                            tabSeleccionado = indice
                        }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (tabSeleccionado) {
                    0 -> PantallaInicio(
                        alAbrirDrawer = { scope.launch { drawerState.open() } },
                        alNavegarAPagar = alNavegarAPagar,
                        alCerrarSesion = alCerrarSesion
                    )
                    1 -> PantallaInicio(
                        alAbrirDrawer = { scope.launch { drawerState.open() } },
                        alNavegarAPagar = alNavegarAPagar,
                        alCerrarSesion = alCerrarSesion
                    )
                    3 -> PantallaHistorial()
                    4 -> PantallaPerfil(alCerrarSesion = alCerrarSesion)
                }
            }
        }
    }
}