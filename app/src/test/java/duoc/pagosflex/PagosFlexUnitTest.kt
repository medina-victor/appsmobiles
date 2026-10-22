package duoc.pagosflex

import duoc.pagosflex.data.CompromisoRepositorio
import duoc.pagosflex.data.SesionUsuarioManager
import duoc.pagosflex.viewmodel.TipoRolUsuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PagosFlexUnitTest {

    @Test
    fun testSesionUsuarioManager() {
        assertFalse(SesionUsuarioManager.usuarioActual.value.esSesionActiva)

        SesionUsuarioManager.iniciarSesion(
            nombre = "Camila Test",
            rut = "15.123.456-3",
            correo = "camila@pagoflex.cl",
            rol = TipoRolUsuario.USUARIO_FINAL
        )

        assertTrue(SesionUsuarioManager.usuarioActual.value.esSesionActiva)
        assertEquals("Camila Test", SesionUsuarioManager.usuarioActual.value.nombre)
        assertEquals(TipoRolUsuario.USUARIO_FINAL, SesionUsuarioManager.usuarioActual.value.rol)

        SesionUsuarioManager.cerrarSesion()
        assertFalse(SesionUsuarioManager.usuarioActual.value.esSesionActiva)
    }

    @Test
    fun testCompromisoRepositorioYPagos() {
        val compromisosIniciales = CompromisoRepositorio.compromisos.value
        assertTrue(compromisosIniciales.isNotEmpty())

        val primerCompromiso = compromisosIniciales.first()
        val id = primerCompromiso.id
        val monto = primerCompromiso.monto

        val txId = "TX-TEST-001"
        val exito = CompromisoRepositorio.registrarPagoExitoso(id, txId, monto)

        assertTrue(exito)

        val historial = CompromisoRepositorio.historial.value
        assertTrue(historial.isNotEmpty())
        assertEquals(txId, historial.first().idTransaccion)
        assertEquals(monto, historial.first().monto, 0.01)
    }

    @Test
    fun testValidacionContrasenaRequisitos() {
        val passValida = "Password123!"
        assertTrue(passValida.any { it.isUpperCase() })
        assertTrue(passValida.any { it.isLowerCase() })
        assertTrue(passValida.any { it.isDigit() })
        assertTrue(passValida.any { !it.isLetterOrDigit() })

        val passInvalida = "weak"
        assertFalse(passInvalida.length >= 8)
    }
}