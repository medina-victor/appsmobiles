# Implementation Plan — PagosFlex 0.4 Audit and Functional Improvements

Comprehensive audit, bug fixes, and feature completion for the academic Android app **PagosFlex 0.4** (DSY1105 Duoc UC), covering 10 phases from secure authentication and registration persistence to robust payment flows, theme consistency, and unit testing.

## User Review Required

> [!IMPORTANT]
> - **Authentication & Demo Accounts**: Login will now authenticate against seeded demo users in SQLite (`UsuarioDbHelper`) with proper role assignment (`CLIENTE`, `AGENTE`, `EJECUTIVO`), rejecting invalid credentials instead of letting any non-empty input pass.
> - **Payment Flow**: `PantallaPago` will be fully integrated with `PagoViewModel` and commitment details retrieved by `idCompromiso`, ensuring commitment state updates and history recording happen *only* on successful simulated payment.
> - **Database Upgrade Policy**: `UsuarioDbHelper.onUpgrade()` will be updated to be non-destructive or safely handle schema updates without losing registered user data.

## Open Questions

- None. All requirements strictly align with the academic specifications and existing project architecture.

## Proposed Changes

### Data & Authentication
#### [MODIFY] [UsuarioDbHelper.kt](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/java/duoc/pagosflex/data/UsuarioDbHelper.kt)
- Add password column/field support or secure demo credential verification.
- Fix `onUpgrade` to avoid unconditional table dropping if users are registered.
- Add methods to find user by login/rut/correo and verify credentials.

#### [MODIFY] [SesionUsuarioManager.kt](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/java/duoc/pagosflex/data/SesionUsuarioManager.kt)
- Support secure session management and role persistence across navigation.

### ViewModels
#### [MODIFY] [LoginViewModel.kt](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/java/duoc/pagosflex/viewmodel/LoginViewModel.kt)
- Validate against actual demo accounts / SQLite database.
- Assign correct `TipoRolUsuario` based on user profile.
- Handle loading, success, and error states properly.

#### [MODIFY] [RegistroViewModel.kt](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/java/duoc/pagosflex/viewmodel/RegistroViewModel.kt)
- Ensure validation rules for name, RUT, email, and password (8+ chars, upper, lower, number, special char, matching confirmation).
- Persist new user via `UsuarioDbHelper`.

#### [MODIFY] [PagoViewModel.kt](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/java/duoc/pagosflex/viewmodel/PagoViewModel.kt)
- Connect with commitment data repository/source.
- Handle processing state, duplicate submission prevention, and simulation result success/error.

#### [MODIFY] [InicioViewModel.kt](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/java/duoc/pagosflex/viewmodel/InicioViewModel.kt)
- Ensure commitments update correctly upon successful payment.

### UI Screens & Components
#### [MODIFY] [PantallaLogin.kt](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/java/duoc/pagosflex/ui/screens/PantallaLogin.kt)
- Add password eye icon for visibility toggling.
- Show loading indicator, proper error messages, and prevent double submission.

#### [MODIFY] [PantallaRegistro.kt](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/java/duoc/pagosflex/ui/screens/PantallaRegistro.kt)
- Dual password fields with eye toggle icons and real-time validation error feedback.

#### [MODIFY] [PantallaPago.kt](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/java/duoc/pagosflex/ui/screens/PantallaPago.kt)
- Integrate `PagoViewModel`. Display correct commitment details (institution, concept, amount, due date).
- Include academic simulation disclaimer.
- Handle success/error/processing states.

#### [MODIFY] [PantallaPerfil.kt](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/java/duoc/pagosflex/ui/screens/PantallaPerfil.kt)
- Camera permissions / FileProvider handling, biometric and notification preference persistence.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/AndroidManifest.xml)
- Verify FileProvider and camera/storage configuration.

### Theme & Styling
#### [MODIFY] [Theme.kt](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/java/duoc/pagosflex/ui/theme/Theme.kt) & [Color.kt](file:///C:/Users/victo/AndroidStudioProjects/pagosFlex/app/src/main/java/duoc/pagosflex/ui/theme/Color.kt)
- Ensure light/dark theme consistency with Material 3 color schemes and PagoFlex red/white/gray identity.

## Verification Plan

### Automated Tests
- Run unit tests for Login authentication, Registro validations, PagoViewModel processing, and Session management.
- Run Gradle `assembleDebug` to verify successful compilation.

### Manual Verification
- Deploy to device/emulator and test login for client, agent, and enterprise executive roles.
- Test registration flow with validation errors and successful SQLite storage.
- Test payment flow with commitment selection, processing state, simulation disclaimer, and history update.
