package duoc.pagosflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import duoc.pagosflex.ui.components.BotonPrimario
import duoc.pagosflex.ui.components.BotonVolver
import duoc.pagosflex.viewmodel.AgregarCompromisoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAgregarCompromiso(
    alVolverAtras: () -> Unit,
    viewModel: AgregarCompromisoViewModel = viewModel()
) {
    val institucion by viewModel.institucion.collectAsState()
    val concepto by viewModel.concepto.collectAsState()
    val monto by viewModel.monto.collectAsState()
    val fechaVencimiento by viewModel.fechaVencimiento.collectAsState()
    val cuotasTotal by viewModel.cuotasTotal.collectAsState()
    val errorFormulario by viewModel.errorFormulario.collectAsState()

    Scaffold(
        topBar = {
            BotonVolver(onVolver = alVolverAtras, titulo = "Registrar Nueva Cuenta")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { valoresPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = institucion,
                onValueChange = { viewModel.institucion.value = it },
                label = { Text("Institución / Empresa (ej. PagoFlex Servicios)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = concepto,
                onValueChange = { viewModel.concepto.value = it },
                label = { Text("Concepto (ej. Consumo Luz / Arancel)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = monto,
                onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.monto.value = it },
                label = { Text("Monto ($ CLP)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = fechaVencimiento,
                onValueChange = { viewModel.fechaVencimiento.value = it },
                label = { Text("Fecha de Vencimiento (ej. 15/11/2026)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = cuotasTotal,
                onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.cuotasTotal.value = it },
                label = { Text("Total de Cuotas") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (errorFormulario != null) {
                Text(
                    text = errorFormulario!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            BotonPrimario(
                texto = "Registrar Cuenta",
                onClick = {
                    viewModel.guardarCompromiso(alExito = alVolverAtras)
                },
                habilitado = institucion.isNotBlank() && concepto.isNotBlank() && monto.isNotBlank() && fechaVencimiento.isNotBlank()
            )
        }
    }
}