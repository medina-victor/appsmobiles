package duoc.pagosflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import duoc.pagosflex.ui.components.CampoDeTexto
import duoc.pagosflex.ui.components.HeaderCurved
import duoc.pagosflex.ui.components.PrimaryButton
import duoc.pagosflex.ui.theme.PagosFlexTheme

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val primaryRed = Color(0xFFD32F2F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HeaderCurved(title = "PAGOSFLEX")

        Spacer(modifier = Modifier.height(36.dp))


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CampoDeTexto(
                label = "USUARIO / RUT",
                value = username,
                onValueChange = { username = it },
                icon = Icons.Default.Person,
                iconDescription = "Ícono usuario"
            )

            Spacer(modifier = Modifier.height(20.dp))


            CampoDeTexto(
                label = "CONTRASEÑA",
                value = password,
                onValueChange = { password = it },
                icon = Icons.Default.Lock,
                iconDescription = "Ícono contraseña",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(28.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                PrimaryButton(
                    text = "Iniciar Sesion",
                    onClick = { },
                    modifier = Modifier.width(1500.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onRegisterClick() }
                    .padding(8.dp)
            ) {
                Text(
                    text = "CREAR NUEVO USUARIO",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Crear usuario",
                    tint = primaryRed,
                    modifier = Modifier.size(18.dp)
                )
            }


            Text(
                text = "¿OLVIDASTE TU CONTRASEÑA?",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onForgotPasswordClick() }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))




            Spacer(modifier = Modifier.height(28.dp))
        }

    }


}
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Login Screen Preview"
)
@Composable
fun LoginScreenPreview() {
    PagosFlexTheme {
        LoginScreen()
    }
}