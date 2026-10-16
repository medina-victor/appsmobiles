package duoc.pagosflex.model

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val rut: String = "",
    val rol: Rol = Rol.CLIENTE,
    val codigo: String = "",
    val areaOEmpresa: String = "",
    val avatarIniciales: String = ""
)