package com.example.partlog.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partlog.ui.AppLanguage
import com.example.partlog.ui.JobViewModel
import com.example.partlog.ui.Loc
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementsScreen(
    viewModel: JobViewModel,
    onNext: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language
    val context = LocalContext.current
    val themeColor = Color(0xFF0D4B46) // Compressor flow is dark teal

    var highSidePressure by viewModel.highSidePressure
    var lowSidePressure by viewModel.lowSidePressure
    var ambientTemp by viewModel.ambientTemperature
    var coolingTemp by viewModel.coolingTemperature
    
    // Bind Compressor RPM to notes state
    var compressorRpm by remember { 
        mutableStateOf(viewModel.notes.value.replace("RPM: ", "")) 
    }

    val hasAllFields = highSidePressure.isNotBlank() &&
                       lowSidePressure.isNotBlank() &&
                       ambientTemp.isNotBlank() &&
                       coolingTemp.isNotBlank()

    Scaffold(
        topBar = {
            LoggingFlowHeader(
                currentStep = 3,
                totalSteps = 5,
                title = Loc.get("Log Compressor", lang),
                themeColor = themeColor,
                onBack = onCancel
            )
        },
        bottomBar = {
            FlowNextButton(
                text = Loc.get("next", lang),
                enabled = hasAllFields,
                themeColor = themeColor,
                onClick = onNext
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = Loc.get("Measurements", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    // 1. High Side Pressure
                    LoggingTextField(
                        label = Loc.get("High Side Pressure (PSI) *", lang),
                        value = highSidePressure,
                        onValueChange = { highSidePressure = it },
                        placeholder = "e.g. 280 PSI",
                        leadingIcon = Icons.Default.Speed,
                        themeColor = themeColor,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // 2. Low Side Pressure
                    LoggingTextField(
                        label = Loc.get("Low Side Pressure (PSI) *", lang),
                        value = lowSidePressure,
                        onValueChange = { lowSidePressure = it },
                        placeholder = "e.g. 35 PSI",
                        leadingIcon = Icons.Default.Speed,
                        themeColor = themeColor,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // 3. Ambient Temp
                    LoggingTextField(
                        label = Loc.get("Ambient Temperature (°C) *", lang),
                        value = ambientTemp,
                        onValueChange = { ambientTemp = it },
                        placeholder = "e.g. 32 °C",
                        leadingIcon = Icons.Default.Thermostat,
                        themeColor = themeColor,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // 4. Cooling Temp
                    LoggingTextField(
                        label = Loc.get("Cooling Temperature (°C) *", lang),
                        value = coolingTemp,
                        onValueChange = { coolingTemp = it },
                        placeholder = "e.g. 8 °C",
                        leadingIcon = Icons.Default.AcUnit,
                        themeColor = themeColor,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // 5. Compressor RPM (Optional)
                    LoggingTextField(
                        label = Loc.get("Compressor RPM (Optional)", lang),
                        value = compressorRpm,
                        onValueChange = { 
                            compressorRpm = it
                            viewModel.notes.value = if (it.isNotBlank()) "RPM: $it" else ""
                        },
                        placeholder = "e.g. 1200 RPM",
                        leadingIcon = Icons.Default.Speed,
                        themeColor = themeColor,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }
    }
}
