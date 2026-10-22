package duoc.pagosflex.viewmodel

enum class TipoRolUsuario(val titulo: String, val descripcion: String) {
    USUARIO_FINAL("Cliente / Deudor", "Consulta y paga tus compromisos pendientes"),
    EJECUTIVO_EMPRESA("Empresa / Institución", "Carga convenios y gestiona recaudaciones"),
    AGENTE_PAGOFLEX("Ejecutivo FinTech", "Gestiona convenios, monitorea operaciones y clientes")
}