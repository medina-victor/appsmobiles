package duoc.pagosflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import duoc.pagosflex.ui.components.BotonVolver

data class PreguntaFrecuente(
    val pregunta: String,
    val respuesta: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAyuda(
    alVolverAtras: () -> Unit
) {
    val preguntas = listOf(
        PreguntaFrecuente(
            "¿Cómo se procesan los pagos en PagoFlex?",
            "Los pagos se simulan mediante una pasarela segura para fines académicos (DSY1105). No se realizan cargos bancarios reales."
        ),
        PreguntaFrecuente(
            "¿Qué hacer si mi pago no aparece en el historial?",
            "El historial se actualiza de forma inmediata al confirmar la transacción. Si persiste algún inconveniente, contacte a soporte desde su panel."
        ),
        PreguntaFrecuente(
            "¿Cómo agregar una nueva cuenta de servicios?",
            "Desde la pantalla de inicio, presione el botón 'Agregar Cuenta', seleccione la categoría e institución proveedora e ingrese los datos solicitados."
        ),
        PreguntaFrecuente(
            "¿Es seguro almacenar mis datos personales?",
            "PagoFlex cumple con los lineamientos de minimización de datos y protección bajo la Ley 19.628."
        )
    )

    Scaffold(
        topBar = {
            BotonVolver(onVolver = alVolverAtras, titulo = "Centro de Ayuda y FAQ")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Preguntas Frecuentes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(preguntas) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.pregunta,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.respuesta,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}