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
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Shield
import com.example.partlog.R
import com.example.partlog.ui.JobViewModel
import com.example.partlog.ui.Loc
import com.msg91.sendotp.OTPWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: JobViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language
    val context = LocalContext.current

    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val widgetId = "366965655632323634373032"
    val tokenAuth = "567821TECBH4N2ju6a9bada7P1"

    val coroutineScope = rememberCoroutineScope()
    var otpSent by remember { mutableStateOf(false) }
    var reqId by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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
                        onLoginSuccess()
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

    fun handleRetryOTP(channel: Number) {
        if (reqId.isEmpty()) {
            Toast.makeText(context, Loc.get("No active request ID to retry", lang), Toast.LENGTH_SHORT).show()
            return
        }
        loading = true
        coroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    OTPWidget.retryOTP(widgetId, tokenAuth, reqId, channel)
                }
                val jsonObject = JSONObject(result)
                val responseType = jsonObject.optString("type")
                val message = jsonObject.optString("message")

                if (responseType != "error") {
                    Toast.makeText(context, "${Loc.get("OTP resent via channel", lang)} $channel", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "${Loc.get("Retry failed", lang)}: $message", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                loading = false
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

                if (responseType != "error") {
                    viewModel.loginMechanicOtp(
                        accessToken = message,
                        mobile = mobileNumber,
                        onSuccess = {
                            loading = false
                            Toast.makeText(context, Loc.get("Login successful", lang), Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        },
                        onFailure = { error ->
                            loading = false
                            Toast.makeText(context, "${Loc.get("Login failed", lang)}: $error", Toast.LENGTH_LONG).show()
                        }
                    )
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
            // Language selection / Toggle row
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

            // Welcome message and Circular cropped illustration
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1.5f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = Loc.get("Welcome Back!", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = BrandColors.TextPrimary
                    )
                    Text(
                        text = Loc.get("Let's collect data, drive quality & earn more.", lang),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = BrandColors.RoyalBlue,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = Loc.get("Login to continue and submit accurate compressor & condenser data.", lang),
                        fontSize = 12.sp,
                        color = BrandColors.TextSecondary,
                        lineHeight = 16.sp
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
                        contentDescription = "Welcome Illustration",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.CenterEnd,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // White login card
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (!otpSent) Loc.get("LOGIN", lang) else Loc.get("VERIFY MOBILE", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF023F97),
                        letterSpacing = 0.5.sp
                    )

                    if (!otpSent) {
                        // Mobile Number Input
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
                            placeholder = { Text(Loc.get("Enter registered number", lang), color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                disabledContainerColor = Color(0xFFE2E8F0),
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent,
                                focusedLabelColor = Color(0xFF023F97)
                            ),
                            enabled = !loading
                        )

                        // Password Input
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
                            placeholder = { Text(Loc.get("Password", lang), color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                disabledContainerColor = Color(0xFFE2E8F0),
                                focusedBorderColor = Color(0xFF023F97),
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent
                            ),
                            enabled = !loading
                        )

                        // Forgot password link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { Toast.makeText(context, Loc.get("Use OTP login instead of password if forgotten.", lang), Toast.LENGTH_SHORT).show() },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text(Loc.get("Forgot Password?", lang), color = Color(0xFF023F97), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Log In Button
                        Button(
                            onClick = {
                                if (mobileNumber.isBlank() || password.isBlank()) {
                                    Toast.makeText(context, Loc.get("Please fill in all fields", lang), Toast.LENGTH_SHORT).show()
                                } else {
                                    loading = true
                                    viewModel.loginMechanic(
                                        id = mobileNumber,
                                        password = password,
                                        onSuccess = {
                                            loading = false
                                            Toast.makeText(context, Loc.get("Login successful", lang), Toast.LENGTH_SHORT).show()
                                            onLoginSuccess()
                                        },
                                        onFailure = { error ->
                                            loading = false
                                            Toast.makeText(context, "${Loc.get("Login failed", lang)}: $error", Toast.LENGTH_LONG).show()
                                        }
                                    )
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
                                        painter = painterResource(id = R.drawable.ic_login),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(Loc.get("LOGIN", lang), fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // OR divider
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE2E8F0)))
                            Text(
                                text = Loc.get("or", lang),
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE2E8F0)))
                        }

                        // Login with OTP
                        OutlinedButton(
                            onClick = { handleSendOTP() },
                            enabled = !loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            border = BorderStroke(1.dp, Color(0xFF023F97)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF023F97))
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = null,
                                    tint = Color(0xFF023F97),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(Loc.get("LOGIN WITH OTP", lang), fontSize = 15.sp, color = Color(0xFF023F97), fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // OTP sent view
                        Text(
                            text = "${Loc.get("OTP sent to", lang)} $mobileNumber",
                            fontSize = 13.sp,
                            color = Color(0xFF6B6B6B),
                            textAlign = TextAlign.Center
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
                                Text(Loc.get("VERIFY & LOGIN", lang), fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(onClick = { handleRetryOTP(11) }, enabled = !loading) {
                                Text(Loc.get("Resend SMS", lang), color = Color(0xFF023F97), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            TextButton(onClick = { handleRetryOTP(12) }, enabled = !loading) {
                                Text(Loc.get("Resend WhatsApp", lang), color = Color(0xFF023F97), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        TextButton(onClick = { otpSent = false }, enabled = !loading, modifier = Modifier.fillMaxWidth()) {
                            Text(Loc.get("Back to Password Login", lang), color = Color(0xFF6B6B6B), fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Navigate to Register / SignUp
                    TextButton(
                        onClick = onNavigateToSignUp,
                        enabled = !loading,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = Loc.get("Don't have an account? Register Now", lang),
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
