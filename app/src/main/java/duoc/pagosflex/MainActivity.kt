package duoc.pagosflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import duoc.pagosflex.navigation.AppNavegacion
import duoc.pagosflex.ui.theme.PagosFlexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PagosFlexTheme {
                AppNavegacion()
            }
        }
    }
}