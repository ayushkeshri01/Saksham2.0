package com.example.partlog.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partlog.ui.AppLanguage
import com.example.partlog.ui.JobViewModel
import com.example.partlog.ui.Loc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopDetailsScreen(
    viewModel: JobViewModel,
    onNext: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language
    val context = LocalContext.current
    val isCompressor = viewModel.componentType.value == "compressor"
    val themeColor = if (isCompressor) Color(0xFF0D4B46) else Color(0xFF023F97)

    val mechanicName = viewModel.mechanicName.value.ifBlank { "Abhishek Kumar" }
    val workshopName = viewModel.mechanicWorkshop.value.ifBlank { "Kumar Car AC Service" }
    var workshopCity by viewModel.workshopCity
    
    val lat by viewModel.gpsLatitude
    val lng by viewModel.gpsLongitude
    val gpsCaptured by viewModel.gpsCaptured
    val gpsAcquiring by viewModel.gpsAcquiring

    val gpsString = if (gpsCaptured) {
        "${String.format("%.4f", lat)}° N, ${String.format("%.4f", lng)}° E"
    } else if (gpsAcquiring) {
        "Acquiring GPS Lock..."
    } else {
        "GPS coordinates not locked yet"
    }

    val hasAllData = workshopCity.isNotBlank() && gpsCaptured

    Scaffold(
        topBar = {
            LoggingFlowHeader(
                currentStep = 4,
                totalSteps = 5,
                title = if (isCompressor) Loc.get("Log Compressor", lang) else Loc.get("Log Condenser", lang),
                themeColor = themeColor,
                onBack = onCancel
            )
        },
        bottomBar = {
            FlowNextButton(
                text = Loc.get("next", lang),
                enabled = hasAllData,
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
                        text = Loc.get("Workshop Details", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    LoggingTextFieldReadOnly(
                        label = Loc.get("Mechanic Name", lang),
                        value = mechanicName
                    )

                    LoggingTextFieldReadOnly(
                        label = Loc.get("Workshop Name", lang),
                        value = workshopName
                    )

                    LoggingTextField(
                        label = Loc.get("City *", lang),
                        value = workshopCity,
                        onValueChange = { workshopCity = it },
                        placeholder = "e.g. Faridabad",
                        themeColor = themeColor
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = Loc.get("GPS Location", lang) + " *",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF475569)
                        )
                        OutlinedTextField(
                            value = gpsString,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { viewModel.captureGpsAndTimestamp() }) {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = "Capture GPS",
                                        tint = themeColor
                                    )
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedTextColor = if (gpsCaptured) Color.Black else Color.Gray,
                                unfocusedTextColor = if (gpsCaptured) Color.Black else Color.Gray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFF3B82F6), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = Loc.get("Location captured automatically", lang),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8),
                            fontSize = 13.sp
                        )
                        Text(
                            text = Loc.get("Please ensure you are at the workshop location.", lang),
                            color = Color(0xFF1E40AF),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoggingTextFieldReadOnly(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF475569)
        )
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFCBD5E1),
                unfocusedBorderColor = Color(0xFFCBD5E1),
                focusedContainerColor = Color(0xFFF1F5F9), // gray/disabled container
                unfocusedContainerColor = Color(0xFFF1F5F9),
                focusedTextColor = Color(0xFF64748B),
                unfocusedTextColor = Color(0xFF64748B)
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
