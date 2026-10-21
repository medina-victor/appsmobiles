package duoc.pagosflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import duoc.pagosflex.ui.components.BotonPrimario
import duoc.pagosflex.ui.components.BotonVolver
import duoc.pagosflex.ui.components.CampoDeTexto
import duoc.pagosflex.ui.components.TarjetaDetallePago
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPago(
    idCompromiso: String,
    alVolverAtras: () -> Unit
) {
    var numeroTarjeta by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var procesando by remember { mutableStateOf(false) }
    var pagoCompletado by remember { mutableStateOf(false) }

    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    Scaffold(
        topBar = {
            BotonVolver(
                onVolver = alVolverAtras,
                titulo = if (pagoCompletado) "Comprobante" else "Pasarela de Pago"
            )
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
            if (!pagoCompletado) {
                TarjetaDetallePago(
                    concepto = "Arancel Mensual Cuota 02",
                    institucion = "Universidad Duoc UC",
                    monto = formatoMoneda.format(145000.0),
                    metodoPago = "Tarjeta de Débito/Crédito"
                )

                Text(text = "Datos de la Tarjeta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                CampoDeTexto(
                    valor = numeroTarjeta,
                    alCambiarValor = { if (it.length <= 16) numeroTarjeta = it },
                    etiqueta = "Número de Tarjeta",
                    icono = Icons.Default.CreditCard
                )

                CampoDeTexto(
                    valor = cvv,
                    alCambiarValor = { if (it.length <= 4) cvv = it },
                    etiqueta = "Código CVV",
                    icono = Icons.Default.Pin
                )

                Spacer(modifier = Modifier.height(8.dp))

                BotonPrimario(
                    texto = "Confirmar y Pagar",
                    onClick = {
                        procesando = true
                        pagoCompletado = true
                        procesando = false
                    },
                    habilitado = numeroTarjeta.length >= 15 && cvv.length >= 3,
                    cargando = procesando
                )
            } else {
                TarjetaDetallePago(
                    concepto = "Pago Exitoso - N° Transacción TX-99120",
                    institucion = "Universidad Duoc UC",
                    monto = formatoMoneda.format(145000.0),
                    metodoPago = "Aprobado WebPay / Banco"
                )

                Spacer(modifier = Modifier.height(16.dp))

                BotonPrimario(
                    texto = "Volver al Inicio",
                    onClick = alVolverAtras
                )
            }
        }
    }
}