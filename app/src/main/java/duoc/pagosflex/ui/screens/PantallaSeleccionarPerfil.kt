package duoc.pagosflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.pagosflex.model.Usuario
import duoc.pagosflex.ui.components.BotonVolver
import duoc.pagosflex.ui.theme.*
import duoc.pagosflex.viewmodel.SeleccionarPerfilViewModel

@Composable
fun PantallaSeleccionarPerfil(
    onVolver: () -> Unit,
    onCrearCuentaClick: () -> Unit,
    onPerfilSeleccionado: (Usuario) -> Unit,
    viewModel: SeleccionarPerfilViewModel = viewModel()
) {
    val usuarios by viewModel.usuarios.collectAsState()
    val usuarioSeleccionado by viewModel.usuarioSeleccionado.collectAsState()

    // Estado para alternar entre Login directo y Selección rápida de Perfil
    var modoModoDemo by remember { mutableStateOf(false) }
    var rutIngresado by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
            .statusBarsPadding() // <- Protege contra el notch y la cámara del teléfono
            .padding(horizontal = Dimens.espacioPantalla)
    ) {
        // --- BARRA SUPERIOR CON ESPACIO SEGURO ---
        BotonVolver(
            onVolver = onVolver,
            titulo = "Ingresar"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- TITULO PRINCIPAL LIMPIO ---
        Text(
            text = if (modoModoDemo) "Perfiles Disponibles" else "Inicia Sesión",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro
        )

        Text(
            text = if (modoModoDemo)
                "Elige un perfil para simular la sesión:"
            else
                "Ingresa con tu RUT o usa un perfil de acceso rápido.",
            fontSize = 13.sp,
            color = TextoGris
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- OPCIÓN 1: INGRESO TRADICIONAL (FORMULARIO CLEAN) ---
        if (!modoModoDemo) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = rutIngresado,
                    onValueChange = { rutIngresado = it },
                    label = { Text("RUT de usuario") },
                    placeholder = { Text("12.345.678-9") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.radioCampo),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primario,
                        unfocusedBorderColor = GrisDeshabilitado
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val userEncontrado = usuarios.find { it.rut.contains(rutIngresado) } ?: usuarios.firstOrNull()
                        userEncontrado?.let { onPerfilSeleccionado(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.alturaBoton),
                    shape = RoundedCornerShape(Dimens.radioBoton),
                    colors = ButtonDefaults.buttonColors(containerColor = Primario)
                ) {
                    Text(text = "Continuar", color = Blanco, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Selector para cambiar a perfiles simulados
                Card(
                    onClick = { modoModoDemo = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.radioTarjeta),
                    colors = CardDefaults.cardColors(containerColor = FondoClaro)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimarioClaro),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👤", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Acceso Rápido / Demo", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                            Text(text = "Seleccionar perfil precargado sin ingresar clave", fontSize = 12.sp, color = TextoGris)
                        }
                        Text(text = "→", fontSize = 18.sp, color = Primario, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        // --- OPCIÓN 2: LISTA DE PERFILES PRECARGADOS ---
        else {
            Column(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(usuarios) { user ->
                        val isSelected = user.id == usuarioSeleccionado?.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.seleccionarUsuario(user) }
                                .border(
                                    width = if (isSelected) Dimens.bordeBoton else 1.dp,
                                    color = if (isSelected) Primario else GrisDeshabilitado,
                                    shape = RoundedCornerShape(Dimens.radioTarjeta)
                                ),
                            shape = RoundedCornerShape(Dimens.radioTarjeta),
                            colors = CardDefaults.cardColors(containerColor = Blanco)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(PrimarioClaro, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = user.avatarIniciales, color = Primario, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = user.nombre, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                    Text(text = "${user.rol.etiqueta} · ${user.rut}", fontSize = 12.sp, color = TextoGris)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                usuarioSeleccionado?.let { user ->
                    Button(
                        onClick = { onPerfilSeleccionado(user) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.alturaBoton),
                        shape = RoundedCornerShape(Dimens.radioBoton),
                        colors = ButtonDefaults.buttonColors(containerColor = Primario)
                    ) {
                        Text(
                            text = "Entrar como ${user.nombre.split(" ").first()}",
                            color = Blanco,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                TextButton(
                    onClick = { modoModoDemo = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Volver a ingreso por RUT", color = TextoGris, fontSize = 12.sp)
                }
            }
        }

        // --- BOTÓN INFERIOR: REGISTRO DE CUENTA NUEVA ---
        OutlinedButton(
            onClick = onCrearCuentaClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(Dimens.alturaBoton),
            shape = RoundedCornerShape(Dimens.radioBoton)
        ) {
            Text(text = "+ Crear una cuenta nueva", color = Primario, fontWeight = FontWeight.Bold)
        }
    }
}