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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FailureCauseScreen(
    viewModel: JobViewModel,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language
    val context = LocalContext.current
    
    val isCompressor = viewModel.componentType.value == "compressor"
    val themeColor = if (isCompressor) Color(0xFF0D4B46) else Color(0xFF023F97)
    var confirmed by remember { mutableStateOf(false) }

    // Retrieve values for review
    val make = viewModel.make.value
    val model = viewModel.model.value
    val year = viewModel.year.value
    
    val condenserCondition = viewModel.condenserCondition.value
    val condenserReplacement = viewModel.condenserReplacement.value
    val brandInstalled = viewModel.brandInstalled.value
    
    val compressorFailureType = viewModel.compressorFailureType.value
    val compressorOilPresent = viewModel.compressorOilPresent.value
    
    val highSide = viewModel.highSidePressure.value
    val lowSide = viewModel.lowSidePressure.value
    val ambient = viewModel.ambientTemperature.value
    val cooling = viewModel.coolingTemperature.value

    val city = viewModel.workshopCity.value
    val workshopName = viewModel.mechanicWorkshop.value.ifBlank { "Kumar Car AC Service" }
    val mechanicName = viewModel.mechanicName.value.ifBlank { "Abhishek Kumar" }

    // Calculate uploaded images count
    val photosCount = listOf(
        viewModel.photoPath1.value,
        viewModel.photoPath2.value,
        viewModel.photoPath3.value,
        viewModel.photoPath4.value,
        viewModel.photoPath5.value,
        viewModel.photoPath6.value
    ).count { it != null }

    Scaffold(
        topBar = {
            LoggingFlowHeader(
                currentStep = if (isCompressor) 3 else 5,
                totalSteps = if (isCompressor) 3 else 5,
                title = if (isCompressor) Loc.get("Log Compressor", lang) else Loc.get("Log Condenser", lang),
                themeColor = themeColor,
                onBack = onCancel
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(Color(0xFFF8FAFC))
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onSubmit,
                    enabled = confirmed,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColor,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (isCompressor) {
                            Loc.get("Submit Compressor Report", lang)
                        } else {
                            Loc.get("Submit Condenser Report", lang)
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = Loc.get("Your data is safe and used to improve quality.", lang),
                        color = Color(0xFF1E40AF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
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
            Text(
                text = Loc.get("Review & Submit", lang),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF0F172A)
            )

            // Review Details Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isCompressor) {
                        ReviewRow(
                            label = Loc.get("Car Model", lang),
                            value = "$make $model"
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ReviewRow(
                            label = Loc.get("Make of Compressor", lang),
                            value = brandInstalled,
                            trailingBadge = if (brandInstalled == "Sanden") {
                                {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = Loc.get("Eligible for Incentive", lang),
                                            color = Color(0xFF15803D),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else null
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ReviewRow(
                            label = Loc.get("Year of Manufacture", lang),
                            value = year.toString()
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        val currentMileage = viewModel.currentMileage.value
                        ReviewRow(
                            label = Loc.get("Current Mileage", lang),
                            value = currentMileage
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        val odometerVal = viewModel.odometer.value
                        ReviewRow(
                            label = Loc.get("Odometer", lang),
                            value = "$odometerVal km"
                        )
                    } else {
                        ReviewRow(
                            label = Loc.get("Vehicle", lang),
                            value = "$make, $model, $year"
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ReviewRow(
                            label = Loc.get("Condition", lang),
                            value = condenserCondition
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ReviewRow(
                            label = Loc.get("Replacement?", lang),
                            value = condenserReplacement
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ReviewRow(
                            label = Loc.get("Brand Installed", lang),
                            value = brandInstalled,
                            trailingBadge = if (brandInstalled == "Pranav") {
                                {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = Loc.get("Eligible for Incentive", lang),
                                            color = Color(0xFF15803D),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else null
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ReviewRow(
                            label = Loc.get("Workshop", lang),
                            value = "$workshopName, $city"
                        )
                    }
                }
            }

            // Confirmation Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = confirmed,
                    onCheckedChange = { confirmed = it },
                    colors = CheckboxDefaults.colors(checkedColor = themeColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Loc.get("I confirm all details are correct", lang),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.clickable { confirmed = !confirmed }
                )
            }
        }
    }
}

@Composable
fun ReviewRow(
    label: String,
    value: String,
    trailingBadge: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = value,
                color = Color(0xFF0F172A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            trailingBadge?.invoke()
        }
    }
}
