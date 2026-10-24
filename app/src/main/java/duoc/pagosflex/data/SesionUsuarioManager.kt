package duoc.pagosflex.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import duoc.pagosflex.viewmodel.TipoRolUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

data class UsuarioSesion(
    val nombre: String = "",
    val rut: String = "",
    val correo: String = "",
    val rol: TipoRolUsuario = TipoRolUsuario.USUARIO_FINAL,
    val fotoUri: Uri? = null,
    val usuarioLogin: String = "",
    val pin: String = "1111",
    val esSesionActiva: Boolean = false
)

object SesionUsuarioManager {
    private var prefs: SharedPreferences? = null

    fun inicializar(context: Context) {
        prefs = context.applicationContext.getSharedPreferences("pagoflex_sesion_prefs", Context.MODE_PRIVATE)
        cargarSesionGuardada()
    }

    private val _usuarioActual = MutableStateFlow(UsuarioSesion())
    val usuarioActual: StateFlow<UsuarioSesion> = _usuarioActual.asStateFlow()

    private fun cargarSesionGuardada() {
        val p = prefs ?: return
        val activa = p.getBoolean("es_activa", false)
        if (activa) {
            val nombre = p.getString("nombre", "") ?: ""
            val rut = p.getString("rut", "") ?: ""
            val correo = p.getString("correo", "") ?: ""
            val rolName = p.getString("rol", TipoRolUsuario.USUARIO_FINAL.name) ?: TipoRolUsuario.USUARIO_FINAL.name
            val usuarioLogin = p.getString("usuario_login", "") ?: ""
            val pin = p.getString("pin", "1111") ?: "1111"
            val fotoUriStr = p.getString("foto_uri", null)
            val fotoUri = if (!fotoUriStr.isNullOrBlank()) Uri.parse(fotoUriStr) else null
            val rol = try { TipoRolUsuario.valueOf(rolName) } catch (_: Exception) { TipoRolUsuario.USUARIO_FINAL }

            _usuarioActual.value = UsuarioSesion(
                nombre = nombre,
                rut = rut,
                correo = correo,
                rol = rol,
                fotoUri = fotoUri,
                usuarioLogin = usuarioLogin,
                pin = pin,
                esSesionActiva = true
            )
            val esDemo = correo.contains("camila.rojas", ignoreCase = true) || nombre.contains("Camila", ignoreCase = true)
            CompromisoRepositorio.inicializarParaUsuario(esDemo)
        }
    }

    fun iniciarSesion(
        nombre: String,
        rut: String = "",
        correo: String = "",
        rol: TipoRolUsuario = TipoRolUsuario.USUARIO_FINAL,
        fotoUri: Uri? = null,
        usuarioLogin: String = "",
        pin: String = "1111"
    ) {
        val login = if (usuarioLogin.isBlank()) {
            if (correo.contains("@")) correo.substringBefore("@") else nombre
        } else usuarioLogin

        _usuarioActual.value = UsuarioSesion(
            nombre = nombre,
            rut = rut,
            correo = correo,
            rol = rol,
            fotoUri = fotoUri,
            usuarioLogin = login,
            pin = pin,
            esSesionActiva = true
        )

        prefs?.edit()?.apply {
            putBoolean("es_activa", true)
            putString("nombre", nombre)
            putString("rut", rut)
            putString("correo", correo)
            putString("rol", rol.name)
            putString("usuario_login", login)
            putString("pin", pin)
            putString("foto_uri", fotoUri?.toString())
            apply()
        }

        val esDemo = correo.contains("camila.rojas", ignoreCase = true) || nombre.contains("Camila", ignoreCase = true)
        CompromisoRepositorio.inicializarParaUsuario(esDemo)
    }

    fun actualizarFoto(context: Context, uri: Uri?) {
        if (uri == null) {
            _usuarioActual.value = _usuarioActual.value.copy(fotoUri = null)
            prefs?.edit()?.remove("foto_uri")?.apply()
            return
        }

        try {
            val archivo = File(context.filesDir, "avatar_perfil_guardado.jpg")
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                archivo.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            val archivoUri = Uri.fromFile(archivo)
            _usuarioActual.value = _usuarioActual.value.copy(fotoUri = archivoUri)
            prefs?.edit()?.putString("foto_uri", archivoUri.toString())?.apply()
        } catch (_: Exception) {
            _usuarioActual.value = _usuarioActual.value.copy(fotoUri = uri)
            prefs?.edit()?.putString("foto_uri", uri.toString())?.apply()
        }
    }

    fun actualizarUsuarioLogin(nuevoUsuario: String) {
        _usuarioActual.value = _usuarioActual.value.copy(usuarioLogin = nuevoUsuario)
        prefs?.edit()?.putString("usuario_login", nuevoUsuario)?.apply()
    }

    fun cerrarSesion() {
        _usuarioActual.value = UsuarioSesion(esSesionActiva = false)
        prefs?.edit()?.clear()?.apply()
    }
}