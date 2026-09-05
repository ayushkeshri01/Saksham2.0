package com.example.partlog.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.ArrowDropDown
import com.example.partlog.R
import com.example.partlog.ui.JobViewModel
import com.msg91.sendotp.OTPWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.example.partlog.ui.Loc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: JobViewModel,
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val widgetId = "366965676331323632333333"
    val tokenAuth = "567821TECBH4N2ju6a9bada7P1"
    val coroutineScope = rememberCoroutineScope()
    var otpSent by remember { mutableStateOf(false) }
    var reqId by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var showAlreadyRegisteredDialog by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    fun registerBackend() {
        loading = true
        viewModel.registerMechanic(
            id = mobileNumber,
            name = name,
            workshop = shopName,
            mobile = mobileNumber,
            password = password,
            dob = dob,
            city = city,
            onSuccess = {
                loading = false
                Toast.makeText(context, Loc.get("Registration successful! Please login.", lang), Toast.LENGTH_LONG).show()
                onSignUpSuccess()
            },
            onFailure = { error ->
                loading = false
                Toast.makeText(context, "${Loc.get("Registration failed", lang)}: $error", Toast.LENGTH_LONG).show()
            }
        )
    }

    fun handleSendOTP() {
        if (mobileNumber.isBlank()) {
            Toast.makeText(context, Loc.get("Please enter mobile number", lang), Toast.LENGTH_SHORT).show()
            return
        }
        val identifier = if (mobileNumber.length == 10) "91$mobileNumber" else mobileNumber
        loading = true
        coroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    OTPWidget.sendOTP(widgetId, tokenAuth, identifier)
                }
                val jsonObject = JSONObject(result)
                val responseType = jsonObject.optString("type")
                val message = jsonObject.optString("message")

                if (responseType != "error") {
                    val invisibleVerified = jsonObject.optString("invisibleVerified")?.let { it == "true" } ?: false
                    if (!invisibleVerified) {
                        reqId = message
                        otpSent = true
                        Toast.makeText(context, Loc.get("OTP sent successfully!", lang), Toast.LENGTH_SHORT).show()
                    } else {
                        reqId = ""
                        Toast.makeText(context, Loc.get("Verified silently!", lang), Toast.LENGTH_SHORT).show()
                        registerBackend()
                    }
                } else {
                    Toast.makeText(context, "${Loc.get("Failed to send OTP", lang)}: $message", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                loading = false
            }
        }
    }

    fun checkUserAndProceed() {
        loading = true
        viewModel.checkMechanicExists(mobileNumber) { exists, error ->
            if (error != null) {
                loading = false
                Toast.makeText(context, "${Loc.get("Failed to check user status", lang)}: $error", Toast.LENGTH_LONG).show()
            } else if (exists) {
                loading = false
                showAlreadyRegisteredDialog = true
            } else {
                handleSendOTP()
            }
        }
    }

    fun handleVerifyOtp() {
        if (otpCode.isBlank()) {
            Toast.makeText(context, Loc.get("Please enter OTP", lang), Toast.LENGTH_SHORT).show()
            return
        }
        loading = true
        coroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    OTPWidget.verifyOTP(widgetId, tokenAuth, reqId, otpCode)
                }
                val jsonObject = JSONObject(result)
                val responseType = jsonObject.optString("type")
                val message = jsonObject.optString("message")

                if (responseType != "error" || message.contains("already verified", ignoreCase = true) || message.contains("already_verified", ignoreCase = true)) {
                    registerBackend()
                } else {
                    loading = false
                    Toast.makeText(context, "${Loc.get("Verification failed", lang)}: $message", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                loading = false
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    if (showAlreadyRegisteredDialog) {
        AlertDialog(
            onDismissRequest = { showAlreadyRegisteredDialog = false },
            title = { Text(Loc.get("Account Already Exists", lang)) },
            text = { Text(Loc.get("This mobile number is already registered. Would you like to login with OTP instead?", lang)) },
            confirmButton = {
                Button(
                    onClick = {
                        showAlreadyRegisteredDialog = false
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF023F97))
                ) {
                    Text(Loc.get("Login with OTP", lang))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAlreadyRegisteredDialog = false }
                ) {
                    Text(Loc.get("Cancel", lang), color = Color(0xFF6B6B6B))
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF5F9FE)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Language selection row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                LanguageToggle(
                    currentLanguage = lang,
                    onLanguageChange = { viewModel.changeLanguage(it) }
                )
            }

            // Logos Header
            BrandLogoHeader(sakshamLogoHeight = 44)

            Spacer(modifier = Modifier.height(16.dp))

            // Slogan and circular illustration
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1.5f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (!otpSent) Loc.get("Create Account", lang) else Loc.get("Verify Mobile", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(3.dp)
                            .background(Color(0xFF023F97), shape = RoundedCornerShape(1.5.dp))
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Text(
                        text = if (!otpSent) Loc.get("Register to start submitting data and earn exciting incentives.", lang)
                               else "${Loc.get("An OTP has been sent to", lang)} $mobileNumber. ${Loc.get("Enter it below to verify and complete registration.", lang)}",
                        fontSize = 11.sp,
                        color = Color(0xFF6B6B6B),
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFFEAF0F8), shape = CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_illustration),
                        contentDescription = "Register Illustration",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.CenterEnd,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // White Register Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (!otpSent) Loc.get("REGISTER", lang) else Loc.get("VERIFY & REGISTER", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF023F97),
                        letterSpacing = 0.5.sp
                    )

                    if (!otpSent) {
                        // Full Name
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF023F97)
                                )
                            },
                            placeholder = { Text(Loc.get("Full Name *", lang), color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color.Transparent
                            ),
                            enabled = !loading
                        )

                        // Mobile Number
                        OutlinedTextField(
                            value = mobileNumber,
                            onValueChange = { mobileNumber = it.trim() },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Phone,
                                    contentDescription = null,
                                    tint = Color(0xFF023F97)
                                )
                            },
                            placeholder = { Text(Loc.get("Mobile Number *", lang), color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color.Transparent
                            ),
                            enabled = !loading
                        )

                        // Email Address (Optional)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it.trim() },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = null,
                                    tint = Color(0xFF023F97)
                                )
                            },
                            placeholder = { Text(Loc.get("Email Address (Optional)", lang), color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color.Transparent
                            ),
                            enabled = !loading
                        )

                        // Shop / Company Name
                        OutlinedTextField(
                            value = shopName,
                            onValueChange = { shopName = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Store,
                                    contentDescription = null,
                                    tint = Color(0xFF023F97)
                                )
                            },
                            placeholder = { Text(Loc.get("Shop / Company Name *", lang), color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color.Transparent
                            ),
                            enabled = !loading
                        )

                        // City Selection / Input
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF023F97)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B)
                                )
                            },
                            placeholder = { Text(Loc.get("City *", lang), color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color.Transparent
                            ),
                            enabled = !loading
                        )

                        // Date of Birth
                        OutlinedTextField(
                            value = dob,
                            onValueChange = { dob = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    tint = Color(0xFF023F97)
                                )
                            },
                            placeholder = { Text(Loc.get("Date of Birth (DD/MM/YYYY) *", lang), color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color.Transparent
                            ),
                            enabled = !loading
                        )

                        // Create Password
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF023F97)
                                )
                            },
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = null, tint = Color(0xFF64748B))
                                }
                            },
                            placeholder = { Text(Loc.get("Create Password *", lang), color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color.Transparent
                            ),
                            enabled = !loading
                        )

                        // Confirm Password
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF023F97)
                                )
                            },
                            trailingIcon = {
                                val image = if (confirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(imageVector = image, contentDescription = null, tint = Color(0xFF64748B))
                                }
                            },
                            placeholder = { Text(Loc.get("Confirm Password *", lang), color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color.Transparent
                            ),
                            enabled = !loading
                        )

                        // Agree to Terms & Conditions Checkbox Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = agreeToTerms,
                                onCheckedChange = { agreeToTerms = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF023F97)),
                                enabled = !loading
                            )
                            Text(
                                text = Loc.get("I agree to the Terms & Conditions and Privacy Policy", lang),
                                fontSize = 12.sp,
                                color = Color(0xFF1A1A1A),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Register Button
                        Button(
                            onClick = {
                                if (name.isBlank() || mobileNumber.isBlank() || shopName.isBlank() || city.isBlank() || dob.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                                    Toast.makeText(context, Loc.get("Please fill in all mandatory fields (*)", lang), Toast.LENGTH_SHORT).show()
                                } else if (password.length < 6) {
                                    Toast.makeText(context, Loc.get("Password must be at least 6 characters", lang), Toast.LENGTH_SHORT).show()
                                } else if (password != confirmPassword) {
                                    Toast.makeText(context, Loc.get("Passwords do not match", lang), Toast.LENGTH_SHORT).show()
                                } else if (!agreeToTerms) {
                                    Toast.makeText(context, Loc.get("You must agree to the Terms & Conditions", lang), Toast.LENGTH_SHORT).show()
                                } else {
                                    checkUserAndProceed()
                                }
                            },
                            enabled = !loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF023F97))
                        ) {
                            if (loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_register),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(Loc.get("REGISTER", lang), fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // OTP code input view when verifying mobile
                        OutlinedTextField(
                            value = mobileNumber,
                            onValueChange = {},
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Phone,
                                    contentDescription = null,
                                    tint = Color(0xFF023F97)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = false
                        )

                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { otpCode = it.trim() },
                            placeholder = { Text(Loc.get("Enter 6-digit OTP", lang), color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color.Transparent
                            ),
                            enabled = !loading
                        )

                        Button(
                            onClick = { handleVerifyOtp() },
                            enabled = !loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF023F97))
                        ) {
                            if (loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(Loc.get("VERIFY & REGISTER", lang), fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        TextButton(onClick = { otpSent = false }, enabled = !loading, modifier = Modifier.fillMaxWidth()) {
                            Text(Loc.get("Back to Edit Details", lang), color = Color(0xFF6B6B6B), fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Already have an account text link
                    TextButton(
                        onClick = onNavigateToLogin,
                        enabled = !loading,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = Loc.get("Already have an account? Login Now", lang),
                            fontSize = 14.sp,
                            color = Color(0xFF023F97),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppFooter(lang = lang)
        }
    }
}
