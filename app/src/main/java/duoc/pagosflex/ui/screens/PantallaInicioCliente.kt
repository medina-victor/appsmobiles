package duoc.pagosflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.pagosflex.ui.components.BotonPrimario
import duoc.pagosflex.ui.components.CampoContrasena
import duoc.pagosflex.ui.components.CampoDeTexto
import duoc.pagosflex.viewmodel.EstadoInicioSesion
import duoc.pagosflex.viewmodel.InicioClienteViewModel

@Composable
fun PantallaInicioCliente(
    alNavegarAlInicio: () -> Unit,
    alNavegarAlRegistro: () -> Unit,
    vistaModelo: InicioClienteViewModel = viewModel()
) {
    val estadoUi by vistaModelo.estadoUi.collectAsState()
    val usuario by vistaModelo.usuario.collectAsState()
    val contrasena by vistaModelo.contrasena.collectAsState()
    val errorUsuario by vistaModelo.errorUsuario.collectAsState()
    val errorContrasena by vistaModelo.errorContrasena.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "PagoFlex",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Gestión inteligente de compromisos de pago",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                CampoDeTexto(
                    valor = usuario,
                    alCambiarValor = { vistaModelo.alCambiarUsuario(it) },
                    etiqueta = "Nombre de usuario",
                    icono = Icons.Default.Person,
                    mensajeError = errorUsuario
                )

                Spacer(modifier = Modifier.height(8.dp))

                CampoContrasena(
                    valor = contrasena,
                    alCambiarValor = { vistaModelo.alCambiarContrasena(it) },
                    etiqueta = "Contraseña",
                    mensajeError = errorContrasena
                )

                if (estadoUi is EstadoInicioSesion.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (estadoUi as EstadoInicioSesion.Error).mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                BotonPrimario(
                    texto = "Iniciar Sesión",
                    onClick = { vistaModelo.iniciarSesion(alExito = alNavegarAlInicio) },
                    cargando = estadoUi is EstadoInicioSesion.Cargando
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = alNavegarAlRegistro,
                    enabled = estadoUi !is EstadoInicioSesion.Cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(text = "Crear Cuenta ")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { }) {
                    Text(
                        text = "¿Olvidó su contraseña?",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}