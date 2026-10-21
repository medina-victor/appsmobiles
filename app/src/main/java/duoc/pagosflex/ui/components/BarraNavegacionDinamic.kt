package duoc.pagosflex.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import duoc.pagosflex.viewmodel.TipoRolUsuario

data class ElementoNavegacion(
    val titulo: String,
    val icono: ImageVector,
    val idRuta: String
)

@Composable
fun BarraNavegacionDinamica(
    rolActual: TipoRolUsuario,
    itemSeleccionado: Int,
    onTabSeleccionado: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = when (rolActual) {
        TipoRolUsuario.CLIENTE -> listOf(
            ElementoNavegacion("Inicio", Icons.Default.Home, "inicio"),
            ElementoNavegacion("Compromisos", Icons.Default.AccountBalanceWallet, "compromisos"),
            ElementoNavegacion("Pagar", Icons.Default.Payment, "pagar"),
            ElementoNavegacion("Historial", Icons.Default.History, "historial"),
            ElementoNavegacion("Perfil", Icons.Default.Person, "perfil")
        )
        TipoRolUsuario.EMPRESA -> listOf(
            ElementoNavegacion("Dashboard", Icons.Default.Business, "dashboard_empresa"),
            ElementoNavegacion("Cobros", Icons.Default.ReceiptLong, "cobros_empresa"),
            ElementoNavegacion("Perfil", Icons.Default.Person, "perfil")
        )
        TipoRolUsuario.ADMINISTRADOR -> listOf(
            ElementoNavegacion("Métricas", Icons.Default.Assessment, "dashboard_admin"),
            ElementoNavegacion("Seguridad", Icons.Default.Security, "auditoria_admin"),
            ElementoNavegacion("Perfil", Icons.Default.Person, "perfil")
        )
    }

    NavigationBar(
        modifier = modifier.height(64.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { indice, elemento ->
            val seleccionado = itemSeleccionado == indice
            val escala by animateFloatAsState(
                targetValue = if (seleccionado) 1.25f else 1.0f,
                animationSpec = spring(dampingRatio = 0.5f),
                label = ""
            )

            NavigationBarItem(
                selected = seleccionado,
                onClick = { onTabSeleccionado(indice, elemento.idRuta) },
                icon = {
                    Icon(
                        imageVector = elemento.icono,
                        contentDescription = elemento.titulo,
                        modifier = Modifier
                            .size(24.dp)
                            .scale(escala)
                    )
                },
                label = { Text(elemento.titulo) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}