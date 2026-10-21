package duoc.pagosflex.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EsquemaColoresOscuro = darkColorScheme(
    primary = RojoPrincipalVarianteTemaOscuro,
    onPrimary = SuperficieOscura,
    primaryContainer = RojoContenedorOscuro,
    onPrimaryContainer = SobreRojoContenedorOscuro,
    secondary = TextoSecundarioOscuro,
    background = FondoOscuro,
    surface = SuperficieOscura,
    surfaceVariant = SuperficieVarianteOscura,
    onBackground = TextoPrincipalOscuro,
    onSurface = TextoPrincipalOscuro,
    onSurfaceVariant = TextoSecundarioOscuro,
    outline = BordeOscuro,
    error = EstadoError
)

private val EsquemaColoresClaro = lightColorScheme(
    primary = RojoPrincipal,
    onPrimary = SuperficieClara,
    primaryContainer = RojoContenedorClaro,
    onPrimaryContainer = SobreRojoContenedorClaro,
    secondary = TextoSecundarioClaro,
    background = FondoClaro,
    surface = SuperficieClara,
    surfaceVariant = SuperficieVarianteClara,
    onBackground = TextoPrincipalClaro,
    onSurface = TextoPrincipalClaro,
    onSurfaceVariant = TextoSecundarioClaro,
    outline = BordeClaro,
    error = EstadoError
)

@Composable
fun TemaPagoFlex(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    contenido: @Composable () -> Unit
) {
    val esquemaColores = if (temaOscuro) EsquemaColoresOscuro else EsquemaColoresClaro

    val vista = LocalView.current
    if (!vista.isInEditMode) {
        SideEffect {
            val ventana = (vista.context as Activity).window
            ventana.statusBarColor = esquemaColores.background.toArgb()
            WindowCompat.getInsetsController(ventana, vista).isAppearanceLightStatusBars = !temaOscuro
        }
    }

    MaterialTheme(
        colorScheme = esquemaColores,
        typography = Tipografia,
        content = contenido
    )
}