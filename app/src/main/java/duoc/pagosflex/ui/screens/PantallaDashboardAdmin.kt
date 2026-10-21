package duoc.pagosflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import duoc.pagosflex.ui.components.TarjetaMetrica

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDashboardAdmin() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Supervisión FinTech PagoFlex", fontWeight = FontWeight.Bold) })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Métricas Globales de la Plataforma", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)


            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica(
                    titulo = "Usuarios Activos",
                    valor = "1,240",
                    icono = Icons.Default.Group,
                    modifier = Modifier.weight(1f)
                )
                TarjetaMetrica(
                    titulo = "Transacciones/Día",
                    valor = "342",
                    icono = Icons.Default.Assessment,
                    modifier = Modifier.weight(1f)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.padding(start = 8.dp))
                        Text("Estado de Auditoría y Cumplimiento", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Todos los logs encriptados conforme a la Ley 19.628.", style = MaterialTheme.typography.bodySmall)
                    Text("• Prevención de doble cobro activa en la pasarela.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}