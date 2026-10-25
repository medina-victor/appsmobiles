package duoc.pagosflex.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CobroEmpresaDetalle(
    val id: String,
    val deudor: String,
    val rut: String,
    val correo: String,
    val telefono: String,
    val concepto: String,
    val monto: Double,
    val fechaVencimiento: String,
    val cuotaActual: Int,
    val totalCuotas: Int,
    var estado: String,
    var motivoAnulacion: String = "",
    var anuladoPor: String = "",
    var fechaAnulacion: String = ""
)

object EmpresaRepositorio {
    private val _cobrosEmpresa = MutableStateFlow(
        mutableListOf(
            CobroEmpresaDetalle("CB-901", "Juan Pérez Cotapos", "18.223.114-K", "juan.perez@duocuc.cl", "+56912345678", "Arancel Cuota 5", 145000.0, "05/11/2026", 5, 10, "Pagado"),
            CobroEmpresaDetalle("CB-902", "María González Silva", "15.882.301-4", "maria.gonzalez@gmail.com", "+56987654321", "Consumo Servicios", 24500.0, "12/10/2026", 1, 1, "Vencido"),
            CobroEmpresaDetalle("CB-903", "Carlos Rojas Vera", "19.102.883-2", "carlos.rojas@yahoo.com", "+56911223344", "Tag Mensual", 18900.0, "20/11/2026", 1, 1, "Pendiente"),
            CobroEmpresaDetalle("CB-904", "Ana Torres Soto", "16.554.332-1", "ana.torres@empresa.cl", "+56955443322", "Dividendo Hipotecario", 385000.0, "15/09/2026", 12, 240, "En revisión"),
            CobroEmpresaDetalle("CB-905", "Pedro Morales Díaz", "14.112.998-7", "pedro.morales@outlook.com", "+56999887766", "Seguro Automotriz", 65000.0, "01/11/2026", 3, 12, "Pagado")
        )
    )
    val cobrosEmpresa: StateFlow<List<CobroEmpresaDetalle>> = _cobrosEmpresa.asStateFlow()

    fun registrarNuevoCompromiso(
        deudor: String,
        rut: String,
        correo: String,
        telefono: String,
        concepto: String,
        monto: Double,
        fechaVencimiento: String,
        cuotaActual: Int,
        totalCuotas: Int
    ): Boolean {
        val existe = _cobrosEmpresa.value.any { it.rut == rut && it.concepto == concepto && it.fechaVencimiento == fechaVencimiento }
        if (existe) return false

        val nuevoId = "CB-" + (906..999).random()
        val nuevoCobro = CobroEmpresaDetalle(
            id = nuevoId,
            deudor = deudor,
            rut = rut,
            correo = correo,
            telefono = telefono,
            concepto = concepto,
            monto = monto,
            fechaVencimiento = fechaVencimiento,
            cuotaActual = cuotaActual,
            totalCuotas = totalCuotas,
            estado = "Pendiente"
        )
        _cobrosEmpresa.value.add(0, nuevoCobro)
        return true
    }

    fun anularCompromiso(id: String, motivo: String, usuarioAnula: String): Boolean {
        val lista = _cobrosEmpresa.value.map { cobro ->
            if (cobro.id == id) {
                if (cobro.estado == "Pagado") {
                    cobro
                } else {
                    val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date())
                    cobro.copy(
                        estado = "Anulado",
                        motivoAnulacion = motivo,
                        anuladoPor = usuarioAnula,
                        fechaAnulacion = fechaActual
                    )
                }
            } else {
                cobro
            }
        }
        _cobrosEmpresa.value = lista.toMutableList()
        return true
    }
}