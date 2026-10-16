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
        private const val DATABASE_VERSION = 1
        private const val TABLE_USUARIOS = "usuarios"

        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_RUT = "rut"
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
                $COL_ROL TEXT NOT NULL,
                $COL_CODIGO TEXT NOT NULL,
                $COL_DETALLE TEXT,
                $COL_AVATAR TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)

        // Precargar los 3 perfiles demo exactos de tu maqueta
        precargarUsuariosDemo(db)
    }

    private fun precargarUsuariosDemo(db: SQLiteDatabase) {
        val usuarios = listOf(
            ContentValues().apply {
                put(COL_ID, "usr_01")
                put(COL_NOMBRE, "Camila Rojas")
                put(COL_RUT, "RUT 15.***.***-3")
                put(COL_ROL, Rol.CLIENTE.name)
                put(COL_CODIGO, "UF-01")
                put(COL_DETALLE, "Cliente")
                put(COL_AVATAR, "CR")
            },
            ContentValues().apply {
                put(COL_ID, "usr_02")
                put(COL_NOMBRE, "Andrés Lagos")
                put(COL_RUT, "AG-01")
                put(COL_ROL, Rol.AGENTE.name)
                put(COL_CODIGO, "AG-01")
                put(COL_DETALLE, "Soporte")
                put(COL_AVATAR, "AL")
            },
            ContentValues().apply {
                put(COL_ID, "usr_03")
                put(COL_NOMBRE, "Paula Saavedra")
                put(COL_RUT, "EJ-01")
                put(COL_ROL, Rol.EJECUTIVO.name)
                put(COL_CODIGO, "EJ-01")
                put(COL_DETALLE, "Altos del Maipo")
                put(COL_AVATAR, "PS")
            }
        )

        for (u in usuarios) {
            db.insert(TABLE_USUARIOS, null, u)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    fun obtenerUsuarios(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIOS", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Usuario(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)),
                        rut = cursor.getString(cursor.getColumnIndexOrThrow(COL_RUT)),
                        rol = Rol.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_ROL))),
                        codigo = cursor.getString(cursor.getColumnIndexOrThrow(COL_CODIGO)),
                        areaOEmpresa = cursor.getString(cursor.getColumnIndexOrThrow(COL_DETALLE)) ?: "",
                        avatarIniciales = cursor.getString(cursor.getColumnIndexOrThrow(COL_AVATAR)) ?: ""
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
    // ingresar un nuevo usuario desde registro
    fun registrarUsuario(
        nombre: String,
        rut: String,
        rol: Rol,
        codigo: String,
        detalle: String,
        avatar: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", "usr_" + System.currentTimeMillis())
            put("nombre", nombre)
            put("rut", rut)
            put("rol", rol.name)
            put("codigo", codigo)
            put("detalle", detalle)
            put("avatar", avatar)
        }
        val resultado = db.insert("usuarios", null, values)
        return resultado != -1L
    }
}