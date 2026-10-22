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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import duoc.pagosflex.ui.components.BotonTerminosYCondiciones
import duoc.pagosflex.ui.components.BotonVolver
import duoc.pagosflex.ui.components.CampoContrasena
import duoc.pagosflex.ui.components.CampoDeTexto
import duoc.pagosflex.ui.components.CampoRut
import duoc.pagosflex.ui.components.ListaRequerimientosPassword
import duoc.pagosflex.ui.components.SelectorRol
import duoc.pagosflex.viewmodel.EstadoRegistro
import duoc.pagosflex.viewmodel.RegistroViewModel
import duoc.pagosflex.viewmodel.TipoRolUsuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(
    alVolverAtras: () -> Unit,
    alRegistroExitoso: () -> Unit,
    vistaModelo: RegistroViewModel = viewModel()
) {
    val estadoUi by vistaModelo.estadoUi.collectAsState()
    val nombre by vistaModelo.nombre.collectAsState()
    val rut by vistaModelo.rut.collectAsState()
    val correo by vistaModelo.correo.collectAsState()
    val contrasena by vistaModelo.contrasena.collectAsState()
    val confirmarContrasena by vistaModelo.confirmarContrasena.collectAsState()
    val pin by vistaModelo.pinSeguridad.collectAsState()
    val confirmarPin by vistaModelo.confirmarPin.collectAsState()
    val rolSeleccionado by vistaModelo.rolSeleccionado.collectAsState()
    val terminosAceptados by vistaModelo.terminosAceptados.collectAsState()
    val esValido by vistaModelo.formularioValido.collectAsState()

    val errorNombre by vistaModelo.errorNombre.collectAsState()
    val errorRut by vistaModelo.errorRut.collectAsState()
    val errorCorreo by vistaModelo.errorCorreo.collectAsState()

    val minChar by vistaModelo.tieneMinimoCaracteres.collectAsState()
    val mayus by vistaModelo.tieneMayuscula.collectAsState()
    val minus by vistaModelo.tieneMinuscula.collectAsState()
    val num by vistaModelo.tieneNumero.collectAsState()
    val esp by vistaModelo.tieneCaracterEspecial.collectAsState()
    val coinciden by vistaModelo.contrasenasCoinciden.collectAsState()
    val pinesCoinciden by vistaModelo.pinesCoinciden.collectAsState()

    val esEmpresa = rolSeleccionado == TipoRolUsuario.EJECUTIVO_EMPRESA

    Scaffold(
        topBar = {
            BotonVolver(
                onVolver = alVolverAtras,
                titulo = "Registro de Usuario"
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresPadding)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Crear Cuenta PagoFlex",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                SelectorRol(
                    rolSeleccionado = rolSeleccionado,
                    alSeleccionarRol = { vistaModelo.alCambiarRol(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                CampoDeTexto(
                    valor = nombre,
                    alCambiarValor = { vistaModelo.alCambiarNombre(it) },
                    etiqueta = if (esEmpresa) "Nombre de la Empresa / Razón Social" else "Nombre Completo",
                    icono = Icons.Default.Person,
                    mensajeError = errorNombre
                )

                CampoRut(
                    valor = rut,
                    alCambiarValor = { vistaModelo.alCambiarRut(it) },
                    etiqueta = if (esEmpresa) "RUT de la Empresa (Ej: 76.543.210-K)" else "RUT Personal (Ej: 12.345.678-9)",
                    mensajeError = errorRut
                )

                CampoDeTexto(
                    valor = correo,
                    alCambiarValor = { vistaModelo.alCambiarCorreo(it) },
                    etiqueta = if (esEmpresa) "Correo Corporativo" else "Correo Electrónico",
                    icono = Icons.Default.Email,
                    mensajeError = errorCorreo
                )

                CampoContrasena(
                    valor = contrasena,
                    alCambiarValor = { vistaModelo.alCambiarContrasena(it) },
                    etiqueta = "Contraseña"
                )

                CampoContrasena(
                    valor = confirmarContrasena,
                    alCambiarValor = { vistaModelo.alCambiarConfirmarContrasena(it) },
                    etiqueta = "Confirmar Contraseña"
                )

                CampoDeTexto(
                    valor = pin,
                    alCambiarValor = { vistaModelo.alCambiarPin(it) },
                    etiqueta = "PIN de Seguridad (4 dígitos)",
                    icono = Icons.Default.Lock
                )

                CampoDeTexto(
                    valor = confirmarPin,
                    alCambiarValor = { vistaModelo.alCambiarConfirmarPin(it) },
                    etiqueta = "Confirmar PIN de Seguridad (4 dígitos)",
                    icono = Icons.Default.Lock,
                    mensajeError = if (confirmarPin.isNotEmpty() && !pinesCoinciden) "Los PIN no coinciden" else null
                )

                Spacer(modifier = Modifier.height(4.dp))

                ListaRequerimientosPassword(
                    minChar = minChar,
                    mayus = mayus,
                    minus = minus,
                    num = num,
                    esp = esp,
                    coinciden = coinciden
                )

                BotonTerminosYCondiciones(
                    aceptado = terminosAceptados,
                    alCambiarAceptado = { vistaModelo.alCambiarTerminos(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                BotonPrimario(
                    texto = "Crear Cuenta",
                    onClick = { vistaModelo.registrarUsuario(alRegistroExitoso) },
                    habilitado = esValido,
                    cargando = estadoUi is EstadoRegistro.Cargando
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}