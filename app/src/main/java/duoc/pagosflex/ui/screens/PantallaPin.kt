package duoc.pagosflex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.pagosflex.viewmodel.PinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPin(
    pinCorrecto: String,
    alPinCorrecto: () -> Unit,
    alCerrarSesion: () -> Unit,
    viewModel: PinViewModel = viewModel()
) {
    val pinIngresado by viewModel.pinIngresado.collectAsState()
    val errorPin by viewModel.errorPin.collectAsState()
    val estaSuspendido by viewModel.estaSuspendido.collectAsState()
    val segundosRestantes by viewModel.segundosRestantes.collectAsState()

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(valoresPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = if (estaSuspendido) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (estaSuspendido) "Acceso Suspendido" else "Ingrese su PIN de Seguridad",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (estaSuspendido) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = if (estaSuspendido) "Demasiados intentos erróneos. Espere para reintentar." else "Para continuar, ingrese su PIN de 4 dígitos (Demo: 1111)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (estaSuspendido) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Suspendido temporalmente por 1 minuto.\nIntente nuevamente en ${segundosRestantes}s",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { index ->
                            val lleno = index < pinIngresado.length
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(if (lleno) MaterialTheme.colorScheme.primary else Color.LightGray)
                            )
                        }
                    }

                    if (errorPin) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "PIN incorrecto. Intente nuevamente.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }

                val botones = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "⌫")
                )

                botones.forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        fila.forEach { tecla ->
                            Button(
                                onClick = {
                                    if (!estaSuspendido) {
                                        when (tecla) {
                                            "C" -> viewModel.limpiar()
                                            "⌫" -> viewModel.borrarUltimo()
                                            else -> viewModel.ingresarDigito(tecla, pinCorrecto, alPinCorrecto)
                                        }
                                    }
                                },
                                modifier = Modifier.size(72.dp),
                                shape = RoundedCornerShape(36.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (estaSuspendido) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                enabled = !estaSuspendido
                            ) {
                                Text(
                                    text = tecla,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (estaSuspendido) Color.Gray else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = alCerrarSesion,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        text = "Cerrar Sesión / Cambiar Usuario",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}