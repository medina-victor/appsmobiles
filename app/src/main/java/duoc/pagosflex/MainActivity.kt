package duoc.pagosflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import duoc.pagosflex.data.CompromisoRepositorio
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.navigation.AppNavegacion
import duoc.pagosflex.ui.theme.TemaPagoFlex

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SesionUsuarioManager.inicializar(applicationContext)
        CompromisoRepositorio.inicializar(applicationContext)
        setContent {
            TemaPagoFlex() {
                AppNavegacion()
            }
        }
    }
}