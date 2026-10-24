package duoc.pagosflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.ui.components.BarraNavegacionDinamica
import duoc.pagosflex.viewmodel.TipoRolUsuario
import kotlinx.coroutines.launch

@Composable
fun PantallaPrincipalContenedor(
    rolActual: TipoRolUsuario,
    itemSeleccionado: Int,
    onTabSeleccionado: (Int, String) -> Unit,
    onNavegarA: (String) -> Unit,
    onCerrarSesion: () -> Unit,
    contenidoPantalla: @Composable (alAbrirDrawer: () -> Unit) -> Unit
) {
    val estadoDrawer = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val usuario by SesionUsuarioManager.usuarioActual.collectAsState()

    ModalNavigationDrawer(
        drawerState = estadoDrawer,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD32F2F))
                            .padding(20.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.DarkGray)
                                    .clickable {
                                        scope.launch { estadoDrawer.close() }
                                        onNavegarA("perfil")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (usuario.fotoUri != null) {
                                    AsyncImage(
                                        model = usuario.fotoUri,
                                        contentDescription = "Foto Perfil",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = if (usuario.nombre.isNotBlank()) usuario.nombre else "Usuario PagoFlex",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            if (usuario.usuarioLogin.isNotBlank()) {
                                Text(
                                    text = "@${usuario.usuarioLogin}",
                                    color = Color.White.copy(alpha = 0.9f),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = rolActual.titulo,
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (rolActual) {
                        TipoRolUsuario.USUARIO_FINAL -> {
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                                label = { Text("Panel Principal") },
                                selected = true,
                                onClick = {
                                    scope.launch { estadoDrawer.close() }
                                    onNavegarA("contenedor_principal")
                                }
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.History, contentDescription = null) },
                                label = { Text("Historial de Pagos") },
                                selected = false,
                                onClick = {
                                    scope.launch { estadoDrawer.close() }
                                    onNavegarA("historial")
                                }
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Security, contentDescription = null) },
                                label = { Text("Seguridad y Privacidad") },
                                selected = false,
                                onClick = {
                                    scope.launch { estadoDrawer.close() }
                                    onNavegarA("terminos")
                                }
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Help, contentDescription = null) },
                                label = { Text("Centro de Ayuda") },
                                selected = false,
                                onClick = {
                                    scope.launch { estadoDrawer.close() }
                                    onNavegarA("ayuda")
                                }
                            )
                        }
                        TipoRolUsuario.AGENTE_PAGOFLEX -> {
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.SupportAgent, contentDescription = null) },
                                label = { Text("Panel de Control y Soporte") },
                                selected = true,
                                onClick = {
                                    scope.launch { estadoDrawer.close() }
                                    onNavegarA("contenedor_principal")
                                }
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Security, contentDescription = null) },
                                label = { Text("Seguridad y Privacidad") },
                                selected = false,
                                onClick = {
                                    scope.launch { estadoDrawer.close() }
                                    onNavegarA("terminos")
                                }
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                                label = { Text("Mi Perfil y Ajustes") },
                                selected = false,
                                onClick = {
                                    scope.launch { estadoDrawer.close() }
                                    onNavegarA("perfil")
                                }
                            )
                        }
                        TipoRolUsuario.EJECUTIVO_EMPRESA -> {
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Business, contentDescription = null) },
                                label = { Text("Panel Institucional / Cobranza") },
                                selected = true,
                                onClick = {
                                    scope.launch { estadoDrawer.close() }
                                    onNavegarA("contenedor_principal")
                                }
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) },
                                label = { Text("Carga Masiva de Convenios") },
                                selected = false,
                                onClick = {
                                    scope.launch { estadoDrawer.close() }
                                    onNavegarA("contenedor_principal")
                                }
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                                label = { Text("Reportes Financieros") },
                                selected = false,
                                onClick = {
                                    scope.launch { estadoDrawer.close() }
                                    onNavegarA("contenedor_principal")
                                }
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Security, contentDescription = null) },
                                label = { Text("Seguridad y Privacidad") },
                                selected = false,
                                onClick = {
                                    scope.launch { estadoDrawer.close() }
                                    onNavegarA("terminos")
                                }
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                                label = { Text("Mi Perfil y Ajustes") },
                                selected = false,
                                onClick = {
                                    scope.launch { estadoDrawer.close() }
                                    onNavegarA("perfil")
                                }
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) },
                        label = { Text("Cerrar Sesión", color = Color.Red) },
                        selected = false,
                        onClick = {
                            scope.launch { estadoDrawer.close() }
                            onCerrarSesion()
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                BarraNavegacionDinamica(
                    rolActual = rolActual,
                    itemSeleccionado = itemSeleccionado,
                    onTabSeleccionado = onTabSeleccionado
                )
            }
        ) { valoresPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(valoresPadding)
            ) {
                contenidoPantalla {
                    scope.launch { estadoDrawer.open() }
                }
            }
        }
    }
}