package duoc.pagosflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import duoc.pagosflex.ui.theme.*

@Composable
fun PantallaBienvenida(
    onIniciarSesionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blanco)
            .statusBarsPadding()
            .padding(Dimens.espacioPantalla),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(PrimarioClaro)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "PAGOFLEX APP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primario
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Gestiona tus\npagos sin\ncomplicaciones.",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = TextoOscuro,
                lineHeight = 42.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Controla tus compromisos financieros y realiza el seguimiento de tus solicitudes desde un solo lugar.",
                fontSize = 14.sp,
                color = TextoGris,
                lineHeight = 20.sp
            )
        }

        Card(
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
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Primario),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💳", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Rápido y seguro",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Text(
                        text = "Accede a tus cuentas y perfiles en un toque.",
                        fontSize = 12.sp,
                        color = TextoGris
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Button(
                onClick = onIniciarSesionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.alturaBoton),
                shape = RoundedCornerShape(Dimens.radioBoton),
                colors = ButtonDefaults.buttonColors(containerColor = Primario)
            ) {
                Text(
                    text = "Iniciar sesión",
                    color = Blanco,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Plataforma oficial de gestión PagoFlex",
                fontSize = 11.sp,
                color = TextoGris,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}