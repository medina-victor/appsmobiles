# Tasks — PagosFlex 0.4 Audit & Improvements

- [x] Task 1: Audit and enhance data layer (`UsuarioDbHelper`, `SesionUsuarioManager`) with proper non-destructive upgrade and credential management
- [x] Task 2: Fix `LoginViewModel` and `PantallaLogin` with correct credential verification, role assignment, loading/error states, and password eye icon
- [x] Task 3: Fix `RegistroViewModel` and `PantallaRegistro` with robust validation and SQLite persistence (`UsuarioDbHelper`)
- [x] Task 4: Fix `PagoViewModel` and `PantallaPago` end-to-end payment flow with commitment details, processing lock, success/error states, and academic simulation disclaimer
- [x] Task 5: Verify theme consistency (Light/Dark mode, Material 3 color schemes, PagoFlex identity)
- [x] Task 6: Check profile, camera (`FileProvider`), biometrics, and notification preferences in `PantallaPerfil` and `AndroidManifest.xml`
- [x] Task 7: Review navigation guardrails, role-based routing, and session closure
- [x] Task 8: Add unit tests for authentication, validation, payment processing, and session management
- [x] Task 9: Execute Gradle `assembleDebug` and unit tests to verify build success
