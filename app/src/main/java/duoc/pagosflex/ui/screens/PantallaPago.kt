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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.pagosflex.ui.components.BotonPrimario
import duoc.pagosflex.ui.components.BotonVolver
import duoc.pagosflex.ui.components.CampoDeTexto
import duoc.pagosflex.ui.components.TarjetaDetallePago
import duoc.pagosflex.viewmodel.EstadoPago
import duoc.pagosflex.viewmodel.PagoViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPago(
    idCompromiso: String,
    alVolverAtras: () -> Unit,
    vistaModelo: PagoViewModel = viewModel()
) {
    val compromiso = vistaModelo.obtenerCompromiso(idCompromiso)
    val metodoPago by vistaModelo.metodoPago.collectAsState()
    val numeroTarjeta by vistaModelo.numeroTarjeta.collectAsState()
    val cvv by vistaModelo.cvv.collectAsState()
    val expiracion by vistaModelo.expiracion.collectAsState()
    val saldoCuenta by vistaModelo.saldoCuenta.collectAsState()
    val estadoUi by vistaModelo.estadoUi.collectAsState()
    val estaProcesando by vistaModelo.estaProcesando.collectAsState()

    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    val monto = compromiso?.monto ?: 0.0
    val concepto = "${compromiso?.institucion ?: "PagoFlex"} - ${compromiso?.concepto ?: "Compromiso"}"

    Scaffold(
        topBar = {
            BotonVolver(
                onVolver = alVolverAtras,
                titulo = if (estadoUi is EstadoPago.Exito) "Comprobante de Pago" else "Pasarela de Pago"
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
            if (compromiso == null && estadoUi !is EstadoPago.Exito) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = "Compromiso no encontrado o ya pagado.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                BotonPrimario(texto = "Volver", onClick = alVolverAtras)
                return@Column
            }

            when (val estado = estadoUi) {
                is EstadoPago.Exito -> {
                    TarjetaDetallePago(
                        concepto = "Pago Exitoso - N° Transacción ${estado.idTransaccion}",
                        institucion = compromiso?.institucion ?: "PagoFlex",
                        monto = formatoMoneda.format(estado.monto),
                        metodoPago = "Aprobado vía $metodoPago"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "El pago se ha procesado con éxito. Se ha actualizado su cuota y descontado el saldo de su cuenta.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BotonPrimario(
                        texto = "Volver al Inicio",
                        onClick = alVolverAtras
                    )
                }
                else -> {
                    TarjetaDetallePago(
                        concepto = "${compromiso?.concepto} (Cuota ${compromiso?.cuotaActual}/${compromiso?.totalCuotas})",
                        institucion = compromiso?.institucion ?: "",
                        monto = formatoMoneda.format(monto),
                        metodoPago = "Vencimiento: ${compromiso?.fechaVencimiento}"
                    )

                    Text(
                        text = "Seleccione Método de Pago",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    val metodos = listOf("Saldo PagoFlex", "Tarjeta", "Transferencia Bancaria")
                    metodos.forEach { metodo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = metodoPago == metodo,
                                    onClick = { vistaModelo.alCambiarMetodoPago(metodo) }
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = metodoPago == metodo,
                                onClick = { vistaModelo.alCambiarMetodoPago(metodo) }
                            )
                            Text(
                                text = if (metodo == "Saldo PagoFlex") "Saldo Disponible en Cuenta (${formatoMoneda.format(saldoCuenta)})" else metodo,
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (metodoPago == "Tarjeta") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Datos de Tarjeta",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        CampoDeTexto(
                            valor = numeroTarjeta,
                            alCambiarValor = { vistaModelo.alCambiarNumeroTarjeta(it) },
                            etiqueta = "Número de Tarjeta (16 dígitos)",
                            icono = Icons.Default.CreditCard
                        )

                        CampoDeTexto(
                            valor = expiracion,
                            alCambiarValor = { vistaModelo.alCambiarExpiracion(it) },
                            etiqueta = "Expiración (MM/AA)",
                            icono = Icons.Default.DateRange
                        )

                        CampoDeTexto(
                            valor = cvv,
                            alCambiarValor = { vistaModelo.alCambiarCvv(it) },
                            etiqueta = "Código CVV (3-4 dígitos)",
                            icono = Icons.Default.Lock
                        )
                    }

                    if (estado is EstadoPago.Error) {
                        Text(
                            text = estado.mensaje,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    BotonPrimario(
                        texto = "Confirmar y Pagar ${formatoMoneda.format(monto)}",
                        onClick = {
                            vistaModelo.procesarPago(
                                idCompromiso = idCompromiso,
                                monto = monto,
                                concepto = concepto
                            )
                        },
                        habilitado = (metodoPago != "Tarjeta" || (numeroTarjeta.length >= 15 && cvv.length >= 3 && expiracion.length == 5)) && !estaProcesando,
                        cargando = estaProcesando
                    )
                }
            }
        }
    }
}