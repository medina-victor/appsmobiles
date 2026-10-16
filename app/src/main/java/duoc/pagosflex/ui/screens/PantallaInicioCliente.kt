package duoc.pagosflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.pagosflex.model.Usuario
import duoc.pagosflex.ui.components.BarraNavegacionInferior
import duoc.pagosflex.ui.components.BotonVolver
import duoc.pagosflex.ui.theme.*
import duoc.pagosflex.viewmodel.InicioClienteViewModel
import duoc.pagosflex.viewmodel.ProductoCuenta
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PantallaInicioCliente(
    usuario: Usuario,
    onVolver: () -> Unit,
    onIrAPagarCuenta: () -> Unit,
    onIrAServicios: () -> Unit,
    onIrADividendo: () -> Unit,
    viewModel: InicioClienteViewModel = viewModel()
) {
    LaunchedEffect(usuario) {
        viewModel.cargarDatosCliente(usuario)
    }

    val state by viewModel.uiState.collectAsState()
    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    Scaffold(
        bottomBar = {
            BarraNavegacionInferior(
                itemSeleccionado = state.tabSeleccionado,
                onTabSeleccionado = { viewModel.onTabCambiar(it) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Blanco)
                .padding(paddingValues)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // --- BARRA SUPERIOR CON BOTÓN VOLVER / SALIR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.espacioPantalla),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BotonVolver(
                    onVolver = onVolver,
                    modifier = Modifier.weight(1f)
                )

                TextButton(onClick = onVolver) {
                    Text(
                        text = "Cerrar sesión",
                        color = Primario,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- 1. CABECERA SALUDO ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.espacioPantalla, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hola, ${usuario.nombre.split(" ").first()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Text(
                        text = "RUT: ${usuario.rut}",
                        fontSize = 12.sp,
                        color = TextoGris
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PrimarioClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = usuario.avatarIniciales,
                        fontWeight = FontWeight.Bold,
                        color = Primario,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- 2. TARJETAS DE PRODUCTOS ---
            Text(
                text = "Mis Productos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro,
                modifier = Modifier.padding(horizontal = Dimens.espacioPantalla)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = Dimens.espacioPantalla),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(state.productos) { prod ->
                    TarjetaProductoItem(prod = prod, formato = formatoMoneda)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. ACCIONES RÁPIDAS ---
            Text(
                text = "Acciones Rápidas",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro,
                modifier = Modifier.padding(horizontal = Dimens.espacioPantalla)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.espacioPantalla),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BotonAccionRapida(
                    icono = "📄",
                    etiqueta = "Cuentas",
                    onClick = onIrAPagarCuenta,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BotonAccionRapida(
                    icono = "💡",
                    etiqueta = "Servicios",
                    onClick = onIrAServicios,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BotonAccionRapida(
                    icono = "🏠",
                    etiqueta = "Dividendo",
                    onClick = onIrADividendo,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 4. COMPROMISOS PENDIENTES ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.espacioPantalla),
                shape = RoundedCornerShape(Dimens.radioTarjeta),
                colors = CardDefaults.cardColors(containerColor = FondoClaro)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Próximo Vencimiento",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(PrimarioClaro)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "Pendiente", fontSize = 10.sp, color = Primario, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Gastos Comunes - Edificio Central",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextoOscuro
                    )
                    Text(
                        text = "Vence el 28 de Octubre",
                        fontSize = 11.sp,
                        color = TextoGris
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatoMoneda.format(85000),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primario
                        )

                        Button(
                            onClick = onIrAPagarCuenta,
                            colors = ButtonDefaults.buttonColors(containerColor = Primario),
                            shape = RoundedCornerShape(Dimens.radioBoton),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(text = "Pagar", color = Blanco, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TarjetaProductoItem(prod: ProductoCuenta, formato: NumberFormat) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .shadow(4.dp, RoundedCornerShape(Dimens.radioTarjeta)),
        shape = RoundedCornerShape(Dimens.radioTarjeta),
        colors = CardDefaults.cardColors(containerColor = Primario)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = prod.tipo, color = Blanco.copy(alpha = 0.9f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text(text = prod.icono, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "N° ${prod.numeroCuenta}",
                color = Blanco.copy(alpha = 0.75f),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Saldo disponible",
                color = Blanco.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
            Text(
                text = formato.format(prod.saldoDisponible),
                color = Blanco,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BotonAccionRapida(
    icono: String,
    etiqueta: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(Dimens.radioTarjeta),
        colors = CardDefaults.cardColors(containerColor = FondoClaro)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icono, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = etiqueta, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
        }
    }
}