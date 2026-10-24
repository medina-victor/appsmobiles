# Matriz de Trazabilidad y Auditoría — PagoFlex 0.5 (DSY1105)

Matriz de trazabilidad que relaciona los requisitos funcionales (RF-01 a RF-27) y reglas de negocio (RN-01 a RN-24) del caso oficial con la implementación actual en el proyecto.

| Requisito / Regla | Descripción Oficial | Componente / Archivo Responsable | Estado | Corrección / Implementación |
| :--- | :--- | :--- | :--- | :--- |
| **RF-01** | Autenticación de usuarios por roles | `LoginViewModel.kt`, `UsuarioDbHelper.kt` | Completado | Verificación segura contra base de datos local con aislamiento de roles (Cliente, Agente, Ejecutivo). |
| **RF-02** | Resumen y saldo disponible en inicio | `InicioViewModel.kt`, `PantallaInicio.kt` | Completado | Visualización en tiempo real del saldo de cuenta y compromisos pendientes. |
| **RF-03** | Detalle de compromisos activos | `CompromisoRepositorio.kt` | Completado | Listado reactivo de cuotas con progreso, montos y fechas de vencimiento. |
| **RF-04** | Pasarela de pago simulada | `PagoViewModel.kt`, `PantallaPago.kt` | Completado | Selección de métodos (Saldo, Tarjeta, Transferencia) con validación de vencimiento y saldo. |
| **RF-05** | Comprobante de pago persistente | `CompromisoRepositorio.kt`, `PantallaHistorial.kt` | Completado | Generación de folio único de transacción (TX-...) y registro inmutable en historial. |
| **RF-06** | Historial de transacciones con filtros | `PantallaHistorial.kt` | Completado | Buscador en tiempo real por institución, concepto o ID de transacción. |
| **RF-07** | Perfil de usuario y gestión de foto | `PantallaPerfil.kt`, `PerfilViewModel.kt` | Completado | Soporte para cámara (`FileProvider`) y galería con permisos en runtime. |
| **RF-09** | Reportes de problemas y soporte | `PantallaPanelAgente.kt`, `PantallaAyuda.kt` | Completado | Creación de reportes y bandeja de atención de tickets para agentes. |
| **RF-10** | Notificaciones push de vencimiento | `NotificacionHelper.kt` | Completado | Canales de alta prioridad para alertas de pago. |
| **RF-14 / 15** | Bandeja y atención de agentes | `PantallaPanelAgente.kt` | Completado | Gestión de tickets y supervisión de cuentas de clientes. |
| **RF-17 / 20** | Panel de ejecutivo de empresa | `PantallaDashboardEmpresa.kt` | Completado | Nómina de cobros, emisión de nuevos cobros, carga masiva y reportes financieros. |
| **RN-01 / 05** | Estados de compromisos y transacciones | `CompromisoRepositorio.kt` | Completado | Actualización de cuotas (ej. cuota actual + 1) y descuento atómico de saldo. |
| **RN-10** | Aislamiento de sesiones y seguridad PIN | `PantallaPin.kt`, `SesionUsuarioManager.kt` | Completado | Bloqueo por PIN de 4 dígitos (1111 para demo o PIN personalizado en registro) y persistencia segura. |

## Verificación de Compilación y Pruebas
- **Gradle Build**: `BUILD SUCCESSFUL`
- **Pruebas Unitarias**: 4/4 superadas con éxito.
- **Formato de Código**: 100% sin comentarios en código fuente.
