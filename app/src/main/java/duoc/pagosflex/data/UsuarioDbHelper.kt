package duoc.pagosflex.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import duoc.pagosflex.model.Rol
import duoc.pagosflex.model.Usuario

class UsuarioDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pagoflex.db"
        private const val DATABASE_VERSION = 5
        private const val TABLE_USUARIOS = "usuarios"

        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_RUT = "rut"
        private const val COL_CORREO = "correo"
        private const val COL_CONTRASENA = "contrasena"
        private const val COL_PIN = "pin"
        private const val COL_LOGIN = "login"
        private const val COL_FOTO = "foto"
        private const val COL_ROL = "rol"
        private const val COL_CODIGO = "codigo"
        private const val COL_DETALLE = "detalle"
        private const val COL_AVATAR = "avatar"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID TEXT PRIMARY KEY,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_RUT TEXT NOT NULL,
                $COL_CORREO TEXT,
                $COL_CONTRASENA TEXT NOT NULL,
                $COL_PIN TEXT NOT NULL DEFAULT '1111',
                $COL_LOGIN TEXT,
                $COL_FOTO TEXT,
                $COL_ROL TEXT NOT NULL,
                $COL_CODIGO TEXT NOT NULL,
                $COL_DETALLE TEXT,
                $COL_AVATAR TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
        precargarUsuariosDemo(db)
    }

    private fun precargarUsuariosDemo(db: SQLiteDatabase) {
        val usuarios = listOf(
            ContentValues().apply {
                put(COL_ID, "usr_01")
                put(COL_NOMBRE, "Camila Rojas")
                put(COL_RUT, "15.123.456-3")
                put(COL_CORREO, "camila.rojas@duocuc.cl")
                put(COL_CONTRASENA, "Camila2024*")
                put(COL_PIN, "1111")
                put(COL_LOGIN, "camila")
                put(COL_ROL, Rol.CLIENTE.name)
                put(COL_CODIGO, "UF-01")
                put(COL_DETALLE, "Cliente")
                put(COL_AVATAR, "CR")
            },
            ContentValues().apply {
                put(COL_ID, "usr_02")
                put(COL_NOMBRE, "Andrés Lagos")
                put(COL_RUT, "18.987.654-K")
                put(COL_CORREO, "andres.lagos@pagoflex.cl")
                put(COL_CONTRASENA, "Andres2024*")
                put(COL_PIN, "1111")
                put(COL_LOGIN, "andres")
                put(COL_ROL, Rol.AGENTE.name)
                put(COL_CODIGO, "AG-01")
                put(COL_DETALLE, "Soporte Agente")
                put(COL_AVATAR, "AL")
            },
            ContentValues().apply {
                put(COL_ID, "usr_03")
                put(COL_NOMBRE, "Paula Saavedra")
                put(COL_RUT, "76.543.210-9")
                put(COL_CORREO, "paula.saavedra@empresa.cl")
                put(COL_CONTRASENA, "Paula2024*")
                put(COL_PIN, "1111")
                put(COL_LOGIN, "paula")
                put(COL_ROL, Rol.EJECUTIVO.name)
                put(COL_CODIGO, "EJ-01")
                put(COL_DETALLE, "Altos del Maipo")
                put(COL_AVATAR, "PS")
            }
        )

        for (u in usuarios) {
            db.insertWithOnConflict(TABLE_USUARIOS, null, u, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 5) {
            try {
                db.execSQL("ALTER TABLE $TABLE_USUARIOS ADD COLUMN $COL_FOTO TEXT")
            } catch (_: Exception) {
            }
            precargarUsuariosDemo(db)
        }
    }

    fun existeRutOCorreo(rut: String, correo: String): Boolean {
        val db = readableDatabase
        val query = "SELECT 1 FROM $TABLE_USUARIOS WHERE $COL_RUT = ? OR $COL_CORREO = ?"
        val cursor = db.rawQuery(query, arrayOf(rut, correo))
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    fun existeAlias(alias: String): Boolean {
        val db = readableDatabase
        val query = "SELECT 1 FROM $TABLE_USUARIOS WHERE $COL_LOGIN = ?"
        val cursor = db.rawQuery(query, arrayOf(alias.lowercase()))
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    fun obtenerPinUsuario(identificador: String): String {
        val db = readableDatabase
        val query = "SELECT $COL_PIN FROM $TABLE_USUARIOS WHERE $COL_CORREO = ? OR $COL_RUT = ? OR $COL_NOMBRE = ? OR $COL_LOGIN = ?"
        val cursor = db.rawQuery(query, arrayOf(identificador, identificador, identificador, identificador))
        var pin = "1111"
        if (cursor.moveToFirst()) {
            pin = cursor.getString(cursor.getColumnIndexOrThrow(COL_PIN)) ?: "1111"
        }
        cursor.close()
        return pin
    }

    fun actualizarFotoUsuario(identificador: String, fotoUriStr: String?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_FOTO, fotoUriStr)
        }
        db.update(TABLE_USUARIOS, values, "$COL_CORREO = ? OR $COL_RUT = ? OR $COL_LOGIN = ?", arrayOf(identificador, identificador, identificador))
    }

    fun obtenerFotoUsuario(identificador: String): String? {
        val db = readableDatabase
        val query = "SELECT $COL_FOTO FROM $TABLE_USUARIOS WHERE $COL_CORREO = ? OR $COL_RUT = ? OR $COL_LOGIN = ?"
        val cursor = db.rawQuery(query, arrayOf(identificador, identificador, identificador))
        var foto: String? = null
        if (cursor.moveToFirst()) {
            foto = cursor.getString(cursor.getColumnIndexOrThrow(COL_FOTO))
        }
        cursor.close()
        return foto
    }

    fun actualizarCorreoUsuario(identificadorAntiguo: String, nuevoCorreo: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_CORREO, nuevoCorreo)
            put(COL_LOGIN, nuevoCorreo.substringBefore("@").lowercase())
        }
        db.update(TABLE_USUARIOS, values, "$COL_CORREO = ? OR $COL_RUT = ? OR $COL_LOGIN = ?", arrayOf(identificadorAntiguo, identificadorAntiguo, identificadorAntiguo))
    }

    fun actualizarAliasUsuario(identificador: String, nuevoAlias: String): Boolean {
        if (existeAlias(nuevoAlias)) return false
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_LOGIN, nuevoAlias.lowercase())
        }
        val filas = db.update(TABLE_USUARIOS, values, "$COL_CORREO = ? OR $COL_RUT = ? OR $COL_ID = ?", arrayOf(identificador, identificador, identificador))
        return filas > 0
    }

    fun verificarCredenciales(identificador: String, contrasena: String): Usuario? {
        val db = readableDatabase
        val query = """
            SELECT * FROM $TABLE_USUARIOS 
            WHERE ($COL_RUT = ? OR $COL_CORREO = ? OR $COL_NOMBRE = ? OR $COL_ID = ? OR $COL_LOGIN = ?) 
            AND $COL_CONTRASENA = ?
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(identificador, identificador, identificador, identificador, identificador, contrasena))
        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)),
                rut = cursor.getString(cursor.getColumnIndexOrThrow(COL_RUT)),
                rol = try {
                    Rol.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_ROL)))
                } catch (_: Exception) {
                    Rol.CLIENTE
                },
                codigo = cursor.getString(cursor.getColumnIndexOrThrow(COL_CODIGO)) ?: "",
                areaOEmpresa = cursor.getString(cursor.getColumnIndexOrThrow(COL_DETALLE)) ?: "",
                avatarIniciales = cursor.getString(cursor.getColumnIndexOrThrow(COL_AVATAR)) ?: ""
            )
        }
        cursor.close()
        return usuario
    }

    fun registrarUsuario(
        nombre: String,
        rut: String,
        correo: String,
        contrasena: String,
        pin: String,
        rol: Rol,
        codigo: String,
        detalle: String,
        avatar: String
    ): Boolean {
        val db = writableDatabase
        val loginAlias = correo.substringBefore("@").lowercase()
        if (existeAlias(loginAlias)) return false

        val values = ContentValues().apply {
            put(COL_ID, "usr_" + System.currentTimeMillis())
            put(COL_NOMBRE, nombre)
            put(COL_RUT, rut)
            put(COL_CORREO, correo)
            put(COL_CONTRASENA, contrasena)
            put(COL_PIN, pin.ifBlank { "1234" })
            put(COL_LOGIN, loginAlias)
            put(COL_ROL, rol.name)
            put(COL_CODIGO, codigo.ifBlank { "UF-0${System.currentTimeMillis() % 100}" })
            put(COL_DETALLE, detalle.ifBlank { rol.etiqueta })
            put(COL_AVATAR, avatar.ifBlank { nombre.take(2).uppercase() })
        }
        val resultado = db.insertWithOnConflict(TABLE_USUARIOS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        return resultado != -1L
    }
}