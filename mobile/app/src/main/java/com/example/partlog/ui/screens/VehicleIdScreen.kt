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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

private val oems = listOf(
    "Maruti Suzuki",
    "Hyundai",
    "Tata Motors",
    "Mahindra",
    "Toyota",
    "Honda",
    "Kia",
    "Renault",
    "Nissan",
    "Skoda",
    "Volkswagen",
    "Other"
)

private val oemModels = mapOf(
    "Maruti Suzuki" to listOf("Swift", "Baleno", "WagonR", "Brezza", "Dzire", "Alto", "Ertiga", "Celerio", "Eeco", "Grand Vitara", "Ignis", "S-Presso", "XL6", "Fronx", "Jimny"),
    "Hyundai" to listOf("Creta", "i20", "Venue", "i10 Grand", "Verna", "Aura", "Exter", "Alcazar", "Tucson"),
    "Tata Motors" to listOf("Nexon", "Punch", "Altroz", "Tiago", "Tigor", "Harrier", "Safari"),
    "Mahindra" to listOf("Scorpio", "Thar", "XUV700", "XUV300", "Bolero", "XUV400"),
    "Toyota" to listOf("Fortuner", "Innova Crysta", "Innova Hycross", "Glanza", "Urban Cruiser Hyryder", "Rumion"),
    "Honda" to listOf("City", "Amaze", "Elevate", "Jazz", "WR-V"),
    "Kia" to listOf("Seltos", "Sonet", "Carens", "Carnival"),
    "Renault" to listOf("Kwid", "Triber", "Kiger"),
    "Nissan" to listOf("Magnite"),
    "Skoda" to listOf("Kushaq", "Slavia", "Rapid", "Octavia", "Superb"),
    "Volkswagen" to listOf("Taigun", "Virtus", "Polo", "Vento")
)

private val fuelTypes = listOf("Petrol", "Diesel", "CNG", "Electric")
private val yearsList = (2018..2026).map { it.toString() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleIdScreen(
    viewModel: JobViewModel,
    onNext: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language
    val context = LocalContext.current
    val isCompressor = viewModel.componentType.value == "compressor"
    val themeColor = if (isCompressor) Color(0xFF0D4B46) else Color(0xFF023F97)

    var make by viewModel.make
    var model by viewModel.model
    var variant by viewModel.variant
    var year by viewModel.year
    var regNo by viewModel.registrationNumber
    var fuelType by viewModel.fuelType
    var odometer by viewModel.odometer
    var vinNumber by remember { mutableStateOf("") }

    val currentMileageVal by viewModel.currentMileage
    val brandInstalledVal by viewModel.brandInstalled
    val buttonEnabled = if (isCompressor) {
        make.isNotBlank() && model.isNotBlank() && year != 0 && brandInstalledVal.isNotBlank() && currentMileageVal.isNotBlank() && odometer.isNotBlank()
    } else {
        make.isNotBlank() && model.isNotBlank() && year != 0 && fuelType.isNotBlank() && odometer.isNotBlank()
    }

    val modelsForSelectedOem = oemModels[make] ?: emptyList()

    Scaffold(
        topBar = {
            LoggingFlowHeader(
                currentStep = 1,
                totalSteps = if (isCompressor) 3 else 5,
                title = if (isCompressor) Loc.get("Log Compressor", lang) else Loc.get("Log Condenser", lang),
                themeColor = themeColor,
                onBack = onCancel
            )
        },
        bottomBar = {
            FlowNextButton(
                text = Loc.get("next", lang),
                enabled = buttonEnabled,
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
                        text = if (isCompressor) Loc.get("Compressor Details", lang) else Loc.get("Vehicle Details", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    // 1. Car Model (Manufacturer + Model dropdown)
                    LoggingDropdownField(
                        label = Loc.get("Manufacturer", lang) + " *",
                        value = make,
                        options = oems,
                        themeColor = themeColor,
                        onOptionSelected = { selectedMake ->
                            make = selectedMake
                            model = ""
                        }
                    )

                    if (make != "Other" && modelsForSelectedOem.isNotEmpty()) {
                        LoggingDropdownField(
                            label = Loc.get("Model", lang) + " *",
                            value = model,
                            options = modelsForSelectedOem,
                            themeColor = themeColor,
                            onOptionSelected = { selectedModel ->
                                model = selectedModel
                            }
                        )
                    } else {
                        LoggingTextField(
                            label = Loc.get("Model", lang) + " *",
                            value = model,
                            onValueChange = { model = it },
                            placeholder = "Enter model name",
                            themeColor = themeColor
                        )
                    }

                    if (isCompressor) {
                        // 2. Make of Compressor
                        val compressorBrands = listOf("Subros", "Behr", "Valeo", "Delphi", "Sanden", "Other")
                        var brandInstalledState by viewModel.brandInstalled
                        LoggingDropdownField(
                            label = Loc.get("Make of Compressor *", lang),
                            value = brandInstalledState,
                            options = compressorBrands,
                            themeColor = themeColor,
                            onOptionSelected = { brandInstalledState = it }
                        )
                    }

                    // 3. Year of manufacture
                    LoggingDropdownField(
                        label = Loc.get("Model Year", lang) + " *",
                        value = if (year == 0) "" else year.toString(),
                        options = yearsList,
                        themeColor = themeColor,
                        onOptionSelected = { selectedYear ->
                            year = selectedYear.toIntOrNull() ?: 0
                        }
                    )

                    if (!isCompressor) {
                        LoggingDropdownField(
                            label = Loc.get("Fuel Type", lang) + " *",
                            value = fuelType,
                            options = fuelTypes,
                            themeColor = themeColor,
                            onOptionSelected = { selectedFuel ->
                                fuelType = selectedFuel
                            }
                        )

                        LoggingTextField(
                            label = Loc.get("Vehicle VIN (Optional)", lang),
                            value = vinNumber,
                            onValueChange = { vinNumber = it },
                            placeholder = "Enter VIN number",
                            themeColor = themeColor
                        )

                        LoggingTextField(
                            label = Loc.get("reg_no", lang) + " (Optional)",
                            value = regNo,
                            onValueChange = { regNo = it.uppercase() },
                            placeholder = "E.g. MH-12-KJ-5678",
                            themeColor = themeColor
                        )
                    }

                    if (isCompressor) {
                        // 4. Current Mileage
                        var currentMileageState by viewModel.currentMileage
                        LoggingTextField(
                            label = Loc.get("Current Mileage *", lang),
                            value = currentMileageState,
                            onValueChange = { currentMileageState = it },
                            placeholder = "e.g. 15 kmpl",
                            themeColor = themeColor
                        )
                    }

                    // 5. Odometer
                    LoggingTextField(
                        label = Loc.get("odometer", lang) + " *",
                        value = odometer,
                        onValueChange = { odometer = it },
                        placeholder = Loc.get("odometer", lang),
                        themeColor = themeColor,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }
    }
}

@Composable
fun LoggingFlowHeader(
    currentStep: Int,
    totalSteps: Int,
    title: String,
    themeColor: Color,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .background(themeColor)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.Red, shape = CircleShape)
                        .align(Alignment.TopEnd)
                        .offset(x = (-2).dp, y = 2.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                for (i in 1..totalSteps) {
                    if (i > 1) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .background(if (i <= currentStep) themeColor else Color(0xFFE2E8F0))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = if (i <= currentStep) themeColor else Color.White,
                                shape = CircleShape
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (i <= currentStep) themeColor else Color(0xFFCBD5E1),
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Step $currentStep of $totalSteps",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoggingDropdownField(
    label: String,
    value: String,
    options: List<String>,
    themeColor: Color,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasOther = options.any { it.equals("Other", ignoreCase = true) }
    val displayOptions = if (hasOther) options else options + "Other"

    var expanded by remember { mutableStateOf(false) }

    var isOtherSelected by remember(options) {
        mutableStateOf(
            value.isNotEmpty() && !options.any { it.equals(value, ignoreCase = true) && !it.equals("Other", ignoreCase = true) }
        )
    }

    LaunchedEffect(value) {
        val isInOriginalOptions = options.any { it.equals(value, ignoreCase = true) && !it.equals("Other", ignoreCase = true) }
        if (isInOriginalOptions) {
            isOtherSelected = false
        } else if (value.equals("Other", ignoreCase = true)) {
            isOtherSelected = true
        }
    }

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
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = if (isOtherSelected) "Other" else value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = themeColor
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeColor,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White)
            ) {
                displayOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 14.sp) },
                        onClick = {
                            if (option.equals("Other", ignoreCase = true)) {
                                isOtherSelected = true
                                onOptionSelected("Other")
                            } else {
                                isOtherSelected = false
                                onOptionSelected(option)
                            }
                            expanded = false
                        }
                    )
                }
            }
        }

        if (isOtherSelected) {
            val customTextValue = if (value.equals("Other", ignoreCase = true)) "" else value
            val isNumberInput = label.contains("Year", ignoreCase = true)
            
            LoggingTextField(
                label = "Specify Custom " + label.replace("*", "").trim(),
                value = customTextValue,
                onValueChange = { newValue ->
                    onOptionSelected(newValue)
                },
                placeholder = "Enter custom " + label.replace("*", "").trim().lowercase(),
                themeColor = themeColor,
                keyboardOptions = if (isNumberInput) {
                    KeyboardOptions(keyboardType = KeyboardType.Number)
                } else {
                    KeyboardOptions.Default
                }
            )
        }
    }
}

@Composable
fun LoggingTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    themeColor: Color,
    modifier: Modifier = Modifier,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
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
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.Gray) },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = themeColor,
                unfocusedBorderColor = Color(0xFFCBD5E1),
                focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedContainerColor = Color(0xFFF8FAFC),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            keyboardOptions = keyboardOptions,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
