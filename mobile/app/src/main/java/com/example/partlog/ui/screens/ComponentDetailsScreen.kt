package com.example.partlog.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partlog.R
import com.example.partlog.ui.AppLanguage
import com.example.partlog.ui.JobViewModel
import com.example.partlog.ui.Loc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentDetailsScreen(
    viewModel: JobViewModel,
    onNext: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language
    val context = LocalContext.current
    val isCompressor = viewModel.componentType.value == "compressor"
    val themeColor = if (isCompressor) Color(0xFF0D4B46) else Color(0xFF023F97)

    var condenserCondition by viewModel.condenserCondition
    var condenserReplacement by viewModel.condenserReplacement
    var condenserReplacementCount by viewModel.condenserReplacementCount
    var brandInstalled by viewModel.brandInstalled
    var compressorFailureType by viewModel.compressorFailureType
    var compressorOilPresent by viewModel.compressorOilPresent

    val conditions = listOf("Leak", "Clogged", "Damaged", "Other")
    val replacements = listOf("Aftermarket", "OEM")
    val brands = if (isCompressor) {
        listOf("Subros", "Behr", "Valeo", "Delphi", "Sanden", "Other")
    } else {
        listOf("Pranav", "Subros", "Behr", "Valeo", "Delphi", "Other")
    }
    val failures = listOf("Seized", "Leak", "Noisy", "Clutch Failure", "Control Valve", "Other")
    val oilOptions = listOf("Yes", "No")

    Scaffold(
        topBar = {
            LoggingFlowHeader(
                currentStep = 2,
                totalSteps = 5,
                title = if (isCompressor) Loc.get("Log Compressor", lang) else Loc.get("Log Condenser", lang),
                themeColor = themeColor,
                onBack = onCancel
            )
        },
        bottomBar = {
            FlowNextButton(
                text = Loc.get("next", lang),
                enabled = true,
                themeColor = themeColor,
                onClick = {
                    val isValid = if (isCompressor) {
                        compressorFailureType.isNotBlank() && compressorOilPresent.isNotBlank() && brandInstalled.isNotBlank()
                    } else {
                        condenserCondition.isNotBlank() && condenserReplacement.isNotBlank() && brandInstalled.isNotBlank() && condenserReplacementCount.isNotBlank()
                    }
                    if (!isValid) {
                        Toast.makeText(context, Loc.get("fill_fields_error", lang), Toast.LENGTH_SHORT).show()
                    } else {
                        onNext()
                    }
                }
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
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isCompressor) Loc.get("Compressor Details", lang) else Loc.get("Condenser Details", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    // Pedestal component view
                    Box(
                        modifier = Modifier.size(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            (if (isCompressor) Color(0xFF00FFCC) else Color(0xFF3399FF)).copy(alpha = 0.25f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )
                        Image(
                            painter = painterResource(id = R.drawable.hero_illustration),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            alignment = if (isCompressor) Alignment.CenterEnd else Alignment.TopCenter,
                            modifier = Modifier.size(105.dp)
                        )
                    }

                    if (!isCompressor) {
                        LoggingDropdownField(
                            label = Loc.get("Condition *", lang),
                            value = condenserCondition,
                            options = conditions,
                            themeColor = themeColor,
                            onOptionSelected = { condenserCondition = it }
                        )

                        LoggingDropdownField(
                            label = Loc.get("Replacement? *", lang),
                            value = condenserReplacement,
                            options = replacements,
                            themeColor = themeColor,
                            onOptionSelected = { condenserReplacement = it }
                        )

                        LoggingDropdownField(
                            label = Loc.get("Replacement Count *", lang),
                            value = condenserReplacementCount,
                            options = listOf("1st Time", "2nd Time", "3rd Time or more"),
                            themeColor = themeColor,
                            onOptionSelected = { condenserReplacementCount = it }
                        )
                    } else {
                        LoggingDropdownField(
                            label = Loc.get("Failure Type *", lang),
                            value = compressorFailureType,
                            options = failures,
                            themeColor = themeColor,
                            onOptionSelected = { compressorFailureType = it }
                        )

                        LoggingDropdownField(
                            label = Loc.get("Oil Present? *", lang),
                            value = compressorOilPresent,
                            options = oilOptions,
                            themeColor = themeColor,
                            onOptionSelected = { compressorOilPresent = it }
                        )
                    }

                    LoggingDropdownField(
                        label = Loc.get("Brand Installed *", lang),
                        value = brandInstalled,
                        options = brands,
                        themeColor = themeColor,
                        onOptionSelected = { brandInstalled = it }
                    )
                }
            }

            // Eligible ribbon banner
            val isEligible = (!isCompressor && brandInstalled == "Pranav") || (isCompressor && brandInstalled == "Sanden")
            if (isEligible) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
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
                                .background(Color(0xFF22C55E), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = Loc.get("Eligible for Incentive", lang),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isCompressor) {
                                    Loc.get("Extra incentive applicable for using Sanden Compressor", lang)
                                } else {
                                    Loc.get("Extra incentive applicable for using Pranav Condenser", lang)
                                },
                                color = Color(0xFF166534),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
