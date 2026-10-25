package duoc.pagosflex.util

import android.content.Context
import duoc.pagosflex.data.Compromiso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object VerificadorVencimientos {
    fun verificarYNotificar(context: Context, compromisos: List<Compromiso>) {
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL"))
        val hoy = Calendar.getInstance().time

        for (comp in compromisos) {
            try {
                val fechaVenc = formatoFecha.parse(comp.fechaVencimiento) ?: continue
                val diffMillis = fechaVenc.time - hoy.time
                val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

                if (diffDays == 3) {
                    NotificacionHelper.mostrarNotificacionRecordatorio(
                        context,
                        "Recordatorio: Pago vence en 3 días",
                        "Tu cuota de ${comp.institucion} (${comp.concepto}) vence el ${comp.fechaVencimiento}."
                    )
                } else if (diffDays == 1) {
                    NotificacionHelper.mostrarNotificacionRecordatorio(
                        context,
                        "¡Urgente! Pago vence mañana",
                        "Tu cuota de ${comp.institucion} (${comp.concepto}) vence mañana ${comp.fechaVencimiento}."
                    )
                } else if (diffDays == 0) {
                    NotificacionHelper.mostrarNotificacionRecordatorio(
                        context,
                        "¡Pago vence hoy!",
                        "Recuerda pagar tu cuota de ${comp.institucion} hoy."
                    )
                }
            } catch (_: Exception) {
            }
        }
    }
}