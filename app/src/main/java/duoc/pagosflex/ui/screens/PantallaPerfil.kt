package duoc.pagosflex.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import duoc.pagosflex.data.CompromisoRepositorio
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.ui.components.BotonPrimario
import duoc.pagosflex.util.NotificacionHelper
import duoc.pagosflex.viewmodel.PerfilViewModel
import duoc.pagosflex.viewmodel.TipoRolUsuario
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(
    alVolverAtras: () -> Unit,
    alCerrarSesion: () -> Unit,
    vistaModelo: PerfilViewModel = viewModel()
) {
    val usuario by vistaModelo.usuarioSesion.collectAsState()
    val contexto = LocalContext.current

    var notificacionesActivadas by remember { mutableStateOf(true) }
    var autenticacionBiometrica by remember { mutableStateOf(false) }
    var nuevoAlias by remember { mutableStateOf(usuario.usuarioLogin) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val tieneNumeros = nuevoAlias.any { it.isDigit() }
    val errorAlias = if (tieneNumeros) "El alias no puede contener números" else null

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { SesionUsuarioManager.actualizarFoto(contexto, it) }
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito && tempCameraUri != null) {
            SesionUsuarioManager.actualizarFoto(contexto, tempCameraUri)
        }
    }

    fun lanzarCamaraReal() {
        val archivo = File(contexto.cacheDir, "foto_perfil_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            contexto,
            "${contexto.packageName}.provider",
            archivo
        )
        tempCameraUri = uri
        camaraLauncher.launch(uri)
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            lanzarCamaraReal()
        }
    }

    val permisoGaleriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            galeriaLauncher.launch("image/*")
        }
    }

    val permisoNotificacionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        NotificacionHelper.mostrarNotificacionRecordatorio(
            contexto,
            "Pago Próximo a Vencer",
            "Tienes una cuota de PagoFlex pendiente que vence mañana."
        )
    }

    fun tomarFoto() {
        val estadoPermiso = ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA)
        if (estadoPermiso == PackageManager.PERMISSION_GRANTED) {
            lanzarCamaraReal()
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun abrirGaleria() {
        val permiso = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val estadoPermiso = ContextCompat.checkSelfPermission(contexto, permiso)
        if (estadoPermiso == PackageManager.PERMISSION_GRANTED) {
            galeriaLauncher.launch("image/*")
        } else {
            permisoGaleriaLauncher.launch(permiso)
        }
    }

    fun probarNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val estadoPermiso = ContextCompat.checkSelfPermission(contexto, Manifest.permission.POST_NOTIFICATIONS)
            if (estadoPermiso == PackageManager.PERMISSION_GRANTED) {
                NotificacionHelper.mostrarNotificacionRecordatorio(
                    contexto,
                    "Pago Próximo a Vencer",
                    "Tienes una cuota de PagoFlex pendiente que vence mañana."
                )
            } else {
                permisoNotificacionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            NotificacionHelper.mostrarNotificacionRecordatorio(
                contexto,
                "Pago Próximo a Vencer",
                "Tienes una cuota de PagoFlex pendiente que vence mañana."
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes y Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = alVolverAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (usuario.fotoUri != null) {
                    AsyncImage(
                        model = usuario.fotoUri,
                        contentDescription = "Foto Perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.Gray
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { abrirGaleria() }) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Galería")
                }

                Button(onClick = { tomarFoto() }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cámara")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (usuario.nombre.isNotBlank()) usuario.nombre else "Usuario PagoFlex",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (usuario.usuarioLogin.isNotBlank()) {
                        Text(
                            text = "@${usuario.usuarioLogin}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = if (usuario.correo.isNotBlank()) usuario.correo else "sin-correo@pagoflex.cl",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (usuario.rut.isNotBlank()) "RUT: ${usuario.rut}" else "RUT: No registrado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Rol: ${usuario.rol.titulo}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            if (usuario.rol == TipoRolUsuario.USUARIO_FINAL) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Gestión de Saldo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        OutlinedButton(
                            onClick = { CompromisoRepositorio.recargarSaldo(100000.0) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Recargar Saldo Cuenta (+$100.000)")
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Personalizar Nombre de Usuario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = nuevoAlias,
                        onValueChange = { nuevoAlias = it },
                        label = { Text("Nombre de Usuario / Alias") },
                        isError = errorAlias != null,
                        supportingText = {
                            if (errorAlias != null) {
                                Text(text = errorAlias, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    BotonPrimario(
                        texto = "Guardar Alias",
                        onClick = { vistaModelo.guardarAliasUsuario(nuevoAlias) },
                        habilitado = nuevoAlias.isNotBlank() && errorAlias == null
                    )
                }
            }

            Text(
                text = "Configuración y Notificaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                            Spacer(modifier = Modifier.padding(start = 8.dp))
                            Text("Notificaciones push de vencimiento")
                        }
                        Switch(checked = notificacionesActivadas, onCheckedChange = { notificacionesActivadas = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null)
                            Spacer(modifier = Modifier.padding(start = 8.dp))
                            Text("Autenticación biométrica")
                        }
                        Switch(checked = autenticacionBiometrica, onCheckedChange = { autenticacionBiometrica = it })
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedButton(
                        onClick = { probarNotificacion() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Probar Notificación Push")
                    }
                }
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
                        Text("Privacidad de Datos (Ley 19.628)", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = { vistaModelo.cerrarSesion(alCerrarSesion) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}