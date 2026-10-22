package duoc.pagosflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import duoc.pagosflex.ui.components.BotonVolver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTerminos(
    alVolverAtras: () -> Unit
) {
    Scaffold(
        topBar = {
            BotonVolver(onVolver = alVolverAtras, titulo = "Términos y Privacidad")
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Política de Privacidad y Ley 19.628",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "PagoFlex es una aplicación desarrollada con fines estrictamente académicos para la asignatura DSY1105 de Duoc UC. Los datos recolectados se utilizan exclusivamente para la simulación de cobros, pagos de convenios y gestión de perfiles de usuario dentro del entorno de pruebas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Protección de Datos Personales",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nos comprometemos a aplicar principios de minimización de datos. No se almacenan credenciales bancarias reales ni contraseñas en texto plano. Las transacciones mostradas son simuladas y no representan cargos financieros reales en sistemas bancarios.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}