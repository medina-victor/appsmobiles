package duoc.pagosflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
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
import java.text.NumberFormat
import java.util.Locale

data class CobroEmpresa(
    val id: String,
    val deudor: String,
    val rut: String,
    val monto: Double,
    val estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDashboardEmpresa() {
    val cobros = listOf(
        CobroEmpresa("CB-901", "Juan Pérez Cotapos", "18.223.114-K", 145000.0, "Pagado"),
        CobroEmpresa("CB-902", "María González Silva", "15.882.301-4", 145000.0, "Pendiente"),
        CobroEmpresa("CB-903", "Carlos Rojas Vera", "19.102.883-2", 145000.0, "Vencido")
    )

    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Panel Institución / Empresa", fontWeight = FontWeight.Bold) })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaMetrica(
                    titulo = "Recaudado Mes",
                    valor = formatoMoneda.format(435000.0),
                    icono = Icons.Default.AttachMoney,
                    modifier = Modifier.weight(1f)
                )
                TarjetaMetrica(
                    titulo = "Cobros Emitidos",
                    valor = "24",
                    icono = Icons.Default.ReceiptLong,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Nómina de Cobros", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(onClick = { }, shape = RoundedCornerShape(8.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text(" Nuevo Cobro")
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(cobros) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.deudor, fontWeight = FontWeight.Bold)
                                Text("RUT: ${item.rut}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(formatoMoneda.format(item.monto), fontWeight = FontWeight.Bold)
                                Text(item.estado, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}