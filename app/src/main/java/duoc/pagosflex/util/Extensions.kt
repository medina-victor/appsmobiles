package duoc.pagosflex.util



import java.text.NumberFormat
import java.util.Locale

fun Double.aMonedaCLP(): String {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    return formato.format(this)
}